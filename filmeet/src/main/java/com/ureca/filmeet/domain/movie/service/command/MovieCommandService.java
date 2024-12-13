package com.ureca.filmeet.domain.movie.service.command;

import com.ureca.filmeet.domain.admin.dto.request.AddMoviesRequest;
import com.ureca.filmeet.domain.admin.dto.request.UpdateMovieLikeCountRequest;
import com.ureca.filmeet.domain.admin.dto.request.UpdateMovieRequest;
import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.genre.repository.GenreRepository;
import com.ureca.filmeet.domain.genre.repository.MovieGenreRepository;
import com.ureca.filmeet.domain.movie.entity.*;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.entity.enums.MoviePosition;
import com.ureca.filmeet.domain.movie.exception.MovieNotFoundException;
import com.ureca.filmeet.domain.movie.repository.CountriesRepository;
import com.ureca.filmeet.domain.movie.repository.GalleryRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.infra.kmdb.dto.KmdbPlot;
import com.ureca.filmeet.infra.kmdb.dto.KmdbStaff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieCommandService {
    private final MovieRepository movieRepository;
    private final GalleryRepository galleryRepository;
    private final CountriesRepository countriesRepository;
    private final GenreRepository genreRepository;
    private final PersonnelCommandService personnelCommandService;
    private final MovieQueryService movieQueryService;
    private final MovieGenreRepository movieGenreRepository;

    @Transactional
    public void addMovies(List<AddMoviesRequest> requests) {
        // 1. 이미 존재하는 영화 제목 조회
        List<String> duplicatedTitles = movieRepository.findExistingTitlesByTitleIn(
                requests.stream()
                        .map(request -> request.title().replace(" ", ""))
                        .toList()
        );

        // 2. 존재하지 않는 영화만 필터링
        List<AddMoviesRequest> filteredRequests = requests.stream()
                .filter(request -> !duplicatedTitles.contains(request.title().replace(" ", "")))
                .toList();

        filteredRequests.forEach(request -> {
            // 3. Movie 생성
            Movie movie = Movie.builder()
                    .title(request.title().replace(" ", ""))
                    .plot(request.plots().stream().filter(plot -> "한국어".equals(plot.plotLang()))
                            .map(KmdbPlot::plotText).findFirst().orElse("줄거리 없음"))
                    .releaseDate(parseReleaseDate(request.repRlsDate()))
                    .runtime(convertToInteger(request.runtime()))
                    .posterUrl(request.posters().isEmpty() ? null : request.posters().get(0))
                    .filmRatings(parseFilmRatings(request.rating()))
                    .build();

            // 4. Gallery 저장
            saveGalleriesAndAddGalleries(request, movie);

            // 5. Staff 저장
            addPersonnelToMovie(movie, request.staffs());

            // 6. Country 저장
            List<String> nations = Arrays.stream(request.nation().trim().split(",")).toList();
            addMovieCountryToMovie(nations, movie);

            // 7. Genre 저장
            List<GenreType> genreTypes = Arrays.stream(request.genre().trim().split("[,/]"))
                    .map(GenreType::fromName) // GenreType으로 변환
                    .distinct()
                    .toList();
            addMovieGenreToMovie(genreTypes, movie);

            // 8. 영화 저장
            movieRepository.save(movie);
        });
    }

    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
        movie.delete();
    }

    @Transactional
    public void updateLikeCount(Long movieId, UpdateMovieLikeCountRequest request) {
        Movie movie = movieQueryService.getMovieById(movieId);
        Integer updatedNum = request.likeCount();
        movie.updateLikeCounts(updatedNum);
    }

    @Transactional
    public void updateMovie(Long movieId, UpdateMovieRequest request) {
        Movie movie = movieQueryService.getMovieById(movieId);
        movie.updateMovie(request.title(),
                request.posterUrl(),
                request.likeCounts());

        Gallery gallery = new Gallery(movie, request.posterUrl());
        galleryRepository.save(gallery);
        movie.addGallery(gallery);
    }

    private void saveGalleriesAndAddGalleries(AddMoviesRequest request, Movie movie) {
        List<Gallery> newGalleries = request.posters().stream()
                .map(poster -> new Gallery(movie, poster)) // Gallery 객체 생성
                .toList();

        newGalleries.forEach(movie::addGallery);
    }

    private LocalDate parseReleaseDate(String releaseDate) {
        if (releaseDate == null || releaseDate.isBlank()) {
            return LocalDate.of(2010, 10, 10);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(releaseDate, formatter);
    }

    private FilmRatings parseFilmRatings(String rating) {
        return switch (rating.trim()) {
            case "전체관람가" -> FilmRatings.ALL;
            case "12세관람가" -> FilmRatings.TWELVE;
            case "15세관람가" -> FilmRatings.FIFTEEN;
            case "제한상영가" -> FilmRatings.RESTRICTED_RATING;
            default -> rating.contains("18") ? FilmRatings.ADULT : FilmRatings.UNKNOWN;
        };
    }

    private void addPersonnelToMovie(Movie movie, List<KmdbStaff> staffs) {
        staffs.stream()
                .forEach(staff -> {
                    Integer staffId = Integer.parseInt(staff.staffId());

                    // Personnel 생성 또는 조회
                    Personnel personnel = personnelCommandService.findOrCreatePersonnel(
                            staffId,
                            staff.staffNm()
                    );

                    // MoviePersonnel 생성 및 Movie와 연관 설정
                    MoviePosition position = mapToMoviePosition(staff.staffRoleGroup());
                    MoviePersonnel.MoviePersonnelBuilder moviePersonnelBuilder = MoviePersonnel.builder()
                            .personnel(personnel)
                            .movie(movie)
                            .moviePosition(position);
                    if (staff.staffRole() != null && !staff.staffRole().isBlank()) {
                        moviePersonnelBuilder.characterName(staff.staffRole());
                    }
                    movie.addMoviePersonnel(moviePersonnelBuilder.build());
                });
    }

    private void addMovieCountryToMovie(List<String> nations, Movie movie) {
        List<Country> countries = countriesRepository.findByNationIn(nations);

        countries.stream().forEach(country ->
        {
            MovieCountry movieCountry = MovieCountry.builder()
                    .country(country)
                    .movie(movie)
                    .build();
            movie.addMovieCountry(movieCountry);
        });
    }

    private void addMovieGenreToMovie(List<GenreType> genreTypes, Movie movie) {
        List<Genre> genres = genreRepository.findByGenreTypeIn(genreTypes);

        genres.stream().forEach(genre ->
        {
            MovieGenre movieGenre = MovieGenre.builder()
                    .genre(genre)
                    .movie(movie)
                    .build();
            movie.addMovieGenre(movieGenre);
        });
    }


    private MoviePosition mapToMoviePosition(String roleGroup) {
        log.info("roleGroup: {}", roleGroup);
        return switch (roleGroup) {
            case "감독" -> MoviePosition.DIRECTOR;
            case "각본" -> MoviePosition.SCREEN_WRITER;
            default -> MoviePosition.ACTOR; // 배우
        };
    }

    private Integer convertToInteger(String runtime) {
        if (runtime.isBlank() || runtime == null) {
            return 0;
        }
        return Integer.parseInt(runtime);
    }
}
