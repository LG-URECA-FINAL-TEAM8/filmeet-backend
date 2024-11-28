package com.ureca.filmeet.domain.game.entity;

import com.ureca.filmeet.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status; // 게임 상태 (ACTIVE, INACTIVE)

    private Integer totalRounds; // 총 라운드 수

    @OneToMany(mappedBy = "game")
    private List<GameResult> gameResults = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<RoundMatch> matches = new ArrayList<>();

    @Builder
    private Game(String title, Integer totalRounds) {
        this.startDate = LocalDateTime.now();
        this.title = title;
        this.totalRounds = totalRounds;
        this.status = GameStatus.ACTIVE;
    }

    public void start() {
        this.startDate = LocalDateTime.now();
    }

    public void end() {
        this.status = GameStatus.INACTIVE;
        this.endDate = LocalDateTime.now();
    }

    public void addMatch(RoundMatch match) {
        this.matches.add(match);
    }

    @Transactional(readOnly = true)
    public boolean isCompleted() {
        return matches.stream()
                .filter(match -> match.getRoundNumber() == 2)  // 결승전
                .anyMatch(RoundMatch::hasWinner);
    }

    public boolean isAbandoned() {
        return LocalDateTime.now().isAfter(
                this.getModifiedAt().plusMinutes(10)
        );
    }
}
