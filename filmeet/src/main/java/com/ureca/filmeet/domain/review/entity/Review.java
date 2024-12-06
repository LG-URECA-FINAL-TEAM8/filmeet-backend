package com.ureca.filmeet.domain.review.entity;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String content;

    private Integer likeCounts = 0;

    private Integer commentCounts = 0;

    private Boolean isVisible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    @OneToMany(mappedBy = "review")
    private List<ReviewComment> reviewComments = new ArrayList<>();

    @Builder
    public Review(String content, Movie movie, User user, Integer likeCounts, Integer commentCounts) {
        this.content = content;
        this.movie = movie;
        this.user = user;
        this.likeCounts = (likeCounts != null) ? likeCounts : 0;
        this.commentCounts = (commentCounts != null) ? commentCounts : 0;
    }

    public void modifyReview(String content) {
        this.content = content;
    }

    public void addCommentCounts() {
        this.commentCounts++;
    }

    public void decrementCommentCounts() {
        if (this.commentCounts > 0) {
            this.commentCounts--;
        }
    }

    public void addLikeCounts() {
        this.likeCounts++;
    }

    public void decrementLikesCounts() {
        if (this.likeCounts > 0) {
            this.likeCounts--;
        }
    }

    public void blind() {
        this.isVisible = false;
    }
}