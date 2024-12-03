package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {
}
