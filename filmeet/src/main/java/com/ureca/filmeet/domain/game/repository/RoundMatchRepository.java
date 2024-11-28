package com.ureca.filmeet.domain.game.repository;

import com.ureca.filmeet.domain.game.entity.RoundMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundMatchRepository extends JpaRepository<RoundMatch, Long> {

    @Query("SELECT rm FROM RoundMatch rm " +
            "WHERE rm.id = :matchId AND rm.user.id = :userId")
    Optional<RoundMatch> findByIdAndUserId(Long matchId, Long userId);

    @Query("SELECT rm FROM RoundMatch rm " +
            "WHERE rm.game.id = :gameId AND rm.roundNumber = :roundNumber")
    List<RoundMatch> findByGameIdAndRoundNumber(Long gameId, Integer roundNumber);

    @Query("DELETE FROM RoundMatch rm WHERE rm.game.id = :gameId")
    void deleteByGameId(Long gameId);
}
