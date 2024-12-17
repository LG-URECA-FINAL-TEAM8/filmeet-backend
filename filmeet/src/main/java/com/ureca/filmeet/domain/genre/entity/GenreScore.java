package com.ureca.filmeet.domain.genre.entity;

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
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GenreScore extends BaseTimeEntity {

    @Id
    @Column(name = "genre_score_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Column(nullable = false)
    private Integer score;

    public void addScore(int additionalScore) {
        this.score += additionalScore;
    }

    @Builder
    public GenreScore(User user, Genre genre, Integer score) {
        this.user = user;
        this.genre = genre;
        this.score = score;
    }
}
