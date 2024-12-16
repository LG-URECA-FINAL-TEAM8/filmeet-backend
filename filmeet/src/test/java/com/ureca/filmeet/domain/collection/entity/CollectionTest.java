package com.ureca.filmeet.domain.collection.entity;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createCollectionComment;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class CollectionTest {

    @Test
    @DisplayName("컬렉션이 정상적으로 생성된다.")
    void createCollection_successfullyCreatesCollection() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");

        // when
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // then
        assertThat(collection)
                .extracting("title", "content", "user")
                .contains("컬렉션 제목", "컬렉션 내용", user);
        assertThat(collection.getLikeCounts()).isZero();
        assertThat(collection.getCommentCounts()).isZero();
        assertThat(collection.getCollectionComments()).isEmpty();
    }

    @Test
    @DisplayName("컬렉션 제목과 내용을 수정한다.")
    void modifyCollection_updatesTitleAndContent() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("기존 제목", "기존 내용", user);

        // when
        collection.modifyCollection("수정된 제목", "수정된 내용");

        // then
        assertThat(collection)
                .extracting("title", "content")
                .contains("수정된 제목", "수정된 내용");
    }

    @Test
    @DisplayName("댓글 수가 정상적으로 증가한다.")
    void addCommentCounts_incrementsCommentCounts() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        collection.addCommentCounts();
        collection.addCommentCounts();

        // then
        assertThat(collection.getCommentCounts()).isEqualTo(2);
    }

    @Test
    @DisplayName("댓글 수가 정상적으로 감소한다.")
    void decrementCommentCounts_decrementsCommentCounts() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        collection.addCommentCounts();
        collection.addCommentCounts();

        // when
        collection.decrementCommentCounts();

        // then
        assertThat(collection.getCommentCounts()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 수가 0일 때 감소를 시도해도 0 이하로 내려가지 않는다.")
    void decrementCommentCounts_doesNotGoBelowZero() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        collection.decrementCommentCounts();

        // then
        assertThat(collection.getCommentCounts()).isZero();
    }

    @Test
    @DisplayName("좋아요 수가 정상적으로 증가한다.")
    void addLikeCounts_incrementsLikeCounts() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        collection.addLikeCounts();
        collection.addLikeCounts();

        // then
        assertThat(collection.getLikeCounts()).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요 수가 정상적으로 감소한다.")
    void decrementLikesCounts_decrementsLikeCounts() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        collection.addLikeCounts();
        collection.addLikeCounts();

        // when
        collection.decrementLikesCounts();

        // then
        assertThat(collection.getLikeCounts()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 수가 0일 때 감소를 시도해도 0 이하로 내려가지 않는다.")
    void decrementLikesCounts_doesNotGoBelowZero() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        collection.decrementLikesCounts();

        // then
        assertThat(collection.getLikeCounts()).isZero();
    }

    @Test
    @DisplayName("댓글 목록이 초기화 상태에서 댓글이 추가된다.")
    void collectionComments_initialStateAndAddition() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionComment comment = createCollectionComment("댓글 내용", user, null);

        // when
        collection.getCollectionComments().add(comment);

        // then
        assertThat(collection.getCollectionComments())
                .hasSize(1)
                .extracting("content")
                .containsExactly("댓글 내용");
    }
}