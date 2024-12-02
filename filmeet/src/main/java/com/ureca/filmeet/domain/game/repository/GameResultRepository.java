package com.ureca.filmeet.domain.game.repository;

import com.ureca.filmeet.domain.game.entity.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    @Query("SELECT gr FROM GameResult gr " +
            "WHERE gr.game.id = :gameId AND gr.user.id = :userId")
    List<GameResult> findByGameIdAndUserId(Long gameId, Long userId);

    @Query("DELETE FROM GameResult gr WHERE gr.game.id = :gameId")
    void deleteByGameId(Long gameId);
}
