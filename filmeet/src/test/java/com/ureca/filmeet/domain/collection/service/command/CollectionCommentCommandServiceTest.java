package com.ureca.filmeet.domain.collection.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createCollectionComment;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ureca.filmeet.domain.collection.dto.request.CollectionCommentCreateRequest;
import com.ureca.filmeet.domain.collection.dto.request.CollectionCommentDeleteRequest;
import com.ureca.filmeet.domain.collection.dto.request.CollectionCommentModifyRequest;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionComment;
import com.ureca.filmeet.domain.collection.exception.CollectionCommentNotFoundException;
import com.ureca.filmeet.domain.collection.exception.CollectionNotFoundException;
import com.ureca.filmeet.domain.collection.exception.CollectionUserNotFoundException;
import com.ureca.filmeet.domain.collection.repository.CollectionCommentRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
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
class CollectionCommentCommandServiceTest {

    @Autowired
    private CollectionCommentCommandService collectionCommentCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionCommentRepository collectionCommentRepository;

    @DisplayName("사용자가 컬렉션에 댓글을 성공적으로 작성한다.")
    @Test
    void createCollectionComment_whenValidRequest_savesComment() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        CollectionCommentCreateRequest request = new CollectionCommentCreateRequest(collection.getId(), "댓글 내용");
        Long commentId = collectionCommentCommandService.createCollectionComment(request, user.getId());
        Optional<CollectionComment> savedComment = collectionCommentRepository.findById(commentId);

        // then
        assertThat(savedComment)
                .isPresent()
                .get()
                .extracting("id", "content", "user", "collection", "collection.commentCounts")
                .contains(commentId, request.commentContent(), user, collection, 1
                );
    }

    @Test
    @DisplayName("존재하지 않는 유저가 댓글을 작성하려고 하면 CollectionUserNotFoundException 이 발생한다.")
    void createCollectionComment_whenUserNotFound_throwsException() {
        // given
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", null);

        // when
        collectionRepository.save(collection);
        CollectionCommentCreateRequest request = new CollectionCommentCreateRequest(collection.getId(), "댓글 내용");

        // then
        assertThatThrownBy(() -> collectionCommentCommandService.createCollectionComment(request, 0L))
                .isInstanceOf(CollectionUserNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 컬렉션에 댓글을 작성하려고 하면 CollectionNotFoundException 이 발생한다.")
    void createCollectionComment_whenCollectionNotFound_throwsException() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        CollectionCommentCreateRequest request = new CollectionCommentCreateRequest(999L, "댓글 내용");

        // when
        userRepository.save(user);

        // then
        assertThatThrownBy(() -> collectionCommentCommandService.createCollectionComment(request, user.getId()))
                .isInstanceOf(CollectionNotFoundException.class);
    }

    @Test
    @DisplayName("사용자가 자신의 댓글을 성공적으로 수정한다.")
    void modifyCollectionComment_whenValidRequest_updatesComment() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionComment comment = createCollectionComment("댓글 내용", user, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionCommentRepository.save(comment);
        CollectionCommentModifyRequest request = new CollectionCommentModifyRequest(comment.getId(), "수정된 댓글 내용");
        Long updatedCommentId = collectionCommentCommandService.modifyCollectionComment(request, user.getId());
        Optional<CollectionComment> updatedComment = collectionCommentRepository.findById(updatedCommentId);

        // then
        assertThat(updatedComment)
                .isPresent()
                .get()
                .extracting("id", "content", "user", "collection", "collection.commentCounts")
                .contains(updatedComment.get().getId(), request.commentContent(), user, collection, 0
                );
    }

    @Test
    @DisplayName("사용자가 존재하지 않는 댓글을 수정하려고 하면 CollectionCommentNotFoundException 이 발생한다.")
    void modifyCollectionComment_whenCommentNotFound_throwsException() {
        // given
        CollectionCommentModifyRequest request = new CollectionCommentModifyRequest(999L, "수정된 댓글 내용");

        // when & then
        assertThatThrownBy(() -> collectionCommentCommandService.modifyCollectionComment(request, 1L))
                .isInstanceOf(CollectionCommentNotFoundException.class);
    }

    @Test
    @DisplayName("사용자가 자신의 댓글을 성공적으로 삭제한다.")
    void deleteCollectionComment_whenValidRequest_deletesComment() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionComment comment = createCollectionComment("댓글 내용", user, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionCommentRepository.save(comment);
        CollectionCommentDeleteRequest request = new CollectionCommentDeleteRequest(collection.getId(),
                comment.getId());
        collectionCommentCommandService.deleteCollectionComment(request, user.getId());
        Optional<CollectionComment> deletedComment = collectionCommentRepository.findById(comment.getId());

        // then
        assertThat(deletedComment).isPresent();
        assertThat(deletedComment.get().getIsDeleted()).isTrue();
        assertThat(collection.getCommentCounts()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자가 존재하지 않는 댓글을 삭제하려고 하면 CollectionCommentNotFoundException 이 발생한다.")
    void deleteCollectionComment_whenCommentNotFound_throwsException() {
        // given
        CollectionCommentDeleteRequest request = new CollectionCommentDeleteRequest(1L, 999L);

        // when & then
        assertThatThrownBy(() -> collectionCommentCommandService.deleteCollectionComment(request, 1L))
                .isInstanceOf(CollectionCommentNotFoundException.class);
    }

    @Test
    @DisplayName("사용자가 존재하지 않는 컬렉션의 댓글을 삭제하려고 하면 CollectionNotFoundException 이 발생한다.")
    void deleteCollectionComment_whenCollectionNotFound_throwsException() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", user);
        CollectionComment comment = createCollectionComment("댓글 내용", user, collection);

        // when
        userRepository.save(user);
        collectionRepository.save(collection);
        collectionCommentRepository.save(comment);
        CollectionCommentDeleteRequest request = new CollectionCommentDeleteRequest(100L, comment.getId());

        // then
        assertThatThrownBy(() -> collectionCommentCommandService.deleteCollectionComment(request, user.getId()))
                .isInstanceOf(CollectionNotFoundException.class);
    }
}