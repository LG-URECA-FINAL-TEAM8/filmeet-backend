package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.admin.dto.response.AdminMovieResponse;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.genre.repository.MovieGenreRepository;
import com.ureca.filmeet.domain.movie.dto.response.*;
import com.ureca.filmeet.domain.movie.entity.Gallery;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.exception.MovieNotFoundException;
import com.ureca.filmeet.domain.movie.repository.MovieCountriesRepository;
import com.ureca.filmeet.domain.movie.repository.MovieLikesRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRatingsRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieQueryService {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final MovieLikesRepository movieLikesRepository;
    private final MovieRatingsRepository movieRatingsRepository;
    private final MovieCountriesRepository movieCountriesRepository;

    private static List<String> getGalleryImages(Movie movie) {
        return movie.getGalleries()
                .stream()
                .map(Gallery::getImageUrl)
                .toList();
    }

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

    public Movie getMovieById(Long movieId) {
        return movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
    }

    public Page<AdminMovieResponse> getMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "releaseDate"));
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        return moviePage.map(AdminMovieResponse::fromEntity);
    }

    public MovieDetailResponse getMovieDetailV1(Long movieId) {
        Movie movie = movieRepository.findMovieDetailInfo(movieId)
                .orElseThrow(() -> new RuntimeException("no movie"));

        List<String> countries = movie.getMovieCountries()
                .stream()
                .map(mc -> mc.getCountry().getNation())
                .toList();

        List<GenreType> genres = movie.getMovieGenres()
                .stream()
                .map(movieGenre -> movieGenre.getGenre().getGenreType())
                .toList();

        List<PersonnelInfoResponse> personnels = getPersonnelInfoResponses(movie);

        List<String> galleryImages = getGalleryImages(movie);

        return MovieDetailResponse.from(movie, false, null, null, countries, genres, personnels, galleryImages, null,
                null);
    }

    public MovieDetailResponse getMovieDetail(Long movieId, Long userId) {
        Movie movie = movieRepository.findMovieDetailInfo(movieId)
                .orElseThrow(MovieNotFoundException::new);

        boolean isLiked = movieLikesRepository.findMovieLikesBy(movieId, userId).isPresent();

        MyMovieReview myMovieReview = reviewRepository.findReviewBy(movieId, userId)
                .map(MyMovieReview::of)
                .orElse(new MyMovieReview(null, null));

        MyMovieRating myMovieRating = movieRatingsRepository.findMovieRatingBy(movieId, userId)
                .map(MyMovieRating::of)
                .orElse(new MyMovieRating(null, null));

        List<String> countries = movieCountriesRepository.findMovieCountriesByMovieId(movieId)
                .stream()
                .map(movieCountries -> movieCountries.getCountry().getNation())
                .toList();

        List<GenreType> genres = movieGenreRepository.findMovieGenresByMovieId(movieId)
                .stream()
                .map(movieGenre -> movieGenre.getGenre().getGenreType())
                .toList();

        List<PersonnelInfoResponse> personnels = getPersonnelInfoResponses(movie);

        List<String> galleryImages = getGalleryImages(movie);

        List<RatingDistributionResponse> ratingDistribution = movieRatingsRepository.findRatingDistributionByMovieId(
                movieId);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "likeCounts");
        SliceResponseDto<GetMovieReviewsResponse> movieReviewsResponses = SliceResponseDto.of(
                reviewRepository.findMovieReviewsWithLikes(movieId,
                        userId, pageable));

        return MovieDetailResponse.from(
                movie,
                isLiked,
                myMovieReview,
                myMovieRating,
                countries,
                genres,
                personnels,
                galleryImages,
                movieReviewsResponses,
                ratingDistribution
        );
    }

    public Slice<MoviesResponse> getMoviesByGenre(GenreType genreType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findMoviesByGenre(genreType, pageable);
    }
}