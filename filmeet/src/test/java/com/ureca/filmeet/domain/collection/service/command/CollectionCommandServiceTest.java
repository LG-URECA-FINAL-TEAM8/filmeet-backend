package com.ureca.filmeet.domain.collection.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createCollectionMovie;
import static com.ureca.filmeet.global.util.TestUtils.createGenre;
import static com.ureca.filmeet.global.util.TestUtils.createGenreScore;
import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovieGenre;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.ureca.filmeet.domain.collection.dto.request.CollectionCreateRequest;
import com.ureca.filmeet.domain.collection.dto.request.CollectionDeleteRequest;
import com.ureca.filmeet.domain.collection.dto.request.CollectionModifyRequest;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
import com.ureca.filmeet.domain.collection.exception.CollectionMoviesNotFoundException;
import com.ureca.filmeet.domain.collection.exception.CollectionNotFoundException;
import com.ureca.filmeet.domain.collection.exception.CollectionUserNotFoundException;
import com.ureca.filmeet.domain.collection.repository.CollectionMovieRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.GenreScore;
import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.genre.repository.GenreRepository;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.genre.repository.MovieGenreRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class CollectionCommandServiceTest {

    @Autowired
    private CollectionCommandService collectionCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionMovieRepository collectionMovieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieGenreRepository movieGenreRepository;

    @Autowired
    private GenreScoreRepository genreScoreRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("컬렉션 생성 시 영화와 장르 정보를 저장하고 사용자의 장르 점수를 업데이트한다.")
    void createCollection_whenValidRequest_savesCollection() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Genre genre3 = createGenre(GenreType.ADVENTURE);
        Genre genre4 = createGenre(GenreType.COMEDY);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie2, genre2);
        GenreScore genreScore1 = createGenreScore(user, genre1, 0);
        GenreScore genreScore2 = createGenreScore(user, genre2, 0);
        GenreScore genreScore3 = createGenreScore(user, genre3, 0);
        GenreScore genreScore4 = createGenreScore(user, genre4, 0);

        // when
        userRepository.save(user);
        genreRepository.saveAll(List.of(genre1, genre2, genre3, genre4));
        movieRepository.saveAll(List.of(movie1, movie2));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2));
        genreScoreRepository.saveAll(List.of(genreScore1, genreScore2, genreScore3, genreScore4));

        em.flush();
        em.clear();

        CollectionCreateRequest request = new CollectionCreateRequest("컬렉션 제목", "컬렉션 내용",
                List.of(movie1.getId(), movie2.getId()));
        Long collectionId = collectionCommandService.createCollection(request, user.getId());
        Optional<Collection> savedCollection = collectionRepository.findById(collectionId);
        List<Long> savedMovieIds = collectionMovieRepository.findMovieIdsByCollectionId(collectionId);
        List<GenreScore> genreScores = genreScoreRepository.findAll();

        // then
        assertThat(savedCollection)
                .isPresent()
                .get()
                .extracting("title", "content", "user.id")
                .contains(request.title(), request.content(), user.getId());
        assertThat(savedMovieIds).containsExactlyInAnyOrder(movie1.getId(), movie2.getId());
        assertThat(genreScores)
                .hasSize(4)
                .extracting("user.id", "genre.id", "score")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), genre1.getId(), 4),
                        tuple(user.getId(), genre2.getId(), 4),
                        tuple(user.getId(), genre3.getId(), 0),
                        tuple(user.getId(), genre4.getId(), 0)
                );
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 컬렉션을 생성하려고 하면 CollectionUserNotFoundException이 발생한다.")
    void createCollection_whenUserNotFound_throwsException() {
        // given
        CollectionCreateRequest request = new CollectionCreateRequest("컬렉션 제목", "컬렉션 내용", List.of(1L, 2L));

        // when & then
        assertThatThrownBy(() -> collectionCommandService.createCollection(request, 999L))
                .isInstanceOf(CollectionUserNotFoundException.class);
    }

    @Test
    @DisplayName("영화 ID가 유효하지 않을 경우 CollectionMoviesNotFoundException이 발생한다.")
    void createCollection_whenMoviesNotFound_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        userRepository.save(user);

        CollectionCreateRequest request = new CollectionCreateRequest("컬렉션 제목", "컬렉션 내용", List.of(999L, 1000L));

        // when & then
        assertThatThrownBy(() -> collectionCommandService.createCollection(request, user.getId()))
                .isInstanceOf(CollectionMoviesNotFoundException.class);
    }

    @Test
    @DisplayName("컬렉션 제목, 내용 및 영화 목록을 성공적으로 수정하며, 사용자의 장르 점수를 업데이트한다.")
    void modifyCollection_whenValidRequest_updatesCollection() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie3 = createMovie("제목3", "줄거리3", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Genre genre3 = createGenre(GenreType.ADVENTURE);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie2, genre2);
        MovieGenre movieGenre3 = createMovieGenre(movie3, genre3);
        GenreScore genreScore1 = createGenreScore(user, genre1, 0);
        GenreScore genreScore2 = createGenreScore(user, genre2, 0);
        GenreScore genreScore3 = createGenreScore(user, genre3, 0);
        Collection collection = createCollection("기존 컬렉션 제목", "기존 컬렉션 내용", user);
        CollectionMovie collectionMovie1 = createCollectionMovie(movie1, collection);
        CollectionMovie collectionMovie2 = createCollectionMovie(movie2, collection);

        // when
        userRepository.save(user);
        movieRepository.saveAll(List.of(movie1, movie2, movie3));
        genreRepository.saveAll(List.of(genre1, genre2, genre3));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2, movieGenre3));
        genreScoreRepository.saveAll(List.of(genreScore1, genreScore2, genreScore3));
        collectionRepository.save(collection);
        collectionMovieRepository.saveAll(List.of(collectionMovie1, collectionMovie2));

        em.flush();
        em.clear();

        CollectionModifyRequest request = new CollectionModifyRequest(
                collection.getId(),
                "수정된 컬렉션 제목",
                "수정된 컬렉션 내용",
                List.of(movie2.getId(), movie3.getId())
        );
        Long updatedCollectionId = collectionCommandService.modifyCollection(request, user.getId());
        Optional<Collection> updatedCollection = collectionRepository.findById(updatedCollectionId);
        List<Long> updatedMovieIds = collectionMovieRepository.findMovieIdsByCollectionId(updatedCollectionId);
        List<GenreScore> updatedGenreScores = genreScoreRepository.findAll();

        // then
        // 1. 컬렉션 데이터 검증
        assertThat(updatedCollection)
                .isPresent()
                .get()
                .extracting("title", "content", "user.id")
                .contains(request.title(), request.content(), user.getId());

        // 2. 컬렉션 영화 목록 검증
        assertThat(updatedMovieIds).containsExactlyInAnyOrder(movie2.getId(), movie3.getId());

        // 3. 장르 점수 업데이트 검증
        assertThat(updatedGenreScores)
                .hasSize(3)
                .extracting("user.id", "genre.id", "score")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), genre1.getId(), -4), // movie1 제거
                        tuple(user.getId(), genre2.getId(), 0),  // movie2 유지
                        tuple(user.getId(), genre3.getId(), 4)   // movie3 추가
                );
    }

    @Test
    @DisplayName("존재하지 않는 컬렉션을 수정하려고 하면 CollectionNotFoundException이 발생한다.")
    void modifyCollection_whenCollectionNotFound_throwsException() {
        // given
        CollectionModifyRequest request = new CollectionModifyRequest(999L, "수정된 제목", "수정된 내용", List.of(1L, 2L));

        // when & then
        assertThatThrownBy(() -> collectionCommandService.modifyCollection(request, 1L))
                .isInstanceOf(CollectionNotFoundException.class);
    }

    @Test
    @DisplayName("컬렉션을 성공적으로 삭제하고, 사용자의 장르 점수가 업데이트된다.")
    void deleteCollection_whenValidRequest_deletesCollectionAndUpdatesGenreScores() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie2, genre2);
        GenreScore genreScore1 = createGenreScore(user, genre1, 4); // 초기 점수
        GenreScore genreScore2 = createGenreScore(user, genre2, 4); // 초기 점수
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionMovie collectionMovie1 = createCollectionMovie(movie1, collection);
        CollectionMovie collectionMovie2 = createCollectionMovie(movie2, collection);

        // when
        userRepository.save(user);
        genreRepository.saveAll(List.of(genre1, genre2));
        movieRepository.saveAll(List.of(movie1, movie2));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2));
        genreScoreRepository.saveAll(List.of(genreScore1, genreScore2));
        collectionRepository.save(collection);
        collectionMovieRepository.saveAll(List.of(collectionMovie1, collectionMovie2));

        em.flush();
        em.clear();

        CollectionDeleteRequest request = new CollectionDeleteRequest(collection.getId(),
                List.of(movie1.getId(), movie2.getId()));
        collectionCommandService.deleteCollection(request, user.getId());

        // then
        // 1. 컬렉션 삭제 검증
        Optional<Collection> deletedCollection = collectionRepository.findById(collection.getId());
        assertThat(deletedCollection).isPresent();
        assertThat(deletedCollection.get().getIsDeleted()).isTrue();

        // 2. 장르 점수 업데이트 검증
        List<GenreScore> updatedGenreScores = genreScoreRepository.findAll();
        assertThat(updatedGenreScores)
                .hasSize(2)
                .extracting("user.id", "genre.id", "score")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), genre1.getId(), 0), // movie1 제거로 점수 감소
                        tuple(user.getId(), genre2.getId(), 0)  // movie2 제거로 점수 감소
                );

        // 3. 컬렉션-영화 관계 삭제 검증
        List<Long> remainingMovieIds = collectionMovieRepository.findMovieIdsByCollectionId(collection.getId());
        assertThat(remainingMovieIds).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 컬렉션을 삭제하려고 하면 CollectionNotFoundException이 발생한다.")
    void deleteCollection_whenCollectionNotFound_throwsException() {
        // given
        CollectionDeleteRequest request = new CollectionDeleteRequest(999L, List.of(1L, 2L));

        // when & then
        assertThatThrownBy(() -> collectionCommandService.deleteCollection(request, 1L))
                .isInstanceOf(CollectionNotFoundException.class);
    }
}