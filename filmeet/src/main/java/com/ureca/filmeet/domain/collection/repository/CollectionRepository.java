package com.ureca.filmeet.domain.collection.repository;

import com.ureca.filmeet.domain.collection.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
