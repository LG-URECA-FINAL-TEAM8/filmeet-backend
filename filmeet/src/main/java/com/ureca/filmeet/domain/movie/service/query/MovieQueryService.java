package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.genre.repository.MovieGenreRepository;
import com.ureca.filmeet.domain.movie.dto.response.MovieDetailResponse;
import com.ureca.filmeet.domain.movie.dto.response.PersonnelInfoResponse;
import com.ureca.filmeet.domain.movie.dto.response.ReviewInfo;
import com.ureca.filmeet.domain.movie.entity.Gallery;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieCountriesRepository;
import com.ureca.filmeet.domain.movie.repository.MovieLikesRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieQueryService {

    private final MovieRepository movieRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final MovieLikesRepository movieLikesRepository;
    private final MovieCountriesRepository movieCountriesRepository;
    private final ReviewRepository reviewRepository;

    public MovieDetailResponse getMovieDetailV1(Long movieId) {
        Movie movie = movieRepository.findMovieDetailInfo(movieId)
                .orElseThrow(() -> new RuntimeException("no movie"));

        List<String> countries = movie.getMovieCountries()
                .stream()
                .map(mc -> mc.getCountries().getNation())
                .toList();

        List<GenreType> genres = movie.getMovieGenres()
                .stream()
                .map(movieGenre -> movieGenre.getGenre().getGenreType())
                .toList();

        List<PersonnelInfoResponse> personnels = getPersonnelInfoResponses(movie);

        List<String> galleryImages = getGalleryImages(movie);

        return MovieDetailResponse.from(movie, false, null, countries, genres, personnels, galleryImages);
    }

    public MovieDetailResponse getMovieDetail(Long movieId, Long userId) {
        Movie movie = movieRepository.findMovieDetailInfo(movieId)
                .orElseThrow(() -> new RuntimeException("no movie"));

        boolean isLiked = movieLikesRepository.findMovieLikesBy(movieId, userId).isPresent();

        ReviewInfo reviewInfo = reviewRepository.findReviewBy(movieId, userId)
                .map(ReviewInfo::of)
                .orElse(new ReviewInfo(null, null));

        List<String> countries = movieCountriesRepository.findMovieCountriesByMovieId(movieId)
                .stream()
                .map(movieCountries -> movieCountries.getCountries().getNation())
                .toList();

        List<GenreType> genres = movieGenreRepository.findMovieGenresByMovieId(movieId)
                .stream()
                .map(movieGenre -> movieGenre.getGenre().getGenreType())
                .toList();

        List<PersonnelInfoResponse> personnels = getPersonnelInfoResponses(movie);

        List<String> galleryImages = getGalleryImages(movie);

        return MovieDetailResponse.from(
                movie,
                isLiked,
                reviewInfo,
                countries,
                genres,
                personnels,
                galleryImages
        );
    }

    private static List<String> getGalleryImages(Movie movie) {
        return movie.getGalleries()
                .stream()
                .map(Gallery::getImageUrl)
                .toList();
    }

    // 참여자 정보 리스트 변환
    private static List<PersonnelInfoResponse> getPersonnelInfoResponses(Movie movie) {
        return movie.getMoviePersonnels()
                .stream()
                .map(mp -> new PersonnelInfoResponse(
                        mp.getMoviePosition(),
                        mp.getCharacterName(),
                        mp.getPersonnel().getName(),
                        mp.getPersonnel().getProfileImage()
                ))
                .toList();
    }
}