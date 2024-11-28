package com.ureca.filmeet.domain.collection.entity;

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

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollectionComment extends BaseEntity {

    @Id
    @Column(name = "collection_comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private Collection collection;

    private String content;

    @Builder
    public CollectionComment(String content, User user, Collection collection) {
        this.content = content;
        this.user = user;
        setCollection(collection);
    }

    public void setCollection(Collection collection) {
        // 기존 연관관계 제거
        if (this.collection != null) {
            this.collection.getCollectionComments().remove(this);
        }

        this.collection = collection;
        if (collection != null) {
            collection.getCollectionComments().add(this);
        }
    }
}
