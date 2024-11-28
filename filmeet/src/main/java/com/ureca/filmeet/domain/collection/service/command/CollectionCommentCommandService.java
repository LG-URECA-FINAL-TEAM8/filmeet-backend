package com.ureca.filmeet.domain.collection.service.command;

import com.ureca.filmeet.domain.collection.dto.request.CollectionCommentCreateRequest;
import com.ureca.filmeet.domain.collection.dto.request.CollectionCommentModifyRequest;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionComment;
import com.ureca.filmeet.domain.collection.repository.CollectionCommentRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CollectionCommentCommandService {

    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;
    private final CollectionCommentRepository collectionCommentRepository;

    public Long createCollectionComment(CollectionCommentCreateRequest collectionCommentCreateRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("no user"));

        Collection collection = collectionRepository.findById(collectionCommentCreateRequest.collectionId())
                .orElseThrow(() -> new RuntimeException("no collection"));

        CollectionComment collectionComment = CollectionComment.builder()
                .user(user)
                .collection(collection)
                .content(collectionCommentCreateRequest.commentContent())
                .build();
        CollectionComment savedCollection = collectionCommentRepository.save(collectionComment);

        collection.addCommentCounts();

        return savedCollection.getId();
    }

    public Long modifyCollectionComment(CollectionCommentModifyRequest collectionCommentModifyRequest, Long userId) {
        CollectionComment collectionComment = collectionCommentRepository.findCollectionCommentWrittenUserBy(
                        userId, collectionCommentModifyRequest.collectionCommentId())
                .orElseThrow(() -> new RuntimeException("사용자에 의해 작성된 수정할 댓글이 없습니다. "));

        collectionComment.modifyCollectionComment(collectionCommentModifyRequest.commentContent());

        return collectionComment.getId();
    }
}
