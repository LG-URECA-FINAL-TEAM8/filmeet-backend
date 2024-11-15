package com.ureca.filmeet.domain.movie.entity;

import com.ureca.filmeet.domain.genre.entity.MovieGenre;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private Integer runningTime;

    @Enumerated(EnumType.STRING)
    private AgeLimit ageLimit;

    @OneToMany(mappedBy = "movie")
    private List<MoviePersonnel> moviePersonnel = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private List<MovieGenre> movieGenres = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private List<MovieCountries> movieCountries = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private List<Gallery> galleries = new ArrayList<>();
}

