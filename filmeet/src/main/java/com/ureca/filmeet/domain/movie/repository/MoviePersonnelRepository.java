package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.MoviePersonnel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoviePersonnelRepository extends JpaRepository<MoviePersonnel, Long> {
}
