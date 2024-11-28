package com.ureca.filmeet.domain.game.entity;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoundMatch extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private Movie winner;

    @Column(nullable = false)
    private Integer roundNumber; // 현재 라운드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie1_id")
    private Movie movie1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie2_id")
    private Movie movie2;

    @Builder
    private RoundMatch(Game game, User user, Movie movie1, Movie movie2,
                       Integer roundNumber) {
        this.game = game;
        this.user = user;
        this.movie1 = movie1;
        this.movie2 = movie2;
        this.roundNumber = roundNumber;
    }

    public void selectWinner(Movie winner) {
//        validateWinnerSelection(winner);
        this.winner = winner;
    }

    public boolean hasWinner() {
        return winner != null;
    }

//    private void validateWinnerSelection(Movie winner) {
//        if (!winner.equals(movie1) && !winner.equals(movie2)) {
//            throw new Exception(ErrorCode.INVALID_WINNER_SELECTION);
//        }
//        if (this.winner != null) {
//            throw new BusinessException(ErrorCode.WINNER_ALREADY_SELECTED);
//        }
//    }
}
