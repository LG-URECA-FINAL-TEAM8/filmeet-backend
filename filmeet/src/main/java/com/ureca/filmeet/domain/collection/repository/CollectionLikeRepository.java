package com.ureca.filmeet.domain.collection.repository;

import com.ureca.filmeet.domain.collection.entity.CollectionLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionLikeRepository extends JpaRepository<CollectionLikes, Long> {

    boolean existsByCollectionIdAndUserId(Long collectionId, Long userId);
}
