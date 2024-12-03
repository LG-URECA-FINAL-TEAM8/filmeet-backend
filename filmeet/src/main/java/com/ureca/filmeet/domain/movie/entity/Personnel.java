package com.ureca.filmeet.domain.movie.entity;

import com.ureca.filmeet.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Personnel extends BaseTimeEntity {

    @Id
    @Column(name = "personnel_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer staffId;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 100)
    private String profileImage;

    @Builder
    public Personnel(Long id, Integer staffId, String name, String profileImage) {
        this.id = id;
        this.staffId = staffId;
        this.name = name;
        this.profileImage = profileImage;
    }
}