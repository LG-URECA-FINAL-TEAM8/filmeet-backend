package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.Movie;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long>, MovieCustomRepository {

    @Query("SELECT m " +
            "FROM Movie m " +
            "WHERE m.id IN :movieIds")
    List<Movie> findMoviesByMovieIds(
            @Param("movieIds") List<Long> movieIds
    );

    @Query("SELECT m FROM Movie m " +
            "WHERE m.releaseDate > :currentDate " +
            "AND m.releaseDate BETWEEN :startDate AND :endDate " +
            "ORDER BY m.releaseDate ASC"
    )
    List<Movie> findUpcomingMoviesByDateRange(
            @Param("currentDate") LocalDate currentDate,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value =
            "(SELECT * FROM movie m WHERE m.like_counts > 0 ORDER BY m.like_counts DESC LIMIT 1000) " +
                    "UNION " +
                    "(SELECT * FROM movie m WHERE m.average_rating > 0 ORDER BY m.average_rating DESC LIMIT 1000)",
            nativeQuery = true)
    List<Movie> findMoviesWithStarRatingAndLikesUnion();

    @Query("SELECT m " +
            "FROM Movie m " +
            "JOIN m.movieGenres mg " +
            "LEFT JOIN Review r ON r.movie.id = m.id AND r.user.id = :userId " +
            "LEFT JOIN MovieLikes ml ON ml.movie.id = m.id AND ml.user.id = :userId " +
            "LEFT JOIN CollectionMovie cm ON cm.movie.id = m.id " +
            "LEFT JOIN Collection c ON c.id = cm.id AND c.user.id = :userId " +
            "WHERE mg.genre.id IN :genreIds " +
            "AND r.id IS NULL " +
            "AND ml.id IS NULL " +
            "AND c.id IS NULL " +
            "AND m.id NOT IN :top10MovieIds")
    List<Movie> findMoviesByPreferredGenresAndNotInteracted(
            @Param("genreIds") List<Long> genreIds,
            @Param("userId") Long userId,
            @Param("top10MovieIds") List<Long> top10MovieIds
    );

    @Query("SELECT m FROM Movie m " +
            "LEFT JOIN FETCH m.movieCountries mc " +
            "JOIN FETCH mc.countries c " +
            "LEFT JOIN FETCH m.moviePersonnels mp " +
            "JOIN FETCH mp.personnel p " +
            "LEFT JOIN FETCH m.movieGenres mg " +
            "JOIN FETCH mg.genre genre " +
            "LEFT JOIN FETCH m.galleries g " +
            "WHERE m.id = :movieId AND m.isDeleted = false ")
    Optional<Movie> findMovieDetailInfoV1(@Param("movieId") Long movieId);

    @Query("SELECT m FROM Movie m " +
            "LEFT JOIN FETCH m.moviePersonnels mp " +
            "JOIN FETCH mp.personnel p " +
            "WHERE m.id = :movieId AND m.isDeleted = false ")
    Optional<Movie> findMovieDetailInfo(@Param("movieId") Long movieId);
}