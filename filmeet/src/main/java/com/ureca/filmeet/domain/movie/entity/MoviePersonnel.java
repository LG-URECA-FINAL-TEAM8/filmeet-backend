package com.ureca.filmeet.domain.movie.entity;

import com.ureca.filmeet.domain.movie.entity.enums.MoviePosition;
import com.ureca.filmeet.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoviePersonnel extends BaseTimeEntity {

    @Id
    @Column(name = "movie_personnel_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    private Personnel personnel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private MoviePosition moviePosition;

    @Column(length = 50)
    private String characterName;
}