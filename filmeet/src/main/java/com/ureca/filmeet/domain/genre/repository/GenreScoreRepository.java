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
            "SET gs.score = gs.score + :weight " +
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