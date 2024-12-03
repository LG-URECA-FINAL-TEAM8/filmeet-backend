package com.ureca.filmeet.domain.user.entity;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.genre.entity.GenreScore;
import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // 사용자 역할 (예: ROLE_USER, ROLE_ADMIN)

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(length = 20)
    private String nickname;

    private Integer age = 0;

    @Column(length = 4)
    private String mbti;

    private String profileImage;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Collection> collections = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<MovieLikes> movieLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<GenreScore> genreScores = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<MovieRatings> movieRatings = new ArrayList<>();

    @Builder
    public User(Long id, String username, String password, Role role, Provider provider, String nickname,
                String profileImage) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updatePreference(String mbti, Integer age) {
        this.mbti = mbti;
        this.age = age;
    }
}