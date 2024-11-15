package com.ureca.filmeet.domain.member.entity;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.genre.entity.GenreScore;
import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 50, nullable = false)
    private String username;

    @Column(length = 255)
    private String password;

    @Column(length = 20)
    private String provider;

    @Column(length = 50)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 100)
    private String profileImage;

    @OneToMany(mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Collection> collections = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MovieLikes> movieLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<GenreScore> genreScores = new ArrayList<>();
}