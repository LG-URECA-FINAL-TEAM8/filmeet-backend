package com.ureca.filmeet.domain.movie.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MovieTest {

    @Test
    @DisplayName("영화의 좋아요 수를 증가시킨다.")
    void addLikeCounts_incrementsLikeCounts() {
        // given
        Movie movie = Movie.builder()
                .title("Movie 1")
                .likeCounts(0)
                .build();

        // when
        movie.addLikeCounts();

        // then
        assertThat(movie.getLikeCounts()).isEqualTo(1);
    }

    @Test
    @DisplayName("영화의 좋아요 수를 감소시킨다.")
    void decrementLikeCounts_decrementsLikeCounts() {
        // given
        Movie movie = Movie.builder()
                .title("Movie 1")
                .likeCounts(3)
                .build();

        // when
        movie.decrementLikeCounts();

        // then
        assertThat(movie.getLikeCounts()).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요 수가 0일 때 감소시켜도 0으로 유지된다.")
    void decrementLikeCounts_whenLikeCountsIsZero_staysZero() {
        // given
        Movie movie = Movie.builder()
                .title("Movie 1")
                .likeCounts(0)
                .build();

        // when
        movie.decrementLikeCounts();

        // then
        assertThat(movie.getLikeCounts()).isEqualTo(0);
    }

    @Test
    @DisplayName("영화 평점을 새로 추가한다.")
    void evaluateMovieRating_addsNewRating() {
        // given
        Movie movie = Movie.builder()
                .title("Movie 1")
                .ratingCounts(2)
                .averageRating(BigDecimal.valueOf(3.5))
                .build();

        // when
        movie.evaluateMovieRating(BigDecimal.valueOf(4.0));

        // then
        assertThat(movie.getRatingCounts()).isEqualTo(3);
        assertThat(movie.getAverageRating()).isEqualTo(BigDecimal.valueOf(3.7)); // (3.5*2 + 4.0) / 3
    }

    @Test
    @DisplayName("영화 평점을 수정한다.")
    void modifyMovieRating_updatesRating() {
        // given
        Movie movie = Movie.builder()
                .title("Movie 1")
                .ratingCounts(3)
                .averageRating(BigDecimal.valueOf(4.0))
                .build();
        BigDecimal oldRating = BigDecimal.valueOf(4.0);
        BigDecimal newRating = BigDecimal.valueOf(3.5);

        // when
        movie.modifyMovieRating(oldRating, newRating);

        // then
        assertThat(movie.getAverageRating()).isEqualTo(BigDecimal.valueOf(3.8)); // (4*3 - 4.0 + 3.5) / 3
    }

    @Test
    @DisplayName("영화 평점 삭제 후 평균 평점과 총 평점 수를 업데이트한다.")
    void updateAfterRatingDeletion_updatesRatingAfterDeletion() {
        // given
        Movie movie = Movie.builder()
                .title("Movie 1")
                .ratingCounts(2)
                .averageRating(BigDecimal.valueOf(4.0))
                .build();

        // when
        movie.updateAfterRatingDeletion(BigDecimal.valueOf(4.0));

        // then
        assertThat(movie.getRatingCounts()).isEqualTo(1);
        assertThat(movie.getAverageRating()).isEqualTo(BigDecimal.valueOf(4.0)); // 남은 점수 유지
    }

    @Test
    @DisplayName("평점이 마지막 하나일 경우 삭제 후 평균 평점은 0이다.")
    void updateAfterRatingDeletion_whenLastRatingDeleted_setsAverageRatingToZero() {
        // given
        Movie movie = Movie.builder()
                .title("Movie 1")
                .ratingCounts(1)
                .averageRating(BigDecimal.valueOf(5.0))
                .build();

        // when
        movie.updateAfterRatingDeletion(BigDecimal.valueOf(5.0));

        // then
        assertThat(movie.getRatingCounts()).isEqualTo(0);
        assertThat(movie.getAverageRating()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("영화에 장르를 추가한다.")
    void addMovieGenres_addsGenreToMovie() {
        // given
        Movie movie = Movie.builder()
                .title("Movie 1")
                .build();

        MovieGenre movieGenre = MovieGenre.builder()
                .genre(Genre.builder().genreType(GenreType.ACTION).build())
                .build();

        // when
        movie.addMovieGenres(movieGenre);

        // then
        assertThat(movie.getMovieGenres()).hasSize(1);
        assertThat(movie.getMovieGenres()).contains(movieGenre);
    }

    @Test
    @DisplayName("영화를 빌더로 생성할 때 좋아요 개수, 평점 개수, 평균 별점 기본값을 올바르게 초기화한다.")
    void builder_initializesDefaultValues() {
        // given
        Movie movie = Movie.builder()
                .title("Movie 1")
                .plot("Plot 1")
                .releaseDate(LocalDate.now())
                .runtime(120)
                .posterUrl("poster.url")
                .filmRatings(FilmRatings.ALL)
                .build();

        // then
        assertThat(movie.getLikeCounts()).isEqualTo(0);
        assertThat(movie.getRatingCounts()).isEqualTo(0);
        assertThat(movie.getAverageRating()).isEqualTo(BigDecimal.ZERO);
        assertThat(movie.getMovieGenres()).isEmpty();
    }

    @Test
    @DisplayName("평점 삭제 - 평점 개수 0일 때 예외 발생")
    void updateAfterRatingDeletion_whenNoRatings_throwsException() {
        // given
        Movie movie = Movie.builder()
                .averageRating(BigDecimal.ZERO)
                .ratingCounts(0)
                .build();

        // then
        assertThatThrownBy(() -> movie.updateAfterRatingDeletion(BigDecimal.valueOf(3.0)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("평점을 삭제할 수 없습니다. 별점 개수가 0입니다.");
    }

    @Test
    @DisplayName("평점 삭제 - 삭제할 평점이 null일 때 예외 발생")
    void updateAfterRatingDeletion_whenRatingScoreIsNull_throwsException() {
        // given
        Movie movie = Movie.builder()
                .averageRating(BigDecimal.valueOf(4.0))
                .ratingCounts(2)
                .build();

        // then
        assertThatThrownBy(() -> movie.updateAfterRatingDeletion(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("평점 입력값이 null입니다.");
    }

    @Test
    @DisplayName("평점을 평가할 때 null 값을 전달하면 예외를 발생시킨다.")
    void evaluateMovieRating_whenRatingScoreIsNull_throwsException() {
        // given
        Movie movie = Movie.builder()
                .title("Test Movie")
                .averageRating(BigDecimal.valueOf(4.0))
                .ratingCounts(3)
                .build();

        // when & then
        assertThatThrownBy(() -> movie.evaluateMovieRating(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("평점 입력값이 null입니다.");
    }

    @Test
    @DisplayName("평점을 수정할 때 oldRatingScore가 null이면 예외를 발생시킨다.")
    void modifyMovieRating_whenOldRatingScoreIsNull_throwsException() {
        // given
        Movie movie = Movie.builder()
                .title("Test Movie")
                .averageRating(BigDecimal.valueOf(4.0))
                .ratingCounts(3)
                .build();

        // when & then
        assertThatThrownBy(() -> movie.modifyMovieRating(null, BigDecimal.valueOf(3.5)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("평점 입력값이 null입니다.");
    }

    @Test
    @DisplayName("평점을 수정할 때 newRatingScore가 null이면 예외를 발생시킨다.")
    void modifyMovieRating_whenNewRatingScoreIsNull_throwsException() {
        // given
        Movie movie = Movie.builder()
                .title("Test Movie")
                .averageRating(BigDecimal.valueOf(4.0))
                .ratingCounts(3)
                .build();

        // when & then
        assertThatThrownBy(() -> movie.modifyMovieRating(BigDecimal.valueOf(4.5), null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("평점 입력값이 null입니다.");
    }
}