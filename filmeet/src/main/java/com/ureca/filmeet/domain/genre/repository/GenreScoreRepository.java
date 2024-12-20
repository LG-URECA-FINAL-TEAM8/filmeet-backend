package com.ureca.filmeet.domain.genre.repository;

import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.GenreScore;
import com.ureca.filmeet.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreScoreRepository extends JpaRepository<GenreScore, Long> {

    @Query(value = "SELECT gs.genre_id " +
            "FROM genre_score gs " +
            "WHERE gs.member_id = :memberId " +
            "ORDER BY gs.score DESC " +
            "LIMIT 10",
            nativeQuery = true)
    List<Long> findTop10GenreIdsByMemberId(
            @Param("memberId") Long memberId
    );

    @Modifying
    @Query("UPDATE GenreScore gs " +
            "SET gs.score = gs.score + " +
            "CASE " +
            "    WHEN gs.score >= 900 THEN CAST(:weight * 0.1 AS integer) " +
            "    WHEN gs.score >= 800 THEN CAST(:weight * 0.2 AS integer) " +
            "    WHEN gs.score >= 700 THEN CAST(:weight * 0.3 AS integer) " +
            "    WHEN gs.score >= 600 THEN CAST(:weight * 0.4 AS integer) " +
            "    WHEN gs.score >= 500 THEN CAST(:weight * 0.5 AS integer) " +
            "    WHEN gs.score >= 400 THEN CAST(:weight * 0.6 AS integer) " +
            "    WHEN gs.score >= 300 THEN CAST(:weight * 0.7 AS integer) " +
            "    WHEN gs.score >= 200 THEN CAST(:weight * 0.8 AS integer) " +
            "    WHEN gs.score >= 100 THEN CAST(:weight * 0.9 AS integer) " +
            "    ELSE :weight " +
            "END " +
            "WHERE gs.genre.id IN :genreIds AND gs.user.id = :userId")
    void bulkUpdateGenreScores(
            @Param("weight") int weight,
            @Param("genreIds") List<Long> genreIds,
            @Param("userId") Long userId
    );


    @Query("SELECT gs FROM GenreScore gs WHERE gs.user = :user AND gs.genre = :genre")
    Optional<GenreScore> findByUserAndGenre(@Param("user") User user, @Param("genre") Genre genre);

    List<GenreScore> findByUser(User user);

    @Query("SELECT gs FROM GenreScore gs " +
            "WHERE gs.user = :user " +
            "ORDER BY gs.score DESC")
    List<GenreScore> findTopGenresByUser(User user);

    @Query("SELECT gs.genre.id " +
            "FROM GenreScore gs " +
            "WHERE gs.user.id IN :userIds " +
            "GROUP BY gs.genre.id " +
            "ORDER BY SUM(gs.score) DESC"
    )
    List<Long> findTopGenresBySimilarUsersIds(
            @Param("userIds") List<Long> userIds,
            Pageable pageable
    );
}