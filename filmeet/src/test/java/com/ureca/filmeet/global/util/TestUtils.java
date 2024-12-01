package com.ureca.filmeet.global.util;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionComment;
import com.ureca.filmeet.domain.collection.entity.CollectionLikes;
import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.entity.ReviewComment;
import com.ureca.filmeet.domain.review.entity.ReviewLikes;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class TestUtils {

    public static Movie createMovie(String title, String plot,
                                    LocalDate releaseDate,
                                    Integer runtime, String posterUrl,
                                    FilmRatings filmRatings) {
        return Movie.builder()
                .title(title)
                .plot(plot)
                .releaseDate(releaseDate)
                .runtime(runtime)
                .posterUrl(posterUrl)
                .filmRatings(filmRatings)
                .build();
    }

    public static User createUser(String username, String password, Role role,
                                  Provider provider, String nickname, String profileImage) {
        return User.builder()
                .username(username)
                .password(password)
                .role(role)
                .provider(provider)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }

    public static Review createReview(String content, Movie movie, User user) {
        return Review.builder()
                .content(content)
                .movie(movie)
                .user(user)
                .build();
    }

    public static Review createReview(String content, Movie movie, User user, Integer likeCounts,
                                      Integer commentCounts) {
        return Review.builder()
                .content(content)
                .movie(movie)
                .user(user)
                .likeCounts(likeCounts)
                .commentCounts(commentCounts)
                .build();
    }

    public static ReviewComment createReviewComment(String content, Review review, User user) {
        return ReviewComment.builder()
                .content(content)
                .review(review)
                .user(user)
                .build();
    }

    public static ReviewLikes createReviewLikes(Review review, User user) {
        return ReviewLikes.builder()
                .review(review)
                .user(user)
                .build();
    }

    public static MovieRatings createMovieRatings(Movie movie, User user, BigDecimal ratingScore) {
        return MovieRatings.builder()
                .movie(movie)
                .user(user)
                .ratingScore(ratingScore)
                .build();
    }

    public static Collection createCollection(String tittle, String content, User user) {
        return Collection.builder()
                .title(tittle)
                .content(content)
                .user(user)
                .build();
    }

    public static CollectionComment createCollectionComment(String content, User user, Collection collection) {
        return CollectionComment.builder()
                .content(content)
                .user(user)
                .collection(collection)
                .build();
    }

    public static CollectionLikes createCollectionLikes(User user, Collection collection) {
        return CollectionLikes.builder()
                .user(user)
                .collection(collection)
                .build();
    }

    public static CollectionMovie createCollectionMovie(Movie movie, Collection collection) {
        return CollectionMovie.builder()
                .movie(movie)
                .collection(collection)
                .build();
    }
}