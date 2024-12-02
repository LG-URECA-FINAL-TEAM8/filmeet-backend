package com.ureca.filmeet.domain.collection.repository;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createCollectionMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class CollectionMovieRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionMovieRepository collectionMovieRepository;

    @DisplayName("컬렉션에 저장된 영화 목록 ID 값들을 모두 정상적으로 조회한다.")
    @Test
    void shouldFetchAllMovieIdsFromCollection() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie3 = createMovie("제목3", "줄거리3", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionMovie collectionMovie1 = createCollectionMovie(movie1, collection);
        CollectionMovie collectionMovie2 = createCollectionMovie(movie2, collection);
        CollectionMovie collectionMovie3 = createCollectionMovie(movie3, collection);

        // when
        userRepository.save(user);
        movieRepository.save(movie1);
        movieRepository.save(movie2);
        movieRepository.save(movie3);
        collectionRepository.save(collection);
        collectionMovieRepository.save(collectionMovie1);
        collectionMovieRepository.save(collectionMovie2);
        collectionMovieRepository.save(collectionMovie3);
        List<Long> expectedMovieIds = List.of(movie1.getId(), movie2.getId(), movie3.getId());
        List<Long> movieIds = collectionMovieRepository.findMovieIdsByCollectionId(collection.getId());

        // then
        assertThat(movieIds)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(expectedMovieIds);
    }

    @DisplayName("컬렉션에 저장된 영화 목록 중 삭제된 영화를 제외하고 ID 값들을 조회한다.")
    @Test
    void shouldFetchOnlyActiveMovieIdsFromCollection() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie3 = createMovie("제목3", "줄거리3", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionMovie collectionMovie1 = createCollectionMovie(movie1, collection);
        CollectionMovie collectionMovie2 = createCollectionMovie(movie2, collection);
        CollectionMovie collectionMovie3 = createCollectionMovie(movie3, collection);

        // when
        userRepository.save(user);
        movieRepository.save(movie1);
        movieRepository.save(movie2);
        movieRepository.save(movie3);
        collectionRepository.save(collection);
        collectionMovieRepository.save(collectionMovie1);
        collectionMovieRepository.save(collectionMovie2);
        collectionMovieRepository.save(collectionMovie3);
        movie1.delete();
        movie2.delete();
        List<Long> expectedMovieIds = List.of(movie3.getId());
        List<Long> movieIds = collectionMovieRepository.findMovieIdsByCollectionId(collection.getId());

        // then
        assertThat(movieIds)
                .hasSize(1)
                .containsExactlyInAnyOrderElementsOf(expectedMovieIds);
    }

    @DisplayName("컬렉션에 저장된 영화정보들을 조회한다.")
    @Test
    void shouldFetchMoviesFromMultipleCollections() {
        // given
        LocalDate releaseDate = LocalDate.now();
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie1 = createMovie("제목1", "줄거리1", releaseDate, 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", releaseDate, 150, "https://abc", FilmRatings.ADULT);
        Movie movie3 = createMovie("제목3", "줄거리3", releaseDate, 150, "https://abc", FilmRatings.ADULT);
        Collection collection1 = createCollection("컬렉션 제목", "컬렉션 내용", user);
        Collection collection2 = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionMovie collectionMovie1 = createCollectionMovie(movie1, collection1);
        CollectionMovie collectionMovie2 = createCollectionMovie(movie2, collection1);
        CollectionMovie collectionMovie3 = createCollectionMovie(movie3, collection1);
        CollectionMovie collectionMovie4 = createCollectionMovie(movie3, collection2);

        // when
        userRepository.save(user);
        movieRepository.save(movie1);
        movieRepository.save(movie2);
        movieRepository.save(movie3);
        collectionRepository.save(collection1);
        collectionRepository.save(collection2);
        collectionMovieRepository.save(collectionMovie1);
        collectionMovieRepository.save(collectionMovie2);
        collectionMovieRepository.save(collectionMovie3);
        collectionMovieRepository.save(collectionMovie4);
        List<CollectionMovie> collectionMovies = collectionMovieRepository.findMoviesByCollectionIds(
                List.of(collection1.getId(), collection2.getId()));

        // then
        assertThat(collectionMovies)
                .hasSize(4)
                .extracting("id", "movie", "collection", "movie.title", "movie.plot")
                .containsExactlyInAnyOrder(
                        tuple(
                                collectionMovie1.getId(), movie1, collection1, movie1.getTitle(), movie1.getPlot()
                        ),
                        tuple(
                                collectionMovie2.getId(), movie2, collection1, movie2.getTitle(), movie2.getPlot()
                        ),
                        tuple(
                                collectionMovie3.getId(), movie3, collection1, movie3.getTitle(), movie3.getPlot()
                        ),
                        tuple(
                                collectionMovie4.getId(), movie3, collection2, movie3.getTitle(), movie3.getPlot()
                        )
                );
    }

    @DisplayName("삭제된 컬렉션의 영화는 조회되지 않는다.")
    @Test
    void shouldNotFetchMoviesFromDeletedCollection() {
        // given
        LocalDate releaseDate = LocalDate.now();
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie1 = createMovie("제목1", "줄거리1", releaseDate, 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", releaseDate, 150, "https://abc", FilmRatings.ADULT);
        Movie movie3 = createMovie("제목3", "줄거리3", releaseDate, 150, "https://abc", FilmRatings.ADULT);
        Collection collection1 = createCollection("컬렉션 제목1", "컬렉션 내용1", user);
        Collection collection2 = createCollection("컬렉션 제목2", "컬렉션 내용2", user); // 소프트 딜리트 예정
        CollectionMovie collectionMovie1 = createCollectionMovie(movie1, collection1);
        CollectionMovie collectionMovie2 = createCollectionMovie(movie2, collection1);
        CollectionMovie collectionMovie3 = createCollectionMovie(movie3, collection2);

        // when
        userRepository.save(user);
        movieRepository.save(movie1);
        movieRepository.save(movie2);
        movieRepository.save(movie3);
        collectionRepository.save(collection1);
        collectionRepository.save(collection2);
        collectionMovieRepository.save(collectionMovie1);
        collectionMovieRepository.save(collectionMovie2);
        collectionMovieRepository.save(collectionMovie3);
        collection2.delete();
        List<CollectionMovie> collectionMovies = collectionMovieRepository.findMoviesByCollectionIds(
                List.of(collection1.getId(), collection2.getId()));

        // then
        assertThat(collectionMovies)
                .hasSize(2)
                .extracting("movie.id")
                .doesNotContain(movie3.getId());
    }

    @DisplayName("컬렉션에 저장된 영화 목록을 페이징 처리하여 조회한다.")
    @Test
    void shouldFetchMoviesByCollectionWithPagination() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        CollectionMovie collectionMovie1 = createCollectionMovie(movie1, collection);
        CollectionMovie collectionMovie2 = createCollectionMovie(movie2, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        movieRepository.save(movie1);
        movieRepository.save(movie2);
        collectionMovieRepository.save(collectionMovie1);
        collectionMovieRepository.save(collectionMovie2);

        Pageable pageable = PageRequest.of(0, 10);
        Slice<CollectionMovie> result = collectionMovieRepository.findMoviesBy(collection.getId(), pageable);

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .extracting("id", "movie", "collection")
                .containsExactlyInAnyOrder(
                        tuple(
                                collectionMovie1.getId(), movie1, collection
                        ),
                        tuple(
                                collectionMovie2.getId(), movie2, collection
                        )
                );
        assertThat(result.hasNext()).isFalse();
    }

    @DisplayName("존재하지 않는 컬렉션 ID로 조회 시 빈 결과를 반환한다.")
    @Test
    void shouldReturnEmptyWhenCollectionIdDoesNotExist() {
        // given
        Long nonexistentCollectionId = 999L;
        Pageable pageable = PageRequest.of(0, 1);

        // when
        Slice<CollectionMovie> result = collectionMovieRepository.findMoviesBy(nonexistentCollectionId, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.hasNext()).isFalse();
    }

    @DisplayName("삭제된 컬렉션의 영화 정보를 조회하면 빈 결과를 반환한다.")
    @Test
    void shouldReturnEmptyWhenFetchingMoviesFromDeletedCollection() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        CollectionMovie collectionMovie1 = createCollectionMovie(movie1, collection);
        CollectionMovie collectionMovie2 = createCollectionMovie(movie2, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        movieRepository.save(movie1);
        movieRepository.save(movie2);
        collectionMovieRepository.save(collectionMovie1);
        collectionMovieRepository.save(collectionMovie2);
        collection.delete();

        Pageable pageable = PageRequest.of(0, 10);
        Slice<CollectionMovie> result = collectionMovieRepository.findMoviesBy(collection.getId(), pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @DisplayName("특정 컬렉션에서 지정된 영화들을 삭제한다.")
    @Test
    void shouldDeleteMoviesByCollectionIdAndMovieIds() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        CollectionMovie collectionMovie1 = createCollectionMovie(movie1, collection);
        CollectionMovie collectionMovie2 = createCollectionMovie(movie2, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        movieRepository.save(movie1);
        movieRepository.save(movie2);
        collectionMovieRepository.save(collectionMovie1);
        collectionMovieRepository.save(collectionMovie2);
        collectionMovieRepository.deleteByCollectionIdAndMovieIds(
                collection.getId(),
                List.of(movie1.getId())
        );
        List<CollectionMovie> remainingMovies = collectionMovieRepository.findMoviesBy(
                collection.getId(),
                Pageable.unpaged()
        ).getContent();

        // then
        assertThat(remainingMovies)
                .hasSize(1)
                .extracting("movie.id")
                .containsExactly(movie2.getId());
    }

    @DisplayName("존재하지 않는 컬렉션 ID 또는 영화 ID로 삭제를 시도하면 컬렉션에 영화 데이터들을 삭제 되지 않는다.")
    @Test
    void shouldDoNothingWhenCollectionIdOrMovieIdsDoNotExist() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        Movie movie = createMovie("영화1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        CollectionMovie collectionMovie = createCollectionMovie(movie, collection);

        userRepository.save(user);
        collectionRepository.save(collection);
        movieRepository.save(movie);
        collectionMovieRepository.save(collectionMovie);

        Long nonexistentCollectionId = 999L;
        List<Long> nonexistentMovieIds = List.of(999L);

        // when
        collectionMovieRepository.deleteByCollectionIdAndMovieIds(nonexistentCollectionId, nonexistentMovieIds);

        List<CollectionMovie> remainingMovies = collectionMovieRepository.findMoviesBy(
                collection.getId(),
                Pageable.unpaged()
        ).getContent();

        // then
        assertThat(remainingMovies)
                .hasSize(1)
                .extracting("movie.id")
                .containsExactly(movie.getId());
    }
}