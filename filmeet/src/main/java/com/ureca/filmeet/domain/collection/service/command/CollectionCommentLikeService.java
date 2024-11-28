package com.ureca.filmeet.domain.collection.service.command;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionLikes;
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
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("no collection"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("no user"));

        boolean isAlreadyLiked = collectionLikeRepository.existsByCollectionIdAndUserId(collectionId, userId);
        if (isAlreadyLiked) {
            throw new RuntimeException(
                    String.format("Collection ID %d에 대해 User ID %d는 이미 좋아요를 눌렀습니다.", collectionId, userId));
        }

        CollectionLikes collectionLikes = CollectionLikes.builder()
                .collection(collection)
                .user(user)
                .build();
        collectionLikeRepository.save(collectionLikes);

        collection.addLikeCounts();
    }

    public void collectionLikesCancel(Long collectionId, Long userId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("해당 컬렉션이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        CollectionLikes collectionLikes = collectionLikeRepository.findCollectionLikesByCollectionIdAndUserId(
                        collection.getId(),
                        user.getId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("User ID %d는 Collection ID %d를 좋아요하지 않았습니다.", userId, collectionId)));

        collectionLikeRepository.delete(collectionLikes);

        collection.decrementLikesCounts();
    }
}
