package com.ureca.filmeet.domain.movie.repository;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createCollectionMovie;
import static com.ureca.filmeet.global.util.TestUtils.createGenre;
import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovieGenre;
import static com.ureca.filmeet.global.util.TestUtils.createMovieLikes;
import static com.ureca.filmeet.global.util.TestUtils.createMoviePersonnel;
import static com.ureca.filmeet.global.util.TestUtils.createMovieRatings;
import static com.ureca.filmeet.global.util.TestUtils.createPersonnel;
import static com.ureca.filmeet.global.util.TestUtils.createReview;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
import com.ureca.filmeet.domain.collection.repository.CollectionMovieRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.genre.repository.GenreRepository;
import com.ureca.filmeet.domain.genre.repository.MovieGenreRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import com.ureca.filmeet.domain.movie.entity.MoviePersonnel;
import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import com.ureca.filmeet.domain.movie.entity.Personnel;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.entity.enums.MoviePosition;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieGenreRepository movieGenreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieLikesRepository movieLikesRepository;

    @Autowired
    private MovieRatingsRepository movieRatingsRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionMovieRepository collectionMovieRepository;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private MoviePersonnelRepository moviePersonnelRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("영화 ID 목록으로 해당 영화와 연결된 장르를 페치조인으로 조회한다.")
    void findMoviesWithGenreByMovieIds_whenValidMovieIds_returnsMoviesWithGenres() {
        // given
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Genre genre3 = createGenre(GenreType.COMEDY);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie1, genre2);
        MovieGenre movieGenre3 = createMovieGenre(movie2, genre3);

        // when
        movieRepository.saveAll(List.of(movie1, movie2));
        genreRepository.saveAll(List.of(genre1, genre2, genre3));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2, movieGenre3));
        em.flush();
        em.clear();
        List<Movie> movies = movieRepository.findMoviesWithGenreByMovieIds(List.of(movie1.getId(), movie2.getId()));

        // then
        assertThat(movies).hasSize(2);
        Movie resultMovie1 = movies.stream().filter(m -> m.getId().equals(movie1.getId())).findFirst().orElseThrow();
        Movie resultMovie2 = movies.stream().filter(m -> m.getId().equals(movie2.getId())).findFirst().orElseThrow();
        assertThat(resultMovie1.getMovieGenres())
                .hasSize(2)
                .extracting("genre.genreType")
                .containsExactlyInAnyOrder(genre1.getGenreType(), genre2.getGenreType());
        assertThat(resultMovie2.getMovieGenres())
                .hasSize(1)
                .extracting("genre.genreType")
                .containsExactly(genre3.getGenreType());
    }

    @Test
    @DisplayName("영화 ID로 단일 영화와 연결된 장르를 페치조인으로 조회한다.")
    void findMovieWithGenreByMovieId_whenValidMovieId_returnsMovieWithGenres() {
        // given
        Movie movie = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        MovieGenre movieGenre1 = createMovieGenre(movie, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie, genre2);

        // when
        movieRepository.save(movie);
        genreRepository.saveAll(List.of(genre1, genre2));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2));
        em.flush();
        em.clear();
        Optional<Movie> result = movieRepository.findMovieWithGenreByMovieId(movie.getId());

        // then
        assertThat(result).isPresent();
        Movie resultMovie = result.get();
        assertThat(resultMovie.getTitle()).isEqualTo(movie.getTitle());
        assertThat(resultMovie.getMovieGenres())
                .hasSize(2)
                .extracting("genre.genreType")
                .containsExactlyInAnyOrder(genre1.getGenreType(), genre2.getGenreType());
    }

    @Test
    @DisplayName("존재하지 않는 영화 ID 목록으로 조회하면 빈 결과를 반환한다.")
    void findMoviesWithGenreByMovieIds_whenInvalidMovieIds_returnsEmptyList() {
        // when
        List<Movie> movies = movieRepository.findMoviesWithGenreByMovieIds(List.of(999L, 1000L));

        // then
        assertThat(movies).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 영화 ID로 조회하면 빈 결과를 반환한다.")
    void findMovieWithGenreByMovieId_whenInvalidMovieId_returnsEmptyOptional() {
        // when
        Optional<Movie> result = movieRepository.findMovieWithGenreByMovieId(999L);

        // then
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("삭제된 영화는 조회되지 않는다.")
    void findMoviesWithGenreByMovieIds_whenMovieIsDeleted_returnsEmptyList() {
        // given
        Movie movie = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);

        // when
        movieRepository.save(movie);
        movie.delete();
        List<Movie> movies = movieRepository.findMoviesWithGenreByMovieIds(List.of(movie.getId()));
        Optional<Movie> movieWithGenre = movieRepository.findMovieWithGenreByMovieId(movie.getId());

        // then
        assertThat(movies).isEmpty();
        assertThat(movieWithGenre).isNotPresent();
    }

    @Test
    @DisplayName("현재 날짜 기준 이후에 개봉하는 영화를 페이징 처리하여 반환한다.")
    void findUpcomingMoviesByDateRange_whenValidDate_returnsPagedMovies() {
        // given
        LocalDate currentDate = LocalDate.now();
        Movie movie1 = createMovie("영화1", "줄거리1", currentDate.plusDays(1), 120, "https://poster1.url",
                FilmRatings.ALL);
        Movie movie2 = createMovie("영화2", "줄거리2", currentDate.plusDays(10), 130, "https://poster2.url",
                FilmRatings.ADULT);
        Movie movie3 = createMovie("영화3", "줄거리3", currentDate.plusDays(30), 110, "https://poster3.url",
                FilmRatings.FIFTEEN);
        Movie movie4 = createMovie("영화4", "줄거리4", currentDate, 110, "https://poster3.url",
                FilmRatings.FIFTEEN);

        // when
        movieRepository.saveAll(List.of(movie1, movie2, movie3, movie4));
        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Movie> result = movieRepository.findUpcomingMoviesByDateRange(currentDate, pageable);

        // then
        assertThat(result.getContent())
                .hasSize(3)
                .extracting("title", "releaseDate")
                .containsExactlyInAnyOrder(
                        tuple(movie1.getTitle(), movie1.getReleaseDate()),
                        tuple(movie2.getTitle(), movie2.getReleaseDate()),
                        tuple(movie3.getTitle(), movie3.getReleaseDate())
                );
    }

    @Test
    @DisplayName("현재 날짜 이후에 개봉하는 영화가 없을 경우 빈 결과를 반환한다.")
    void findUpcomingMoviesByDateRange_whenNoUpcomingMovies_returnsEmptySlice() {
        // given
        LocalDate currentDate = LocalDate.now();
        Movie movie1 = createMovie("영화1", "줄거리1", currentDate.minusDays(1), 120, "https://poster1.url",
                FilmRatings.ALL);
        Movie movie2 = createMovie("영화2", "줄거리2", currentDate.minusDays(10), 130, "https://poster2.url",
                FilmRatings.ADULT);
        Movie movie3 = createMovie("영화3", "줄거리3", currentDate, 110, "https://poster3.url",
                FilmRatings.FIFTEEN);

        // when
        movieRepository.saveAll(List.of(movie1, movie2, movie3));
        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Movie> result = movieRepository.findUpcomingMoviesByDateRange(currentDate, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("좋아요 수와 별점 기준으로 영화를 UNION 조회한다.")
    void findMoviesWithStarRatingAndLikesUnion_returnsMoviesWithLikesAndRatings() {
        // given
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ALL);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Movie movie3 = createMovie("영화3", "줄거리3", LocalDate.now(), 140, "https://poster3.url", FilmRatings.ADULT);
        Movie movie4 = createMovie("영화4", "줄거리4", LocalDate.now(), 150, "https://poster3.url", FilmRatings.ADULT);

        // when
        movieRepository.saveAll(List.of(movie1, movie2, movie3, movie4));
        movie1.addLikeCounts();
        movie2.addLikeCounts();
        movie3.evaluateMovieRating(BigDecimal.valueOf(4.05));
        List<Movie> result = movieRepository.findMoviesWithStarRatingAndLikesUnion();

        // then
        assertThat(result)
                .hasSize(3)
                .extracting("title", "likeCounts", "averageRating")
                .containsExactlyInAnyOrder(
                        tuple(movie1.getTitle(), 1, BigDecimal.ZERO),
                        tuple(movie2.getTitle(), 1, BigDecimal.ZERO),
                        tuple(movie3.getTitle(), 0, BigDecimal.valueOf(4.05))
                );
    }

    @Test
    @DisplayName("영화의 좋아요 수와 평균 별점이 0인 경우 조회하지 않는다.")
    void findMoviesWithStarRatingAndLikesUnion_whenNoLikesOrRatings_returnsEmptyList() {
        // given
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ALL);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);

        movieRepository.saveAll(List.of(movie1, movie2));

        // when
        List<Movie> result = movieRepository.findMoviesWithStarRatingAndLikesUnion();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자의 선호 장르의 영화 중에 사용자가 리뷰, 좋아요, 컬렉션, 평점을 남기지 않은 영화를 조회한다.")
    void findMoviesByPreferredGenresAndNotInteracted_whenValidConditions_returnsMovies() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "nickname",
                "https://profile.url");
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ALL);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 120, "https://poster2.url", FilmRatings.ALL);
        Movie movie3 = createMovie("영화3", "줄거리3", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        Movie movie4 = createMovie("영화4", "줄거리4", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        Movie movie5 = createMovie("영화5", "줄거리5", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        Movie movie6 = createMovie("영화5", "줄거리5", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie2, genre1);
        MovieGenre movieGenre3 = createMovieGenre(movie3, genre2);
        MovieGenre movieGenre4 = createMovieGenre(movie4, genre2);
        MovieGenre movieGenre5 = createMovieGenre(movie5, genre1);
        Review review = createReview("좋은 영화", movie1, user);
        MovieLikes movieLikes = createMovieLikes(movie2, user);
        MovieRatings movieRatings = createMovieRatings(movie3, user, BigDecimal.valueOf(3.0));
        Collection collection = createCollection("컬렉션1", "컬렉션 설명", user);
        CollectionMovie collectionMovie = createCollectionMovie(movie4, collection);

        // when
        userRepository.save(user);
        genreRepository.saveAll(List.of(genre1, genre2));
        movieRepository.saveAll(List.of(movie1, movie2, movie3, movie4, movie5, movie6));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2, movieGenre3, movieGenre4, movieGenre5));
        reviewRepository.save(review);
        movieLikesRepository.save(movieLikes);
        movieRatingsRepository.save(movieRatings);
        collectionRepository.save(collection);
        collectionMovieRepository.save(collectionMovie);

        em.flush();
        em.clear();

        List<Long> genreIds = List.of(genre1.getId(), genre2.getId());
        List<Long> top10MovieIds = List.of();
        List<Movie> result = movieRepository.findMoviesByPreferredGenresAndNotInteracted(
                genreIds,
                user.getId(),
                top10MovieIds,
                null
        );

        // then
        assertThat(result)
                .hasSize(1)
                .extracting("title", "likeCounts", "plot")
                .containsExactlyInAnyOrder(
                        tuple(movie5.getTitle(), movie5.getLikeCounts(), movie5.getPlot())
                );
    }

    @Test
    @DisplayName("사용자의 선호 장르의 영화 중에 사용자가 리뷰, 좋아요, 컬렉션, 평점을 남기지 않은 영화와 TOP 10 영화에 속하지 않는 영화를 조회한다.")
    void findMoviesByPreferredGenresExcludingTop10AndUninteractedMovies_whenValidConditions_returnsExpectedMovies() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "nickname",
                "https://profile.url");
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ALL);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 120, "https://poster2.url", FilmRatings.ALL);
        Movie movie3 = createMovie("영화3", "줄거리3", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        Movie movie4 = createMovie("영화4", "줄거리4", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        Movie movie5 = createMovie("영화5", "줄거리5", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        Movie movie6 = createMovie("영화5", "줄거리5", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        Movie movie7 = createMovie("영화5", "줄거리5", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        Movie movie8 = createMovie("영화5", "줄거리5", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        Movie movie9 = createMovie("영화5", "줄거리5", LocalDate.now(), 120, "https://poster3.url", FilmRatings.ALL);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie2, genre1);
        MovieGenre movieGenre3 = createMovieGenre(movie3, genre2);
        MovieGenre movieGenre4 = createMovieGenre(movie4, genre2);
        MovieGenre movieGenre5 = createMovieGenre(movie5, genre1);
        MovieGenre movieGenre6 = createMovieGenre(movie6, genre1);
        MovieGenre movieGenre7 = createMovieGenre(movie7, genre1);
        MovieGenre movieGenre8 = createMovieGenre(movie8, genre1);
        MovieGenre movieGenre9 = createMovieGenre(movie9, genre2);
        Review review = createReview("좋은 영화", movie1, user);
        MovieLikes movieLikes = createMovieLikes(movie2, user);
        MovieRatings movieRatings = createMovieRatings(movie3, user, BigDecimal.valueOf(3.0));
        Collection collection = createCollection("컬렉션1", "컬렉션 설명", user);
        CollectionMovie collectionMovie = createCollectionMovie(movie4, collection);

        // when
        userRepository.save(user);
        genreRepository.saveAll(List.of(genre1, genre2));
        movieRepository.saveAll(
                List.of(movie1, movie2, movie3, movie4, movie5, movie6, movie6, movie7, movie8, movie9));
        movieGenreRepository.saveAll(
                List.of(movieGenre1, movieGenre2, movieGenre3, movieGenre4, movieGenre5, movieGenre6, movieGenre7,
                        movieGenre8, movieGenre9));
        reviewRepository.save(review);
        movieLikesRepository.save(movieLikes);
        movieRatingsRepository.save(movieRatings);
        collectionRepository.save(collection);
        collectionMovieRepository.save(collectionMovie);

        em.flush();
        em.clear();

        List<Long> genreIds = List.of(genre1.getId(), genre2.getId());
        List<Long> top10MovieIds = List.of(movie6.getId(), movie7.getId(), movie8.getId());
        List<Movie> result = movieRepository.findMoviesByPreferredGenresAndNotInteracted(
                genreIds,
                user.getId(),
                top10MovieIds,
                null
        );

        // then
        assertThat(result)
                .hasSize(2)
                .extracting("title", "likeCounts", "plot")
                .containsExactlyInAnyOrder(
                        tuple(movie5.getTitle(), movie5.getLikeCounts(), movie5.getPlot()),
                        tuple(movie9.getTitle(), movie9.getLikeCounts(), movie9.getPlot())
                );
    }

    @Test
    @DisplayName("특정 영화 ID로 영화 상세 정보를 조회한다.")
    void findMovieDetailInfo_whenValidMovieId_returnsMovieDetails() {
        // given
        Movie movie = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster.url", FilmRatings.ALL);
        Personnel director = createPersonnel("감독", "Director", 10);
        Personnel actor = createPersonnel("배우", "Actor", 15);
        MoviePersonnel movieDirector = createMoviePersonnel(director, movie, MoviePosition.DIRECTOR, "감독");
        MoviePersonnel movieActor = createMoviePersonnel(actor, movie, MoviePosition.ACTOR, "주연");

        // when
        movieRepository.save(movie);
        personnelRepository.saveAll(List.of(director, actor));
        moviePersonnelRepository.saveAll(List.of(movieDirector, movieActor));
        em.flush();
        em.clear();
        Optional<Movie> result = movieRepository.findMovieDetailInfo(movie.getId());

        // then
        assertThat(result)
                .isPresent()
                .get()
                .extracting("title", "runtime", "plot")
                .contains(movie.getTitle(), movie.getRuntime(), movie.getPlot());

        assertThat(result.get().getMoviePersonnels())
                .hasSize(2)
                .extracting("personnel.name", "moviePosition", "movie.id", "characterName")
                .containsExactlyInAnyOrder(
                        tuple(director.getName(), movieDirector.getMoviePosition(), movie.getId(),
                                movieDirector.getCharacterName()),
                        tuple(actor.getName(), movieActor.getMoviePosition(), movie.getId(),
                                movieActor.getCharacterName())
                );
    }

    @Test
    @DisplayName("랜덤하게 선택된 영화의 개수가 totalRounds와 일치한다.")
    void findRandomMovies_returnsCorrectNumberOfMovies() {
        // given
        Genre action = createGenre(GenreType.ACTION);
        Genre comedy = createGenre(GenreType.COMEDY);
        Movie movie1 = createMovie("Movie 1", "Plot 1", LocalDate.now(), 120, "poster1.url", FilmRatings.ALL);
        Movie movie2 = createMovie("Movie 2", "Plot 2", LocalDate.now(), 90, "poster2.url", FilmRatings.ALL);
        Movie movie3 = createMovie("Movie 3", "Plot 3", LocalDate.now(), 100, "poster3.url", FilmRatings.ALL);
        int totalRounds = 2;

        // when
        genreRepository.saveAll(List.of(action, comedy));
        movieRepository.saveAll(List.of(movie1, movie2, movie3));
        movieGenreRepository.saveAll(List.of(
                createMovieGenre(movie1, action),
                createMovieGenre(movie2, comedy)
        ));
        List<Movie> randomMovies = movieRepository.findRandomMovies(totalRounds);

        // then
        assertThat(randomMovies)
                .hasSize(totalRounds)
                .extracting("title")
                .containsAnyOf(movie1.getTitle(), movie2.getTitle(), movie3.getTitle());
    }

    @Test
    @DisplayName("랜덤 영화 조회 시 totalRounds가 0이면 빈 리스트를 반환한다.")
    void findRandomMovies_withZeroRounds_returnsEmptyList() {
        // given
        Movie movie = createMovie("Movie 1", "Plot 1", LocalDate.now(), 120, "poster1.url", FilmRatings.ALL);
        int totalRounds = 0;

        // when
        movieRepository.save(movie);
        List<Movie> randomMovies = movieRepository.findRandomMovies(totalRounds);

        // then
        assertThat(randomMovies).isEmpty();
    }

    @Test
    @DisplayName("영화와 장르 정보를 결합하여 반환한다.")
    void findMoviesWithGenres_fetchesMoviesWithGenres() {
        // given
        Genre action = createGenre(GenreType.ACTION);
        Genre comedy = createGenre(GenreType.COMEDY);
        Movie movie1 = createMovie("Movie 1", "Plot 1", LocalDate.now(), 120, "poster1.url", FilmRatings.ALL);
        Movie movie2 = createMovie("Movie 2", "Plot 2", LocalDate.now(), 90, "poster2.url", FilmRatings.ALL);

        // when
        genreRepository.saveAll(List.of(action, comedy));
        movieRepository.saveAll(List.of(movie1, movie2));
        movieGenreRepository.saveAll(List.of(
                createMovieGenre(movie1, action),
                createMovieGenre(movie2, comedy)
        ));
        em.flush();
        em.clear();
        List<Movie> movies = List.of(movie1, movie2);
        List<Movie> result = movieRepository.findMoviesWithGenres(movies);

        // then
        assertThat(result)
                .hasSize(movies.size())
                .allSatisfy(movie -> assertThat(movie.getMovieGenres()).isNotEmpty());
    }

    @Test
    @DisplayName("빈 영화 리스트를 입력하면 빈 결과를 반환한다.")
    void findMoviesWithGenres_withEmptyMovies_returnsEmptyList() {
        // given
        List<Movie> emptyMovies = List.of();

        // when
        List<Movie> result = movieRepository.findMoviesWithGenres(emptyMovies);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("랜덤 영화 조회 후 장르 정보를 올바르게 결합한다.")
    void integrationTest_findRandomMoviesAndFetchGenres() {
        // given
        Genre action = createGenre(GenreType.ACTION);
        Genre comedy = createGenre(GenreType.COMEDY);
        Movie movie1 = createMovie("Movie 1", "Plot 1", LocalDate.now(), 120, "poster1.url", FilmRatings.ALL);
        Movie movie2 = createMovie("Movie 2", "Plot 2", LocalDate.now(), 90, "poster2.url", FilmRatings.ALL);
        Movie movie3 = createMovie("Movie 3", "Plot 3", LocalDate.now(), 100, "poster3.url", FilmRatings.ALL);
        int totalRounds = 2;

        // when
        genreRepository.saveAll(List.of(action, comedy));
        movieRepository.saveAll(List.of(movie1, movie2, movie3));
        movieGenreRepository.saveAll(List.of(
                createMovieGenre(movie1, action),
                createMovieGenre(movie2, comedy)
        ));
        em.flush();
        em.clear();
        List<Movie> randomMovies = movieRepository.findRandomMovies(totalRounds);
        List<Movie> result = movieRepository.findMoviesWithGenres(randomMovies);

        // then
        assertThat(result)
                .hasSize(randomMovies.size())
                .allSatisfy(movie -> assertThat(movie.getMovieGenres()).isNotEmpty());
    }
}