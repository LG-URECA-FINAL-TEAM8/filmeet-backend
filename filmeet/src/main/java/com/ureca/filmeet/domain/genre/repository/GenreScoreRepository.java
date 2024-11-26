package com.ureca.filmeet.domain.genre.repository;

import com.ureca.filmeet.domain.genre.entity.GenreScore;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreScoreRepository extends JpaRepository<GenreScore, Long> {

    @Query(value = "SELECT gs.genre_id " +
            "FROM genre_score gs " +
            "WHERE gs.member_id = :memberId " +
            "ORDER BY gs.score DESC " +
            "LIMIT 5",
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
}