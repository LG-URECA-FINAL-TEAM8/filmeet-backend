package com.ureca.filmeet.domain.genre.repository;

import com.ureca.filmeet.domain.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
