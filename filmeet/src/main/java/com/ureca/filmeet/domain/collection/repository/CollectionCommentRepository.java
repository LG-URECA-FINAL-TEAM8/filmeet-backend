package com.ureca.filmeet.domain.collection.repository;

import com.ureca.filmeet.domain.collection.entity.CollectionComment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectionCommentRepository extends JpaRepository<CollectionComment, Long> {

    @Query("SELECT cc " +
            "FROM CollectionComment cc " +
            "WHERE cc.id = :collectionCommentId AND cc.user.id = :userId")
    Optional<CollectionComment> findCollectionCommentWrittenUserBy(
            @Param("userId") Long userId,
            @Param("collectionCommentId") Long collectionCommentId
    );
}
