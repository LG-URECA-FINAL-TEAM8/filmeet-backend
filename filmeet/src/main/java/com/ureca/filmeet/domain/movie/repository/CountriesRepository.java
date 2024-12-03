package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountriesRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByNation(String nation);

    List<Country> findByNationIn(List<String> nations);
}
