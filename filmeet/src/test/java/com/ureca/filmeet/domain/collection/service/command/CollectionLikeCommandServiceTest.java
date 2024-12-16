package com.ureca.filmeet.domain.collection.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionLikes;
import com.ureca.filmeet.domain.collection.exception.CollectionLikeAlreadyExistsException;
import com.ureca.filmeet.domain.collection.exception.CollectionNotFoundException;
import com.ureca.filmeet.domain.collection.exception.CollectionUserNotFoundException;
import com.ureca.filmeet.domain.collection.repository.CollectionLikeRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class CollectionLikeCommandServiceTest {

    @Autowired
    private CollectionLikeCommandService collectionLikeCommandService;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionLikeRepository collectionLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        collectionLikeRepository.deleteAllInBatch();
        collectionRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자가 컬렉션에 좋아요를 성공적으로 추가하고 좋아요를 누른 만큼 컬렉션에 좋아요 개수가 증가한다.")
    @Test
    void collectionLikes_whenValidRequest_addsLike() {
        // given
        User user1 = createUser("username1", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        User user2 = createUser("username2", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목1", "컬렉션 내용", user1);

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        collectionRepository.save(collection);
        collectionLikeCommandService.collectionLikes(collection.getId(), user1.getId());
        collectionLikeCommandService.collectionLikes(collection.getId(), user2.getId());
        Optional<CollectionLikes> collectionLikes = collectionLikeRepository.findCollectionLikesByCollectionIdAndUserId(
                collection.getId(), user1.getId());

        // then
        assertThat(collectionLikes)
                .isPresent()
                .get()
                .extracting("user.id", "collection.id", "collection.likeCounts")
                .contains(
                        user1.getId(), collection.getId(), 2
                );
    }

    @Test
    @DisplayName("이미 좋아요한 컬렉션에 다시 좋아요를 누를 경우 CollectionLikeAlreadyExistsException 예외가 발생한다.")
    void collectionLikes_whenAlreadyLiked_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionLikeCommandService.collectionLikes(collection.getId(), user.getId());

        // then
        assertThatThrownBy(() -> collectionLikeCommandService.collectionLikes(collection.getId(), user.getId()))
                .isInstanceOf(CollectionLikeAlreadyExistsException.class);
    }

    @Test
    @DisplayName("존재하지 않는 컬렉션 ID로 좋아요를 시도하면 CollectionNotFoundException 예외가 발생한다.")
    void collectionLikes_whenCollectionNotFound_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");

        // when
        userRepository.save(user);

        // then
        assertThatThrownBy(() -> collectionLikeCommandService.collectionLikes(999L, user.getId()))
                .isInstanceOf(CollectionNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 좋아요를 시도하면 CollectionUserNotFoundException 예외가 발생한다.")
    void collectionLikes_whenUserNotFound_throwsException() {
        // given
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", null);

        // when
        Collection saveCollection = collectionRepository.save(collection);

        // then
        assertThatThrownBy(() -> collectionLikeCommandService.collectionLikes(saveCollection.getId(), 0L))
                .isInstanceOf(CollectionUserNotFoundException.class);
    }

    @Test
    @DisplayName("컬렉션 좋아요 취소를 성공적으로 수행하고 컬렉션의 좋아요 개수가 감소한다.")
    void collectionLikesCancel_whenValidRequest_cancelsLike() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionLikeCommandService.collectionLikes(collection.getId(), user.getId());
        collectionLikeCommandService.collectionLikesCancel(collection.getId(), user.getId());
        Optional<CollectionLikes> collectionLikes = collectionLikeRepository.findCollectionLikesByCollectionIdAndUserId(
                collection.getId(),
                user.getId()
        );

        // then
        assertThat(collectionLikes).isNotPresent();
        assertThat(collection.getLikeCounts()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 컬렉션 ID로 좋아요 취소를 시도하면 CollectionNotFoundException 예외가 발생한다.")
    void collectionLikesCancel_whenLikeNotFound_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");

        // when
        userRepository.save(user);

        // then
        assertThatThrownBy(() -> collectionLikeCommandService.collectionLikesCancel(999L, user.getId()))
                .isInstanceOf(CollectionNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 좋아요 취소를 시도하면 CollectionUserNotFoundException 예외가 발생한다.")
    void collectionLikesCancel_whenUserNotFound_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);

        // then
        assertThatThrownBy(() -> collectionLikeCommandService.collectionLikesCancel(collection.getId(), 999L))
                .isInstanceOf(CollectionUserNotFoundException.class);
    }
}