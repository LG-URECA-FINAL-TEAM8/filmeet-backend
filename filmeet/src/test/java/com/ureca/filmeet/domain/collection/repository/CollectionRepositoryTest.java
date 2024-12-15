package com.ureca.filmeet.domain.collection.repository;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
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
class CollectionRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @DisplayName("특정 사용자의 컬렉션 목록을 페이징 처리하여 조회한다.")
    @Test
    void shouldFetchCollectionsByUserIdWithPagination() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection1 = createCollection("컬렉션 제목1", "컬렉션 내용1", user);
        Collection collection2 = createCollection("컬렉션 제목2", "컬렉션 내용2", user);

        // when
        userRepository.save(user);
        collectionRepository.saveAll(List.of(collection1, collection2));
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Collection> result = collectionRepository.findCollectionsByUserId(user.getId(), pageable);

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .extracting("id", "title", "content", "likeCounts", "commentCounts", "user")
                .containsExactlyInAnyOrder(
                        tuple(
                                collection1.getId(), collection1.getTitle(), collection1.getContent(),
                                collection1.getCommentCounts(), collection1.getLikeCounts(), user
                        ),
                        tuple(
                                collection2.getId(), collection2.getTitle(), collection2.getContent(),
                                collection2.getCommentCounts(), collection2.getLikeCounts(), user
                        )
                );
        assertThat(result.hasNext()).isFalse();
    }

    @DisplayName("존재하지 않는 사용자 ID로 컬렉션 조회 시 빈 결과를 반환한다.")
    @Test
    void shouldReturnEmptyWhenUserIdDoesNotExist() {
        // given
        Long nonexistentUserId = 999L;
        Pageable pageable = PageRequest.of(0, 1);

        // when
        Slice<Collection> result = collectionRepository.findCollectionsByUserId(nonexistentUserId, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @DisplayName("삭제된 컬렉션은 조회 되지 않는다.")
    @Test
    void shouldNotReturnDeletedCollectionsByUserId() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection1 = createCollection("컬렉션 제목1", "컬렉션 내용1", user);
        Collection collection2 = createCollection("컬렉션 제목2", "컬렉션 내용2", user);

        // when
        userRepository.save(user);
        collectionRepository.saveAll(List.of(collection1, collection2));
        collection2.delete();
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Collection> result = collectionRepository.findCollectionsByUserId(user.getId(), pageable);

        // then
        assertThat(result.getContent())
                .hasSize(1)
                .extracting("id", "title", "content", "likeCounts", "commentCounts", "user")
                .containsExactlyInAnyOrder(
                        tuple(
                                collection1.getId(), collection1.getTitle(), collection1.getContent(),
                                collection1.getCommentCounts(), collection1.getLikeCounts(), user
                        )
                );
    }

    @DisplayName("특정 사용자의 특정 컬렉션을 조회한다.")
    @Test
    void shouldFetchSpecificCollectionByCollectionIdAndUserId() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        Optional<Collection> result = collectionRepository.findCollectionByCollectionIdAndUserId(collection.getId());

        // then
        assertThat(result)
                .isPresent()
                .get()
                .extracting("id", "content", "title", "likeCounts", "commentCounts", "user")
                .contains(
                        collection.getId(), collection.getContent(), collection.getTitle(),
                        collection.getLikeCounts(), collection.getCommentCounts(), user
                );
    }

    @DisplayName("삭제된 컬렉션은 조회되지 않는다.")
    @Test
    void shouldNotFetchDeletedCollection() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collection.delete();
        Optional<Collection> result = collectionRepository.findCollectionByCollectionIdAndUserId(collection.getId());

        // then
        assertThat(result).isNotPresent();
    }
}