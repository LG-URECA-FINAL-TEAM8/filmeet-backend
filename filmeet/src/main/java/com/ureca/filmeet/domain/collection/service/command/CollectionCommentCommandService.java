package com.ureca.filmeet.domain.collection.service.command;

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
import com.ureca.filmeet.global.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollectionCommentCommandService {

    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;
    private final CollectionCommentRepository collectionCommentRepository;

    @DistributedLock(key = "'collectionComment:' + #collectionCommentCreateRequest.collectionId")
    public Long createCollectionComment(CollectionCommentCreateRequest collectionCommentCreateRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(CollectionUserNotFoundException::new);

        Collection collection = collectionRepository.findById(collectionCommentCreateRequest.collectionId())
                .orElseThrow(CollectionNotFoundException::new);

        CollectionComment collectionComment = CollectionComment.builder()
                .user(user)
                .collection(collection)
                .content(collectionCommentCreateRequest.commentContent())
                .build();
        CollectionComment savedCollection = collectionCommentRepository.save(collectionComment);

        collection.addCommentCounts();

        return savedCollection.getId();
    }

    @Transactional
    public Long modifyCollectionComment(CollectionCommentModifyRequest collectionCommentModifyRequest, Long userId) {
        CollectionComment collectionComment = collectionCommentRepository.findCollectionCommentWrittenUserBy(
                        userId, collectionCommentModifyRequest.collectionCommentId())
                .orElseThrow(CollectionCommentNotFoundException::new);

        collectionComment.modifyCollectionComment(collectionCommentModifyRequest.commentContent());

        return collectionComment.getId();
    }

    @DistributedLock(key = "'collectionComment:' + #collectionCommentDeleteRequest.collectionId")
    public void deleteCollectionComment(CollectionCommentDeleteRequest collectionCommentDeleteRequest, Long userId) {
        CollectionComment collectionComment = collectionCommentRepository.findCollectionCommentWrittenUserBy(
                        userId, collectionCommentDeleteRequest.collectionCommentId())
                .orElseThrow(CollectionCommentNotFoundException::new);
        collectionComment.delete();

        Collection collection = collectionRepository.findById(collectionCommentDeleteRequest.collectionId())
                .orElseThrow(CollectionNotFoundException::new);
        collection.decrementCommentCounts();
    }
}
