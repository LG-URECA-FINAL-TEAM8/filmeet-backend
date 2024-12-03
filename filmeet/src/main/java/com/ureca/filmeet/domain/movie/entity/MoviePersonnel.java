package com.ureca.filmeet.domain.movie.entity;

import com.ureca.filmeet.domain.movie.entity.enums.MoviePosition;
import com.ureca.filmeet.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    @Column(length = 40)
    private MoviePosition moviePosition;

    @Column(length = 50)
    private String characterName;

    @Builder
    public MoviePersonnel(Personnel personnel, Movie movie, MoviePosition moviePosition, String characterName) {
        this.personnel = personnel;
        this.movie = movie;
        this.moviePosition = moviePosition;
        this.characterName = characterName;
    }

    public void changeMovie(Movie movie) {
        this.movie = movie;
    }
}