package com.ureca.filmeet.domain.collection.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.global.util.string.BadWordService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CollectionCommentCommandServiceTest {


    @InjectMocks
    private CollectionCommentCommandService collectionCommentCommandService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private CollectionCommentRepository collectionCommentRepository;

    @Mock
    private BadWordService badWordService;

    @DisplayName("사용자가 컬렉션에 댓글을 성공적으로 작성하고 욕성 필터링이 동작한다.")
    @Test
    void createCollectionComment_whenValidRequest_savesComment() {
        // given
        String commentContent = "This is a comment";
        String maskedCommentContent = "This is a ****";
        User user = mock(User.class);
        Collection collection = mock(Collection.class);
        CollectionComment mockComment = new CollectionComment(maskedCommentContent, user, collection);
        CollectionCommentCreateRequest request = new CollectionCommentCreateRequest(collection.getId(), commentContent);

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(collectionRepository.findById(collection.getId())).thenReturn(Optional.of(collection));
        when(badWordService.maskText(commentContent)).thenReturn(maskedCommentContent);
        when(collectionCommentRepository.save(any(CollectionComment.class))).thenReturn(mockComment);
        Long commentId = collectionCommentCommandService.createCollectionComment(request, user.getId());

        // then
        verify(badWordService).maskText(commentContent);
        verify(collectionCommentRepository).save(any(CollectionComment.class));
        assertThat(commentId).isEqualTo(mockComment.getId());
        assertThat(mockComment.getContent()).isEqualTo(maskedCommentContent);
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
        Long nonExistentCollectionId = 999L;
        User user = mock(User.class);
        CollectionCommentCreateRequest request = new CollectionCommentCreateRequest(nonExistentCollectionId, "댓글 내용");

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(collectionRepository.findById(nonExistentCollectionId)).thenReturn(Optional.empty()); // 컬렉션이 없을 경우

        // then
        assertThatThrownBy(() -> collectionCommentCommandService.createCollectionComment(request, user.getId()))
                .isInstanceOf(CollectionNotFoundException.class);
    }

    @Test
    @DisplayName("사용자가 자신의 댓글을 성공적으로 수정한다.")
    void modifyCollectionComment_shouldModifyCommentSuccessfully() {
        // given
        Long userId = 1L;
        Long commentId = 2L;
        String modifiedComment = "Modified Comment";
        String maskedComment = "**** Comment";

        CollectionCommentModifyRequest request = new CollectionCommentModifyRequest(commentId, modifiedComment);
        CollectionComment collectionComment = mock(CollectionComment.class);

        // when
        when(collectionCommentRepository.findCollectionCommentWrittenUserBy(userId, commentId))
                .thenReturn(Optional.of(collectionComment));
        when(badWordService.maskText(modifiedComment)).thenReturn(maskedComment);
        collectionCommentCommandService.modifyCollectionComment(request, userId);

        // then
        verify(collectionCommentRepository).findCollectionCommentWrittenUserBy(userId, commentId);
        verify(badWordService).maskText(modifiedComment);
        verify(collectionComment).modifyCollectionComment(maskedComment);
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
    void deleteCollectionComment_shouldDeleteCommentSuccessfully() {
        // given
        Long userId = 1L;
        Long commentId = 2L;
        Long collectionId = 3L;

        CollectionCommentDeleteRequest request = new CollectionCommentDeleteRequest(collectionId, commentId);

        CollectionComment collectionComment = mock(CollectionComment.class);
        Collection collection = mock(Collection.class);

        // when
        when(collectionCommentRepository.findCollectionCommentWithCollectionBy(userId, commentId))
                .thenReturn(Optional.of(collectionComment));
        when(collectionComment.getCollection()).thenReturn(collection);
        collectionCommentCommandService.deleteCollectionComment(request, userId);

        // then
        verify(collectionCommentRepository).findCollectionCommentWithCollectionBy(userId, commentId);
        verify(collectionComment).delete();
        verify(collection).decrementCommentCounts();
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
}