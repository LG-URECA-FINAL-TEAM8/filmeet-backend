package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.dto.response.MoviesRoundmatchResponse;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.querydsl.MovieCustomRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long>, MovieCustomRepository {

    @Query("""
                SELECT m.title
                FROM Movie m
                WHERE m.title IN :titles
            """)
    List<String> findExistingTitlesByTitleIn(@Param("titles") List<String> titles);

    @Query("SELECT m " +
            "FROM Movie m " +
            "JOIN FETCH m.movieGenres mg " +
            "JOIN FETCH mg.genre g " +
            "WHERE m.id IN :movieIds " +
            "AND m.isDeleted = false")
    List<Movie> findMoviesWithGenreByMovieIds(
            @Param("movieIds") List<Long> movieIds
    );

    @Query("SELECT m " +
            "FROM Movie m " +
            "JOIN FETCH m.movieGenres mg " +
            "JOIN FETCH mg.genre g " +
            "WHERE m.id = :movieId " +
            "AND m.isDeleted = false")
    Optional<Movie> findMovieWithGenreByMovieId(
            @Param("movieId") Long movieId
    );

    @Query("SELECT m " +
            "FROM Movie m " +
            "JOIN Review r " +
            "ON r.movie.id = m.id " +
            "WHERE m.isDeleted = false " +
            "AND m.id = :movieId " +
            "AND r.id = :reviewId")
    Optional<Movie> findMovieByReviewIdAndMovieId(
            @Param("reviewId") Long reviewId,
            @Param("movieId") Long movieId
    );

    @Query("SELECT m FROM Movie m " +
            "WHERE m.isDeleted = false " +
            "AND m.releaseDate > :currentDate ")
    Slice<Movie> findUpcomingMoviesByDateRange(
            @Param("currentDate") LocalDate currentDate,
            Pageable pageable
    );

    @Query(value =
            "(SELECT * FROM movie m WHERE m.like_counts > 0 AND m.is_deleted = false ORDER BY m.like_counts DESC LIMIT 1000) "
                    +
                    "UNION " +
                    "(SELECT * FROM movie m WHERE m.average_rating > 0 AND m.is_deleted = false ORDER BY m.average_rating DESC LIMIT 1000)",
            nativeQuery = true)
    List<Movie> findMoviesWithStarRatingAndLikesUnion();

    @Query(""" 
                    SELECT m
                    FROM Movie m
                    JOIN m.movieGenres mg
                    LEFT JOIN Review r ON r.movie.id = m.id AND r.user.id = :userId
                    LEFT JOIN MovieLikes ml ON ml.movie.id = m.id AND ml.user.id = :userId
                    LEFT JOIN CollectionMovie cm ON cm.movie.id = m.id
                    LEFT JOIN Collection c ON c.id = cm.collection.id AND c.user.id = :userId
                    LEFT JOIN MovieRatings mr ON mr.movie.id = m.id AND mr.user.id = :userId
                    WHERE m.isDeleted = false
                        AND mg.genre.id IN :genreIds
                        AND r.id IS NULL
                        AND ml.id IS NULL
                        AND c.id IS NULL
                        AND mr.id IS NULL
                        AND NOT EXISTS (
                                SELECT 1
                                FROM Movie topMovies
                                WHERE topMovies.id IN :top10MovieIds
                                AND topMovies.id = m.id
                            )
            """)
    List<Movie> findMoviesByPreferredGenresAndNotInteracted(
            @Param("genreIds") List<Long> genreIds,
            @Param("userId") Long userId,
            @Param("top10MovieIds") List<Long> top10MovieIds,
            Pageable pageable
    );

    @Query("SELECT m FROM Movie m " +
            "LEFT JOIN FETCH m.movieCountries mc " +
            "JOIN FETCH mc.country c " +
            "LEFT JOIN FETCH m.moviePersonnels mp " +
            "JOIN FETCH mp.personnel p " +
            "LEFT JOIN FETCH m.movieGenres mg " +
            "JOIN FETCH mg.genre genre " +
            "LEFT JOIN FETCH m.galleries g " +
            "WHERE m.id = :movieId AND m.isDeleted = false ")
    Optional<Movie> findMovieDetailInfoV1(
            @Param("movieId") Long movieId
    );

    @Query("SELECT m FROM Movie m " +
            "LEFT JOIN FETCH m.moviePersonnels mp " +
            "LEFT JOIN FETCH mp.personnel p " +
            "WHERE m.id = :movieId AND m.isDeleted = false ")
    Optional<Movie> findMovieDetailInfo(
            @Param("movieId") Long movieId
    );

    @Query(value =
            "SELECT * FROM movie " +
                    "WHERE movie_id >= (SELECT FLOOR(RAND() * (SELECT MAX(movie_id) FROM movie))) " +
                    "ORDER BY movie_id " +
                    "LIMIT :totalRounds",
            nativeQuery = true)
    List<Movie> findRandomMovies(@Param("totalRounds") Integer totalRounds);

    @Query(value = "SELECT * FROM movie m " +
            "WHERE MATCH(m.title) AGAINST(:search) > 0",
            nativeQuery = true)
    Slice<Movie> findMoviesByTitle(
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN FETCH m.movieGenres mg " +
            "LEFT JOIN FETCH mg.genre " +
            "WHERE m IN :movies")
    List<Movie> findMoviesWithGenres(@Param("movies") List<Movie> movies);

    @Query("""
                SELECT new com.ureca.filmeet.domain.movie.dto.response.MoviesRoundmatchResponse(
                    m.id,
                    m.title,
                    m.posterUrl,
                    m.likeCounts,
                    CAST(COALESCE(SUM(r.commentCounts), 0) AS int),
                    m.ratingCounts
                )
                FROM Movie m
                INNER JOIN MovieGenre mg ON m.id = mg.movie.id
                INNER JOIN MovieGenre targetMg ON targetMg.movie.id = :movieId AND mg.genre.id = targetMg.genre.id
                LEFT JOIN Review r ON r.movie.id = m.id
                WHERE m.id != :movieId
                GROUP BY m.id, m.title, m.posterUrl, m.likeCounts, m.ratingCounts
                ORDER BY COUNT(mg.genre.id) DESC, m.likeCounts DESC
                limit 6
            """)
    List<MoviesRoundmatchResponse> findSimilarMoviesByGenre(@Param("movieId") Long movieId);

    //    @EntityGraph(attributePaths = {"movieGenres.genre"})
    Page<Movie> findAll(Pageable pageable);

    //    @EntityGraph(attributePaths = {"movieGenres.genre"})
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("""
            SELECT m FROM Movie m
            WHERE m.isDeleted = false AND 
            m.id = :id
            """)
    Optional<Movie> findById(Long id);

    @Query(
            "SELECT m FROM Movie m " +
                    "WHERE m.isDeleted = false "
    )
    Slice<Movie> findMoviesBy(
            Pageable pageable
    );

    @Query("SELECT m FROM Movie m WHERE m.title IN :titles")
    List<Movie> findMoviesByTitles(@Param("titles") List<String> titles);
}