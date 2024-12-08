package com.ureca.filmeet.domain.collection.repository;

import com.ureca.filmeet.domain.collection.entity.CollectionComment;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectionCommentRepository extends JpaRepository<CollectionComment, Long> {

    @Query("SELECT cc " +
            "FROM CollectionComment cc " +
            "WHERE cc.id = :collectionCommentId AND cc.user.id = :userId AND cc.isDeleted = false ")
    Optional<CollectionComment> findCollectionCommentWrittenUserBy(
            @Param("userId") Long userId,
            @Param("collectionCommentId") Long collectionCommentId
    );

    @Query("""  
            SELECT cc
            FROM CollectionComment cc
            JOIN FETCH cc.user u
            JOIN cc.collection c
            WHERE c.id = :collectionId AND c.isDeleted = false AND cc.isDeleted = false
            """)
    Slice<CollectionComment> findCommentsBy(
            @Param("collectionId") Long collectionId,
            Pageable pageable
    );

    @Query("SELECT cc " +
            "FROM CollectionComment cc " +
            "JOIN FETCH cc.collection c " +
            "WHERE cc.id = :collectionCommentId AND cc.user.id = :userId AND cc.isDeleted = false ")
    Optional<CollectionComment> findCollectionCommentWithCollectionBy(
            @Param("userId") Long userId,
            @Param("collectionCommentId") Long collectionCommentId
    );
}
