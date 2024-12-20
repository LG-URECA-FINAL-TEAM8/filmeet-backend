package com.ureca.filmeet.domain.collection.entity;

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
public class Collection extends BaseEntity {

    @Id
    @Column(name = "collection_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String title;

    @Column(length = 50, insertable = false, updatable = false)
    private String lowerTitle;

    @Column(length = 100)
    private String content;

    private Integer likeCounts = 0;

    private Integer commentCounts = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    @OneToMany(mappedBy = "collection")
    private List<CollectionComment> collectionComments = new ArrayList<>();

    @Builder
    public Collection(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void modifyCollection(String title, String content) {
        this.title = title;
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
}