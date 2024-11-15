package com.ureca.filmeet.domain.movie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Countries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long countriesId;

    @Column(length = 50, nullable = false)
    private String name;

    @OneToMany(mappedBy = "countries")
    private List<MovieCountries> movieCountries = new ArrayList<>();
}
