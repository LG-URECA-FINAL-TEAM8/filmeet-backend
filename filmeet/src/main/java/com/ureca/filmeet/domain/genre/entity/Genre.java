package com.ureca.filmeet.domain.genre.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genreId;

    @Column(length = 50, nullable = false)
    private String title;

    @OneToMany(mappedBy = "genre")
    private List<MovieGenre> movieGenres = new ArrayList<>();

    @OneToMany(mappedBy = "genre")
    private List<GenreScore> genreScores = new ArrayList<>();
}