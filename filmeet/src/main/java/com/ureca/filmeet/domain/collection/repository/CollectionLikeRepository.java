package com.ureca.filmeet.domain.collection.repository;

import com.ureca.filmeet.domain.collection.entity.CollectionLikes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionLikeRepository extends JpaRepository<CollectionLikes, Long> {

    Optional<CollectionLikes> findCollectionLikesByCollectionIdAndUserId(Long collectionId, Long userId);

    boolean existsByCollectionIdAndUserId(Long collectionId, Long userId);
}
