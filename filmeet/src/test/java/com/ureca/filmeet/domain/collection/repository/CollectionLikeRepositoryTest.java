package com.ureca.filmeet.domain.collection.repository;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createCollectionLikes;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionLikes;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
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
class CollectionLikeRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionLikeRepository collectionLikeRepository;

    @Test
    @DisplayName("사용자와 컬렉션에 해당하는 좋아요 정보를 성공적으로 조회한다.")
    void findCollectionLikesByCollectionIdAndUserId_whenLikeExists_returnsCollectionLikes() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionLikes collectionLikes = createCollectionLikes(user, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionLikeRepository.save(collectionLikes);
        Optional<CollectionLikes> result = collectionLikeRepository.findCollectionLikesByCollectionIdAndUserId(
                collection.getId(), user.getId());

        // then
        assertThat(result)
                .isPresent()
                .get()
                .extracting("id", "user", "collection")
                .contains(collectionLikes.getId(), user, collection);
    }

    @Test
    @DisplayName("존재하지 않는 사용자와 컬렉션에 대해 좋아요 정보를 조회하면 결과가 비어 있다.")
    void findCollectionLikesByCollectionIdAndUserId_whenLikeDoesNotExist_returnsEmpty() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        Optional<CollectionLikes> result1 = collectionLikeRepository.findCollectionLikesByCollectionIdAndUserId(
                collection.getId(), 999L);
        Optional<CollectionLikes> result2 = collectionLikeRepository.findCollectionLikesByCollectionIdAndUserId(
                999L, user.getId());
        
        // then
        assertThat(result1).isNotPresent();
        assertThat(result2).isNotPresent();
    }

    @Test
    @DisplayName("컬렉션과 사용자에 대해 좋아요가 존재하는지 확인한다.")
    void existsByCollectionIdAndUserId_whenLikeExists_returnsTrue() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionLikes collectionLikes = createCollectionLikes(user, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionLikeRepository.save(collectionLikes);
        boolean exists = collectionLikeRepository.existsByCollectionIdAndUserId(collection.getId(), user.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("컬렉션과 사용자에 대해 좋아요가 존재하지 않는 경우 false를 반환한다.")
    void existsByCollectionIdAndUserId_whenLikeDoesNotExist_returnsFalse() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        boolean exists1 = collectionLikeRepository.existsByCollectionIdAndUserId(collection.getId(), 999L);
        boolean exists2 = collectionLikeRepository.existsByCollectionIdAndUserId(999L, user.getId());

        // then
        assertThat(exists1).isFalse();
        assertThat(exists2).isFalse();
    }

    @Test
    @DisplayName("컬렉션에 대해 여러 사용자가 좋아요를 한 경우 각각의 정보를 성공적으로 조회한다.")
    void findCollectionLikesByCollectionIdAndUserId_whenMultipleLikesExist_returnsCorrectResults() {
        // given
        User user1 = createUser("username1", "password", Role.ROLE_USER, Provider.NAVER, "닉네임1",
                "https://example.com/profile1.jpg");
        User user2 = createUser("username2", "password", Role.ROLE_USER, Provider.NAVER, "닉네임2",
                "https://example.com/profile2.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user1);
        CollectionLikes like1 = createCollectionLikes(user1, collection);
        CollectionLikes like2 = createCollectionLikes(user2, collection);

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        collectionRepository.save(collection);
        collectionLikeRepository.save(like1);
        collectionLikeRepository.save(like2);
        boolean existsUser1 = collectionLikeRepository.existsByCollectionIdAndUserId(collection.getId(), user1.getId());
        boolean existsUser2 = collectionLikeRepository.existsByCollectionIdAndUserId(collection.getId(), user2.getId());

        // then
        assertThat(existsUser1).isTrue();
        assertThat(existsUser2).isTrue();
    }
}