package com.ureca.filmeet.domain.movie.entity;

import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseEntity {

    @Id
    @Column(name = "movie_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1000)
    private String plot;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private Integer runtime;

    @Column(length = 200)
    private String posterUrl;

    private Integer likeCounts;

    private Integer ratingCounts;

    private BigDecimal averageRating;

    @Enumerated(EnumType.STRING)
    private FilmRatings filmRatings;

    @OneToMany(mappedBy = "movie")
    private List<Gallery> galleries = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private List<MovieCountries> movieCountries = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private List<MoviePersonnel> moviePersonnels = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private List<MovieGenre> movieGenres = new ArrayList<>();

    public void addRatingCounts() {
        this.ratingCounts++;
    }

    public void decrementRatingCounts() {
        if (this.ratingCounts > 0) {
            this.ratingCounts--;
        }
    }

    public void addLikeCounts() {
        this.likeCounts++;
    }

    public void decrementLikeCounts() {
        if (this.likeCounts > 0) {
            this.likeCounts--;
        }
    }

    //===연관 관계 메서드===//
    public void addGalleries(Gallery gallery) {
        galleries.add(gallery);
        gallery.changeMovie(this);
    }

    public void addMovieCountries(MovieCountries movieCountry) {
        movieCountries.add(movieCountry);
        movieCountry.changeMovie(this);
    }

    public void addMoviePersonnels(MoviePersonnel moviePersonnel) {
        moviePersonnels.add(moviePersonnel);
        moviePersonnel.changeMovie(this);
    }

    public void addMovieGenres(MovieGenre movieGenre) {
        movieGenres.add(movieGenre);
        movieGenre.changeMovie(this);
    }

    @Builder
    public Movie(String title, String plot, LocalDate releaseDate,
                 Integer runtime, String posterUrl,
                 Integer likeCounts, Integer ratingCounts,
                 BigDecimal averageRating, FilmRatings filmRatings) {
        this.title = title;
        this.plot = plot;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.posterUrl = posterUrl;
        this.likeCounts = likeCounts;
        this.ratingCounts = ratingCounts;
        this.averageRating = averageRating;
        this.filmRatings = filmRatings;
    }
}