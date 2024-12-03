package com.ureca.filmeet.domain.collection.service.service;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createCollectionMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.collection.dto.response.CollectionMovieInfoResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionsResponse;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
import com.ureca.filmeet.domain.collection.repository.CollectionMovieRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
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
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class CollectionQueryServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionMovieRepository collectionMovieRepository;

    @Autowired
    private CollectionQueryService collectionQueryService;

    @DisplayName("특정 사용자의 컬렉션 목록과 컬렉션 별로 영화 목록을 페이징 처리하여 반환한다.")
    @Test
    void shouldReturnPagedCollectionsWithMovies() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie3 = createMovie("제목3", "줄거리3", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Collection collection1 = createCollection("컬렉션1", "내용1", user);
        Collection collection2 = createCollection("컬렉션2", "내용2", user);
        CollectionMovie collectionMovie1 = createCollectionMovie(movie1, collection1);
        CollectionMovie collectionMovie2 = createCollectionMovie(movie2, collection2);
        CollectionMovie collectionMovie3 = createCollectionMovie(movie3, collection2);

        // when
        userRepository.save(user);
        movieRepository.saveAll(List.of(movie1, movie2, movie3));
        collectionRepository.saveAll(List.of(collection1, collection2));
        collectionMovieRepository.saveAll(List.of(collectionMovie1, collectionMovie2, collectionMovie3));
        Slice<CollectionsResponse> result = collectionQueryService.getCollections(user.getId(), 0, 5);

        List<CollectionsResponse> expectedCollections = List.of(
                new CollectionsResponse(
                        collection1.getId(), collection1.getTitle(), collection1.getContent(),
                        collection1.getUser().getNickname(), collection1.getUser().getProfileImage(),
                        collection1.getLikeCounts(), collection1.getCommentCounts(),
                        List.of(new CollectionMovieInfoResponse(
                                collectionMovie1.getMovie().getId(),
                                collectionMovie1.getMovie().getTitle(),
                                collectionMovie1.getMovie().getPosterUrl(),
                                collectionMovie1.getMovie().getReleaseDate(),
                                collectionMovie1.getMovie().getRuntime(),
                                collectionMovie1.getMovie().getFilmRatings(),
                                collectionMovie1.getMovie().getAverageRating(),
                                collectionMovie1.getMovie().getLikeCounts(),
                                collectionMovie1.getMovie().getRatingCounts()
                        ))
                ),
                new CollectionsResponse(
                        collection2.getId(), collection2.getTitle(), collection2.getContent(),
                        collection2.getUser().getNickname(), collection2.getUser().getProfileImage(),
                        collection2.getLikeCounts(), collection2.getCommentCounts(),
                        List.of(
                                new CollectionMovieInfoResponse(
                                        collectionMovie2.getMovie().getId(),
                                        collectionMovie2.getMovie().getTitle(),
                                        collectionMovie2.getMovie().getPosterUrl(),
                                        collectionMovie2.getMovie().getReleaseDate(),
                                        collectionMovie2.getMovie().getRuntime(),
                                        collectionMovie2.getMovie().getFilmRatings(),
                                        collectionMovie2.getMovie().getAverageRating(),
                                        collectionMovie2.getMovie().getLikeCounts(),
                                        collectionMovie2.getMovie().getRatingCounts()
                                ),
                                new CollectionMovieInfoResponse(
                                        collectionMovie3.getMovie().getId(),
                                        collectionMovie3.getMovie().getTitle(),
                                        collectionMovie3.getMovie().getPosterUrl(),
                                        collectionMovie3.getMovie().getReleaseDate(),
                                        collectionMovie3.getMovie().getRuntime(),
                                        collectionMovie3.getMovie().getFilmRatings(),
                                        collectionMovie3.getMovie().getAverageRating(),
                                        collectionMovie3.getMovie().getLikeCounts(),
                                        collectionMovie3.getMovie().getRatingCounts()
                                )
                        )
                )
        );

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(expectedCollections);
    }
}