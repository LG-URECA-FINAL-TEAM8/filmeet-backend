package com.ureca.filmeet.domain.movie.repository;

import static com.ureca.filmeet.global.util.TestUtils.createCountries;
import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovieCountry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.ureca.filmeet.domain.movie.entity.Countries;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieCountries;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class MovieCountriesRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CountryRepository countriesRepository;

    @Autowired
    private MovieCountriesRepository movieCountriesRepository;

    @Test
    @DisplayName("영화 ID로 영화 국가 목록을 성공적으로 조회한다.")
    void findMovieCountriesByMovieId_whenValidMovieId_returnsMovieCountries() {
        // given
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);
        Countries country1 = createCountries("South Korea");
        Countries country2 = createCountries("United States");
        MovieCountries movieCountry1 = createMovieCountry(movie, country1);
        MovieCountries movieCountry2 = createMovieCountry(movie, country2);

        // when
        movieRepository.save(movie);
        countriesRepository.saveAll(List.of(country1, country2));
        movieCountriesRepository.saveAll(List.of(movieCountry1, movieCountry2));
        List<MovieCountries> result = movieCountriesRepository.findMovieCountriesByMovieId(movie.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting("movie.id", "countries.nation")
                .containsExactlyInAnyOrder(
                        tuple(movie.getId(), country1.getNation()),
                        tuple(movie.getId(), country2.getNation())
                );
    }

    @Test
    @DisplayName("존재하지 않는 영화 ID로 조회하면 빈 리스트를 반환한다.")
    void findMovieCountriesByMovieId_whenInvalidMovieId_returnsEmptyList() {
        // given
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);
        Countries country = createCountries("South Korea");
        MovieCountries movieCountry = createMovieCountry(movie, country);

        // when
        movieRepository.save(movie);
        countriesRepository.save(country);
        movieCountriesRepository.save(movieCountry);
        List<MovieCountries> result = movieCountriesRepository.findMovieCountriesByMovieId(999L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("여러 영화가 동일 국가에 매핑된 경우, 해당 영화 ID에 맞는 국가만 반환한다.")
    void findMovieCountriesByMovieId_whenMultipleMovies_returnsCorrectCountries() {
        // given
        Movie movie1 = createMovie("영화 제목1", "영화 줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);
        Movie movie2 = createMovie("영화 제목2", "영화 줄거리2", LocalDate.now(), 140, "https://poster2.url",
                FilmRatings.FIFTEEN);
        Countries country = createCountries("Japan");
        MovieCountries movieCountry1 = createMovieCountry(movie1, country);
        MovieCountries movieCountry2 = createMovieCountry(movie2, country);

        // when
        movieRepository.saveAll(List.of(movie1, movie2));
        countriesRepository.save(country);
        movieCountriesRepository.saveAll(List.of(movieCountry1, movieCountry2));
        List<MovieCountries> result = movieCountriesRepository.findMovieCountriesByMovieId(movie1.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result)
                .extracting("movie.id", "countries.nation")
                .containsExactly(
                        tuple(movie1.getId(), country.getNation())
                );
    }
}