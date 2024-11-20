package com.ureca.filmeet.domain.collection.repository;

import com.ureca.filmeet.domain.collection.entity.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    @Query("SELECT c " +
            "FROM Collection c " +
            "JOIN FETCH c.user u " +
            "WHERE c.user.id = :userId AND c.isDeleted = false ")
    Slice<Collection> findCollectionsByUserId(@Param("userId") Long userId, Pageable pageable);
}