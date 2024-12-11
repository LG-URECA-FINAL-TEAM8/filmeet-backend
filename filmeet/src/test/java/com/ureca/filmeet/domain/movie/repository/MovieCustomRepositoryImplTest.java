package com.ureca.filmeet.domain.movie.repository;

import static com.ureca.filmeet.global.util.TestUtils.createGenre;
import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovieGenre;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.genre.repository.GenreRepository;
import com.ureca.filmeet.domain.genre.repository.MovieGenreRepository;
import com.ureca.filmeet.domain.movie.dto.response.MovieSearchByTitleResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesSearchByGenreResponse;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class MovieCustomRepositoryImplTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieGenreRepository movieGenreRepository;

    @Test
    @DisplayName("특정 장르의 영화 목록을 페이징 처리하여 조회한다.")
    void searchMoviesByGenre_whenValidGenre_returnsPagedMovies() {
        // given
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Movie movie3 = createMovie("영화3", "줄거리3", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Genre genre3 = createGenre(GenreType.COMEDY);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie1, genre2);
        MovieGenre movieGenre3 = createMovieGenre(movie2, genre1);
        MovieGenre movieGenre4 = createMovieGenre(movie3, genre3);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        movieRepository.saveAll(List.of(movie1, movie2, movie3));
        genreRepository.saveAll(List.of(genre1, genre2, genre3));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2, movieGenre3, movieGenre4));
        Slice<MoviesSearchByGenreResponse> result = movieRepository.searchMoviesByGenre(
                List.of(GenreType.ACTION), pageable);

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .extracting("movieId", "title", "genreTypes")
                .containsExactlyInAnyOrder(
                        tuple(movie1.getId(), movie1.getTitle(), List.of(GenreType.ACTION, GenreType.SF)),
                        tuple(movie2.getId(), movie2.getTitle(), List.of(GenreType.ACTION))
                );
    }

    @Test
    @DisplayName("여러 장르 필터로 영화 목록을 페이징 처리하여 검색한다")
    void searchMoviesByGenres_withValidGenres_returnsPagedMovies() {
        // given
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Movie movie3 = createMovie("영화3", "줄거리3", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Movie movie4 = createMovie("영화4", "줄거리4", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Genre genre3 = createGenre(GenreType.COMEDY);
        Genre genre4 = createGenre(GenreType.CRIME);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre2);
        MovieGenre movieGenre2 = createMovieGenre(movie1, genre3);
        MovieGenre movieGenre3 = createMovieGenre(movie2, genre1);
        MovieGenre movieGenre4 = createMovieGenre(movie2, genre2);
        MovieGenre movieGenre5 = createMovieGenre(movie2, genre3);
        MovieGenre movieGenre6 = createMovieGenre(movie3, genre2);
        MovieGenre movieGenre7 = createMovieGenre(movie3, genre3);
        MovieGenre movieGenre8 = createMovieGenre(movie3, genre4);
        MovieGenre movieGenre9 = createMovieGenre(movie4, genre1);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        movieRepository.saveAll(List.of(movie1, movie2, movie3, movie4));
        genreRepository.saveAll(List.of(genre1, genre2, genre3, genre4));
        movieGenreRepository.saveAll(
                List.of(movieGenre1, movieGenre2, movieGenre3, movieGenre4, movieGenre5, movieGenre6, movieGenre7,
                        movieGenre8, movieGenre9));
        Slice<MoviesSearchByGenreResponse> result = movieRepository.searchMoviesByGenre(
                List.of(genre2.getGenreType(), genre3.getGenreType()), pageable);

        // then
        assertThat(result.getContent())
                .hasSize(3)
                .extracting("movieId", "title", "genreTypes")
                .containsExactlyInAnyOrder(
                        tuple(movie1.getId(), movie1.getTitle(), List.of(genre2.getGenreType(), genre3.getGenreType())),
                        tuple(movie2.getId(), movie2.getTitle(),
                                List.of(genre1.getGenreType(), genre2.getGenreType(), genre3.getGenreType())),
                        tuple(movie3.getId(), movie3.getTitle(),
                                List.of(genre2.getGenreType(), genre3.getGenreType(), genre4.getGenreType()))
                );
    }

    @Test
    @DisplayName("검색된 장르가 없으면 빈 결과를 반환한다.")
    void searchMoviesByGenre_whenNoMatchingGenre_returnsEmpty() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Slice<MoviesSearchByGenreResponse> result = movieRepository.searchMoviesByGenre(
                List.of(GenreType.COMEDY), pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("영화 제목으로 검색하여 영화 목록을 페이징 처리하여 조회한다.")
    void searchMoviesByTitle_whenValidTitle_returnsPagedMovies() {
        // given
        Movie movie1 = createMovie("소금", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);
        Movie movie2 = createMovie("금소", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Movie movie3 = createMovie("맛소금", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Movie movie4 = createMovie("맛 소금 그리고", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        movieRepository.saveAll(List.of(movie1, movie2, movie3, movie4));
        Slice<MovieSearchByTitleResponse> result = movieRepository.searchMoviesByTitle("소금", pageable);

        // then
        assertThat(result.getContent())
                .hasSize(3)
                .extracting("title", "movieId")
                .containsExactly(
                        tuple(movie1.getTitle(), movie1.getId()),
                        tuple(movie3.getTitle(), movie3.getId()),
                        tuple(movie4.getTitle(), movie4.getId())
                );
    }

    @Test
    @DisplayName("영화 제목 검색 결과가 없으면 빈 결과를 반환한다.")
    void searchMoviesByTitle_whenNoMatchingTitle_returnsEmpty() {
        // given
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        movieRepository.save(movie1);
        Slice<MovieSearchByTitleResponse> result = movieRepository.searchMoviesByTitle("없는 제목", pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("특정 장르 필터를 사용하지 않고 영화 목록을 페이징 처리하여 조회한다.")
    void findMoviesByGenre_whenNoGenreFilter_returnsAllMovies() {

        // given
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        movieRepository.saveAll(List.of(movie1, movie2));
        Slice<MoviesResponse> result = movieRepository.findMoviesByGenre(null, pageable, 1L);

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .extracting("title", "movieId")
                .containsExactlyInAnyOrder(
                        tuple(movie1.getTitle(), movie1.getId()),
                        tuple(movie2.getTitle(), movie2.getId())
                );
    }

    @Test
    @DisplayName("특정 장르 필터를 사용하여 영화 목록을 페이징 처리하여 조회한다.")
    void findMoviesByGenre_whenGenreFilter_returnsAllMovies() {
        // given
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Movie movie3 = createMovie("영화3", "줄거리3", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Genre genre3 = createGenre(GenreType.COMEDY);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie1, genre2);
        MovieGenre movieGenre3 = createMovieGenre(movie2, genre1);
        MovieGenre movieGenre4 = createMovieGenre(movie3, genre3);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        movieRepository.saveAll(List.of(movie1, movie2, movie3));
        genreRepository.saveAll(List.of(genre1, genre2, genre3));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2, movieGenre3, movieGenre4));
        Slice<MoviesResponse> result = movieRepository.findMoviesByGenre(GenreType.ACTION, pageable, 1L);

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .extracting("title", "movieId")
                .containsExactlyInAnyOrder(
                        tuple(movie1.getTitle(), movie1.getId()),
                        tuple(movie2.getTitle(), movie2.getId())
                );
    }
}