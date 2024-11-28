package com.ureca.filmeet.domain.game.repository;

import com.ureca.filmeet.domain.game.entity.Game;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

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
}
