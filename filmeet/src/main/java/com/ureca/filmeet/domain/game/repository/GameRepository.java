package com.ureca.filmeet.domain.game.repository;

import com.ureca.filmeet.domain.game.dto.response.GameRankingResponse;
import com.ureca.filmeet.domain.game.entity.Game;
import com.ureca.filmeet.domain.game.entity.GameStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g WHERE g.matches IN " +
            "(SELECT rm FROM RoundMatch rm WHERE rm.user.id = :userId)")
    Slice<Game> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT g FROM Game g " +
            "LEFT JOIN FETCH g.matches m " +
            "WHERE g.id = :gameId")
    Optional<Game> findByIdWithMatches(Long gameId);

    List<Game> findByStatus(GameStatus gameStatus);

    @Query(value = """
       SELECT 
           gr.movie_id AS id, 
           m.title AS title, 
           m.poster_url AS posterUrl, 
           COUNT(gr.game_id) AS gameCount, 
           SUM(CASE WHEN gr.game_rank = 1 THEN 1 ELSE 0 END) AS victoryCount,
           ROUND(
               (SUM(CASE WHEN gr.game_rank = 1 THEN 1 ELSE 0 END) * 100.0) / COUNT(gr.game_id), 2
           ) AS victoryRatio,
           (
               SELECT 
                   ROUND((COUNT(rm.id) * 100.0) / COUNT(*), 2)
               FROM round_match rm
               WHERE rm.winner_id = gr.movie_id
           ) AS winRate
       FROM game_result gr
       INNER JOIN movie m ON gr.movie_id = m.movie_id
       GROUP BY gr.movie_id, m.title, m.poster_url
       ORDER BY victoryRatio DESC, winRate DESC
       """, nativeQuery = true)
    List<Object[]> findAllMovieRankings();

}
