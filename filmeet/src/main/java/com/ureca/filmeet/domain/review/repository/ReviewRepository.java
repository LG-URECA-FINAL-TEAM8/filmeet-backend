package com.ureca.filmeet.domain.review.repository;

import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse;
import com.ureca.filmeet.domain.review.dto.response.UserReviewsResponse;
import com.ureca.filmeet.domain.review.dto.response.trending.ReviewResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

    @Query("SELECT r " +
            "FROM Review r " +
            "JOIN FETCH r.user u " +
            "WHERE r.movie.id = :movieId AND r.user.id = :userId AND r.isDeleted = false AND r.isVisible = true ")
    Optional<Review> findReviewBy(
            @Param("movieId") Long movieId,
            @Param("userId") Long userId);

    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE r.id = :reviewId AND r.isDeleted = false AND r.isVisible = true ")
    Optional<Review> findReviewBy(
            @Param("reviewId") Long reviewId);

    @Query("SELECT r " +
            "FROM Review r " +
            "JOIN r.movie m " +
            "WHERE r.id = :reviewId " +
            "AND m.id = :movieId " +
            "AND r.isDeleted = false " +
            "AND r.isVisible = true ")
    Optional<Review> findReviewByMovieIdAndReviewId(
            @Param("reviewId") Long reviewId,
            @Param("movieId") Long movieId
    );

    @Query("SELECT r " +
            "FROM Review r " +
            "JOIN r.reviewComments rc " +
            "WHERE r.id = :reviewId " +
            "AND rc.id = :reviewCommentId " +
            "AND r.isDeleted = false " +
            "AND r.isVisible = true ")
    Optional<Review> findReviewByReviewIdAndCommentId(
            @Param("reviewId") Long reviewId,
            @Param("reviewCommentId") Long reviewCommentId);

    @Query("SELECT new com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse( " +
            "       r.id, u.id, r.content, r.likeCounts, r.commentCounts, " +
            "       u.nickname, u.profileImage, " +
            "       CASE WHEN (rl IS NOT NULL) THEN TRUE ELSE FALSE END) " +
            "FROM Review r " +
            "JOIN r.user u " +
            "LEFT JOIN ReviewLikes rl ON rl.review = r AND rl.user.id = :userId " +
            "WHERE r.movie.id = :movieId AND r.isDeleted = false AND r.isVisible = true ")
    Slice<GetMovieReviewsResponse> findMovieReviewsWithLikes(
            @Param("movieId") Long movieId,
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("SELECT r " +
            "FROM Review r " +
            "JOIN FETCH r.user u " +
            "JOIN FETCH r.movie m " +
            "LEFT JOIN FETCH r.reviewComments rc " +
            "LEFT JOIN FETCH rc.user rcu " +
            "WHERE r.isDeleted = false AND r.isVisible = true AND r.id = :reviewId " +
            "ORDER BY rc.createdAt ASC")
    Optional<Review> findMovieReviewDetailBy(@Param("reviewId") Long reviewId);

    @Query("SELECT new com.ureca.filmeet.domain.review.dto.response.trending.ReviewResponse( " +
            "r.id, u.id, m.id, " +
            "r.content, r.likeCounts, r.commentCounts, r.createdAt, " +
            "u.nickname, u.profileImage, m.title, m.posterUrl, " +
            "(SELECT mr.ratingScore " +
            " FROM MovieRatings mr " +
            " WHERE mr.movie.id = r.movie.id AND mr.user.id = u.id), " +
            "CASE WHEN (rl IS NOT NULL) THEN TRUE ELSE FALSE END " +
            ") " +
            "FROM Review r " +
            "JOIN r.movie m " +
            "JOIN r.user u " +
            "LEFT JOIN ReviewLikes rl ON rl.review = r AND rl.user.id = :userId " +
            "WHERE r.isDeleted = false AND r.isVisible = true "
    )
    Slice<ReviewResponse> findTrendingReviewsBy(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT new com.ureca.filmeet.domain.review.dto.response.UserReviewsResponse( " +
            "r.id, u.id, m.id, " +
            "r.content, r.likeCounts, r.commentCounts, " +
            "u.nickname, u.profileImage, m.title, m.posterUrl, m.releaseDate, " +
            "(SELECT mr.ratingScore " +
            " FROM MovieRatings mr " +
            " WHERE mr.movie.id = r.movie.id AND mr.user.id = u.id), " +
            "CASE WHEN (rl IS NOT NULL) THEN TRUE ELSE FALSE END " +
            ") " +
            "FROM Review r " +
            "JOIN r.movie m " +
            "JOIN r.user u " +
            "LEFT JOIN ReviewLikes rl ON rl.review = r AND rl.user.id = :userId " +
            "WHERE u.id = :userId AND r.isDeleted = false AND r.isVisible = true "
    )
    Slice<UserReviewsResponse> findUserReviews(
            @Param("userId") Long userId,
            Pageable pageable
    );

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    @Query("SELECT SUM(r.commentCounts) FROM Review r WHERE r.movie.id = :movieId")
    Integer findTotalCommentCountsByMovieId(@Param("movieId") Long movieId);

    boolean existsByUserIdAndMovieIdAndIsDeletedFalseAndIsVisibleTrue(Long userId, Long movieId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    Integer countByUserId(@Param("userId") Long userId);
}