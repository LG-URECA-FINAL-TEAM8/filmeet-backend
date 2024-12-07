package com.ureca.filmeet.domain.user.entity;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.genre.entity.GenreScore;
import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import com.ureca.filmeet.domain.movie.entity.MovieRatings;
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
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Role role;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(length = 20)
    private String nickname;

    private Integer age = 0;

    @Column(length = 4)
    private String mbti;

    private String profileImage;

    private int likeActivityScore = 3;

    private int collectionActivityScore = 3;

    private int gameActivityScore = 3;

    private int totalMovieLikes = 0;

    private int totalCollections = 0;

    private int totalGames = 0;

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

    public User(Long memberId, int totalMovieLikes, int totalCollections, int totalGames) {
        this.id = memberId;
        this.totalMovieLikes = totalMovieLikes;
        this.totalCollections = totalCollections;
        this.totalGames = totalGames;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updatePreference(String mbti, Integer age) {
        this.mbti = mbti;
        this.age = age;
    }

    public void addTotalMovieLikes() {
        this.totalMovieLikes++;
    }

    public void decrementTotalMovieLikes() {
        if (this.totalMovieLikes > 0) {
            this.totalMovieLikes--;
        }
    }

    public void addTotalCollections() {
        this.totalCollections++;
    }

    public void decrementTotalCollections() {
        if (this.totalCollections > 0) {
            this.totalCollections--;
        }
    }

    public void adjustActivityScores(
            int maxLikes, int minLikes, int maxGames, int minGames, int maxCollections, int minCollections,
            double averageLikeCount, double averageGameCount, double averageCollectionCount
    ) {

        validateMaxMinValues(maxLikes, minLikes);
        validateMaxMinValues(maxGames, minGames);
        validateMaxMinValues(maxCollections, minCollections);

        likeActivityScore = calculateScore(totalMovieLikes, maxLikes, minLikes, averageLikeCount);
        gameActivityScore = calculateScore(totalGames, maxGames, minGames, averageGameCount);
        collectionActivityScore = calculateScore(totalCollections, maxCollections, minCollections,
                averageCollectionCount);
    }

    private int calculateScore(int actionCount, int maxAction, int minAction, double averageActionCount) {
        final int MAX_SCORE = 5;
        final int MIN_SCORE = 1;
        final double MIN_RATIO = 0.7;

        if (maxAction == minAction) {
            return 3;
        }

        // 최대/최소값 기반 점수 계산
        double weight = (double) (maxAction - actionCount) / (maxAction - minAction);

        // 평균값 기반 비율을 함께 반영
        double ratio = MIN_RATIO + (0.3 * Math.min((double) actionCount / averageActionCount, 1.0));

        double adjustedScore = (MIN_SCORE + (4 * weight)) * ratio;

        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, (int) Math.round(adjustedScore)));
    }

    private void validateMaxMinValues(int maxValue, int minValue) {
        if (maxValue < minValue) {
            throw new IllegalArgumentException("최대값은 최소값보다 작을 수 없습니다.");
        }
    }
}