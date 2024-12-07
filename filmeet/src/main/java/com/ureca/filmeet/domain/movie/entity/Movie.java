package com.ureca.filmeet.domain.movie.entity;

import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(length = 5000)
    private String plot;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private Integer runtime;

    @Column(length = 200)
    private String posterUrl;

    private Integer likeCounts = 0;

    private Integer ratingCounts = 0;

    private BigDecimal averageRating = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private FilmRatings filmRatings;

    @OneToMany(mappedBy = "movie", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Gallery> galleries = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<MovieCountry> movieCountries = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<MoviePersonnel> moviePersonnels = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<MovieGenre> movieGenres = new ArrayList<>();

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
        this.likeCounts = likeCounts != null ? likeCounts : 0;
        this.ratingCounts = ratingCounts != null ? ratingCounts : 0;
        this.averageRating = averageRating != null ? averageRating : BigDecimal.ZERO;
        this.filmRatings = filmRatings;
    }

    private static void validateRatingScoreNotNull(BigDecimal ratingScoreToDelete) {
        if (ratingScoreToDelete == null) {
            throw new RuntimeException("평점 입력값이 null입니다.");
        }
    }

    public void updateLikeCounts(Integer likeCounts) {
        this.likeCounts = likeCounts;
    }

    public void addLikeCounts() {
        this.likeCounts++;
    }

    public void decrementLikeCounts() {
        if (this.likeCounts > 0) {
            this.likeCounts--;
        }
    }

    public void evaluateMovieRating(BigDecimal ratingScore) {
        validateRatingScoreNotNull(ratingScore);

        BigDecimal totalScore = this.averageRating.multiply(BigDecimal.valueOf(this.ratingCounts))
                .add(ratingScore);

        // 새로운 평균 계산
        this.averageRating = totalScore.divide(BigDecimal.valueOf(this.ratingCounts + 1), 2, RoundingMode.DOWN);

        this.ratingCounts++;
    }

    public void modifyMovieRating(BigDecimal oldRatingScore, BigDecimal newRatingScore) {
        validateRatingScoreNotNull(oldRatingScore);
        validateRatingScoreNotNull(newRatingScore);
        if (this.ratingCounts <= 0) {
            this.averageRating = BigDecimal.ZERO;
            return;
        }

        // 현재 총 점수 계산
        BigDecimal currentTotalScore = this.averageRating.multiply(BigDecimal.valueOf(this.ratingCounts));

        // 기존 별점을 총합에서 빼고 새로운 별점을 추가
        BigDecimal updatedTotalScore = currentTotalScore
                .subtract(oldRatingScore)
                .add(newRatingScore);

        // 새로운 평균 계산
        this.averageRating = updatedTotalScore.divide(BigDecimal.valueOf(this.ratingCounts), 2, RoundingMode.DOWN);
    }

    public void updateAfterRatingDeletion(BigDecimal ratingScoreToDelete) {
        validateRatingCountsNotZero();
        validateRatingScoreNotNull(ratingScoreToDelete);

        // 총합에서 삭제된 평점 제거
        BigDecimal totalScore = this.averageRating.multiply(BigDecimal.valueOf(this.ratingCounts))
                .subtract(ratingScoreToDelete);

        // 평점 수 감소
        this.ratingCounts--;

        // 새로운 평균 계산
        if (this.ratingCounts <= 0) {
            this.averageRating = BigDecimal.ZERO;
        } else {
            this.averageRating = totalScore.divide(BigDecimal.valueOf(this.ratingCounts), 2, RoundingMode.DOWN);
        }
    }

    public void updateMovie(String title, String posterUrl, Integer likeCounts) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.likeCounts = likeCounts;
    }

    public void updateGalleries(List<Gallery> galleries) {
        this.galleries.clear();
        this.galleries.addAll(galleries);
    }

    private void validateRatingCountsNotZero() {
        if (this.ratingCounts <= 0) {
            throw new RuntimeException("평점을 삭제할 수 없습니다. 별점 개수가 0입니다.");
        }
    }

    //===연관 관계 메서드===//
    public void addGallery(Gallery gallery) {
        galleries.add(gallery);
        gallery.changeMovie(this);
    }

    public void addMovieCountry(MovieCountry movieCountry) {
        movieCountries.add(movieCountry);
        movieCountry.changeMovie(this);
    }

    public void addMoviePersonnel(MoviePersonnel moviePersonnel) {
        moviePersonnels.add(moviePersonnel);
        moviePersonnel.changeMovie(this);
    }

    public void addMovieGenre(MovieGenre movieGenre) {
        movieGenres.add(movieGenre);
        movieGenre.changeMovie(this);
    }
}