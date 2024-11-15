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
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personnelId;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 100)
    private String profileImage;

    @OneToMany(mappedBy = "personnel")
    private List<MoviePersonnel> moviePersonnel = new ArrayList<>();
}