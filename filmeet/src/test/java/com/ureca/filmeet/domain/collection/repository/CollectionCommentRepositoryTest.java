package com.ureca.filmeet.domain.collection.repository;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createCollectionComment;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionComment;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
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
class CollectionCommentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionCommentRepository collectionCommentRepository;

    @DisplayName("사용자가 작성한 컬렉션 댓글 정보를 성공적으로 조회한다.")
    @Test
    void findCollectionCommentWrittenByUser_whenValidRequest_returnsComment() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionComment collectionComment = createCollectionComment("컬렉션 댓글 내용", user, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionCommentRepository.save(collectionComment);
        Optional<CollectionComment> collectionCommentWrittenUser = collectionCommentRepository.findCollectionCommentWrittenUserBy(
                user.getId(), collectionComment.getId());

        // then
        assertThat(collectionCommentWrittenUser)
                .isPresent()
                .get()
                .extracting("id", "content", "collection", "user")
                .contains(
                        collectionComment.getId(), collectionComment.getContent(), collection, user
                );
    }

    @DisplayName("존재하지 않는 사용자 ID 또는 댓글 ID로 컬렉션 댓글을 조회하면 결과가 없다.")
    @Test
    void findCollectionCommentWrittenByUser_whenInvalidIds_returnsEmpty() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionComment collectionComment = createCollectionComment("컬렉션 댓글 내용", user, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionCommentRepository.save(collectionComment);
        Optional<CollectionComment> collectionCommentWrittenUser1 = collectionCommentRepository.findCollectionCommentWrittenUserBy(
                1000L, collectionComment.getId());
        Optional<CollectionComment> collectionCommentWrittenUser2 = collectionCommentRepository.findCollectionCommentWrittenUserBy(
                user.getId(), 1000L);

        // then
        assertThat(collectionCommentWrittenUser1).isNotPresent();
        assertThat(collectionCommentWrittenUser2).isNotPresent();
    }

    @DisplayName("컬렉션 댓글과 댓글을 작성한 유저 정보를 성공적으로 조회한다.")
    @Test
    void findCollectionCommentsWithUsers_whenValidRequest_returnsComments() {
        // given
        User user1 = createUser("username1", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임1",
                "https://example.com/profile.jpg");
        User user2 = createUser("username2", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임2",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user1);
        CollectionComment collectionComment1 = createCollectionComment("컬렉션 댓글 내용1", user1, collection);
        CollectionComment collectionComment2 = createCollectionComment("컬렉션 댓글 내용2", user2, collection);

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        collectionRepository.save(collection);
        collectionCommentRepository.save(collectionComment1);
        collectionCommentRepository.save(collectionComment2);
        Pageable pageable = PageRequest.of(0, 10);
        Slice<CollectionComment> comments = collectionCommentRepository.findCommentsBy(collection.getId(), pageable);

        // then
        assertThat(comments.getContent())
                .hasSize(2)
                .extracting("id", "user", "collection", "content")
                .containsExactlyInAnyOrder(
                        tuple(
                                collectionComment1.getId(), user1, collection, collectionComment1.getContent()
                        ),
                        tuple(
                                collectionComment2.getId(), user2, collection, collectionComment2.getContent()
                        )
                );
    }

    @DisplayName("존재하지 않는 컬렉션 ID로 댓글을 조회하면 결과가 없다.")
    @Test
    void findCommentsByCollection_whenInvalidCollectionId_returnsEmpty() {
        // given
        User user1 = createUser("username1", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임1",
                "https://example.com/profile.jpg");
        User user2 = createUser("username2", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임2",
                "https://example.com/profile.jpg");
        CollectionComment collectionComment1 = createCollectionComment("컬렉션 댓글 내용1", user1, null);
        CollectionComment collectionComment2 = createCollectionComment("컬렉션 댓글 내용2", user2, null);

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        collectionCommentRepository.save(collectionComment1);
        collectionCommentRepository.save(collectionComment2);
        Pageable pageable = PageRequest.of(0, 10);
        Slice<CollectionComment> comments = collectionCommentRepository.findCommentsBy(null, pageable);

        // then
        assertThat(comments.getContent()).isEmpty();
    }

    @DisplayName("댓글 ID가 존재하지만 다른 사용자가 작성한 경우 조회되지 않는다.")
    @Test
    void findCollectionCommentWrittenUserBy_whenCommentNotWrittenByUser_returnsEmpty() {
        // given
        User user1 = createUser("user1", "password", Role.ROLE_USER, Provider.NAVER, "닉네임1",
                "https://example.com/profile1.jpg");
        User user2 = createUser("user2", "password", Role.ROLE_USER, Provider.NAVER, "닉네임2",
                "https://example.com/profile2.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user1);
        CollectionComment collectionComment = createCollectionComment("댓글 내용", user2, collection);

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        collectionRepository.save(collection);
        collectionCommentRepository.save(collectionComment);
        Optional<CollectionComment> result = collectionCommentRepository.findCollectionCommentWrittenUserBy(
                user1.getId(), collectionComment.getId());

        // then
        assertThat(result).isNotPresent();
    }

    @DisplayName("컬렉션에 댓글이 없는 경우 빈 결과를 반환한다.")
    @Test
    void findCommentsBy_whenNoComments_returnsEmpty() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        Pageable pageable = PageRequest.of(0, 10);
        Slice<CollectionComment> result = collectionCommentRepository.findCommentsBy(collection.getId(), pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @DisplayName("컬렉션에 댓글이 여러 개 있는 경우 올바르게 페이징한다.")
    @Test
    void findCommentsBy_whenMultiplePages_returnsPagedComments() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        List<CollectionComment> comments = IntStream.range(0, 15)
                .mapToObj(i -> createCollectionComment("댓글 내용 " + i, user, collection))
                .toList();

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionCommentRepository.saveAll(comments);
        Pageable pageable = PageRequest.of(0, 10);
        Slice<CollectionComment> firstPage = collectionCommentRepository.findCommentsBy(collection.getId(), pageable);
        Pageable secondPageable = PageRequest.of(1, 10);
        Slice<CollectionComment> secondPage = collectionCommentRepository.findCommentsBy(collection.getId(),
                secondPageable);

        // then
        assertThat(firstPage.getContent())
                .hasSize(10)
                .extracting("content")
                .containsExactlyInAnyOrder(
                        "댓글 내용 0", "댓글 내용 1", "댓글 내용 2", "댓글 내용 3", "댓글 내용 4",
                        "댓글 내용 5", "댓글 내용 6", "댓글 내용 7", "댓글 내용 8", "댓글 내용 9"
                );

        assertThat(secondPage.getContent())
                .hasSize(5)
                .extracting("content")
                .containsExactlyInAnyOrder("댓글 내용 10", "댓글 내용 11", "댓글 내용 12", "댓글 내용 13", "댓글 내용 14");
    }

    @DisplayName("댓글 작성자가 없는 댓글은 조회되지 않는다.")
    @Test
    void findCommentsBy_whenCommentWithoutUser_returnsEmpty() {
        // given
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", null);
        CollectionComment comment = createCollectionComment("댓글 내용", null, collection);

        // when
        collectionRepository.save(collection);
        collectionCommentRepository.save(comment);
        Pageable pageable = PageRequest.of(0, 10);
        Slice<CollectionComment> result = collectionCommentRepository.findCommentsBy(collection.getId(), pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @DisplayName("삭제된 컬렉션의 댓글은 조회되지 않는다.")
    @Test
    void findCommentsBy_whenCollectionDeleted_returnsEmpty() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionComment comment = createCollectionComment("댓글 내용", user, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionCommentRepository.save(comment);
        collection.delete();

        Pageable pageable = PageRequest.of(0, 10);
        Slice<CollectionComment> result = collectionCommentRepository.findCommentsBy(collection.getId(), pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @DisplayName("삭제된 댓글은 조회되지 않는다.")
    @Test
    void findCommentsBy_whenCollectionCommentDeleted_returnsEmpty() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionComment comment = createCollectionComment("댓글 내용", user, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionCommentRepository.save(comment);
        comment.delete();

        Pageable pageable = PageRequest.of(0, 10);
        Slice<CollectionComment> result = collectionCommentRepository.findCommentsBy(collection.getId(), pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @DisplayName("컬렉션 댓글 중 일부가 삭제된 경우 남아있는 댓글만 조회된다.")
    @Test
    void findCommentsBy_whenSomeCollectionCommentsDeleted_returnsRemainingComments() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionComment comment1 = createCollectionComment("댓글 내용1", user, collection);
        CollectionComment comment2 = createCollectionComment("댓글 내용2", user, collection);
        CollectionComment comment3 = createCollectionComment("댓글 내용3", user, collection);
        CollectionComment comment4 = createCollectionComment("댓글 내용4", user, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionCommentRepository.save(comment1);
        collectionCommentRepository.save(comment2);
        collectionCommentRepository.save(comment3);
        collectionCommentRepository.save(comment4);
        comment1.delete();
        comment2.delete();

        Pageable pageable = PageRequest.of(0, 10);
        Slice<CollectionComment> result = collectionCommentRepository.findCommentsBy(collection.getId(), pageable);

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .extracting("id", "user", "collection", "content")
                .containsExactlyInAnyOrder(
                        tuple(
                                comment3.getId(), user, collection, comment3.getContent()
                        ),
                        tuple(
                                comment4.getId(), user, collection, comment4.getContent()
                        )
                );
    }
}