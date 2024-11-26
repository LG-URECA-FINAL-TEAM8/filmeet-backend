package com.ureca.filmeet.domain.review.entity;

import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewComment extends BaseEntity {

    @Id
    @Column(name = "review_comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    public void setReview(Review review) {
        // 기존 연관관계 제거
        if (this.review != null) {
            this.review.getReviewComments().remove(this);
        }

        this.review = review;
        if (review != null) {
            review.getReviewComments().add(this);
        }
    }

    @Builder
    public ReviewComment(String content, Review review, User user) {
        this.content = content;
        this.review = review;
        this.user = user;
    }

    public void modifyReviewComment(String content) {
        this.content = content;
    }
}