package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
