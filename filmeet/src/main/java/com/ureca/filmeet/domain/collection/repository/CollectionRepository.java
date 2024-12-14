package com.ureca.filmeet.domain.collection.repository;

import com.ureca.filmeet.domain.collection.entity.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    @Query("SELECT c " +
            "FROM Collection c " +
            "WHERE c.id = :collectionId AND c.isDeleted = false ")
    Optional<Collection> findCollectionBy(@Param("collectionId") Long collectionId);

    @Query("SELECT c " +
            "FROM Collection c " +
            "JOIN FETCH c.user u " +
            "WHERE c.user.id = :userId AND c.isDeleted = false ")
    Slice<Collection> findCollectionsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c " +
            "FROM Collection c " +
            "JOIN FETCH  c.user u " +
            "WHERE c.id = :collectionId AND c.isDeleted = false ")
    Optional<Collection> findCollectionByCollectionIdAndUserId(
            @Param("collectionId") Long collectionId
    );

    @Query(value = "SELECT c.collection_id, u.member_id, c.title, c.content, u.nickname, u.profile_image " +
            "FROM collection c " +
            "JOIN member u ON u.member_id = c.member_id " +
            "WHERE MATCH(c.lower_title) AGAINST(:titleKeyword IN NATURAL LANGUAGE MODE) " +
            "AND c.is_deleted = false " +
            "ORDER BY c.created_at DESC",
            countQuery = "SELECT COUNT(c.collection_id) " +
                    "FROM collection c " +
                    "WHERE MATCH(c.lower_title) AGAINST(:titleKeyword IN NATURAL LANGUAGE MODE) " +
                    "AND c.is_deleted = false",
            nativeQuery = true)
    Page<Object[]> findCollectionsByTitleKeyword(@Param("titleKeyword") String titleKeyword, Pageable pageable);



}