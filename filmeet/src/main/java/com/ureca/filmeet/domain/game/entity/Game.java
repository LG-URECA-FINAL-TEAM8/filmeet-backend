package com.ureca.filmeet.domain.game.entity;

import com.ureca.filmeet.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = true)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status; // 게임 상태 (ACTIVE, INACTIVE)

    @Column(nullable = false)
    private Integer totalRounds; // 총 라운드 수

    @OneToMany(mappedBy = "game")
    private List<GameResult> gameResults = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<RoundMatch> roundMatches = new ArrayList<>();
}
