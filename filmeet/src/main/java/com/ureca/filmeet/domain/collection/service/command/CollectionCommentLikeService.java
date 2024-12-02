package com.ureca.filmeet.domain.collection.service.command;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionLikes;
import com.ureca.filmeet.domain.collection.exception.CollectionLikeAlreadyExistsException;
import com.ureca.filmeet.domain.collection.exception.CollectionLikeNotFoundException;
import com.ureca.filmeet.domain.collection.exception.CollectionNotFoundException;
import com.ureca.filmeet.domain.collection.exception.CollectionUserNotFoundException;
import com.ureca.filmeet.domain.collection.repository.CollectionLikeRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CollectionCommentLikeService {

    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;
    private final CollectionLikeRepository collectionLikeRepository;

    public void collectionLikes(Long collectionId, Long userId) {
        boolean isAlreadyLiked = collectionLikeRepository.existsByCollectionIdAndUserId(collectionId, userId);
        if (isAlreadyLiked) {
            throw new CollectionLikeAlreadyExistsException();
        }

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(CollectionUserNotFoundException::new);

        CollectionLikes collectionLikes = CollectionLikes.builder()
                .collection(collection)
                .user(user)
                .build();
        collectionLikeRepository.save(collectionLikes);

        collection.addLikeCounts();
    }

    public void collectionLikesCancel(Long collectionId, Long userId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(CollectionUserNotFoundException::new);

        CollectionLikes collectionLikes = collectionLikeRepository.findCollectionLikesByCollectionIdAndUserId(
                        collection.getId(),
                        user.getId())
                .orElseThrow(CollectionLikeNotFoundException::new);

        collectionLikeRepository.delete(collectionLikes);

        collection.decrementLikesCounts();
    }
}
