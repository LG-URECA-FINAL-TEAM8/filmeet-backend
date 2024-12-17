package com.ureca.filmeet.domain.follow.repository;

import com.ureca.filmeet.domain.follow.entity.Follow;
import com.ureca.filmeet.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 팔로우 관계 확인
    @Query("SELECT COUNT(f) > 0 FROM Follow f WHERE f.follower = :follower AND f.following = :following")
    boolean existsByFollowerAndFollowing(@Param("follower") User follower, @Param("following") User following);

    // 팔로우 관계 조회
    @Query("SELECT f FROM Follow f " +
            "JOIN FETCH f.follower " +
            "JOIN FETCH f.following " +
            "WHERE f.follower = :follower AND f.following = :following")
    Optional<Follow> findByFollowerAndFollowing(@Param("follower") User follower, @Param("following") User following);

    // 모든 팔로우 조회
    Slice<Follow> findAllByFollower(User follower, Pageable pageable);

    // 모든 팔로잉 조회
    Slice<Follow> findAllByFollowing(User following, Pageable pageable);

    List<Follow> findAllByFollowing(User following);


    long countByFollower(User follower);    // 팔로잉 수

    long countByFollowing(User following);  // 팔로워 수

    @Query("SELECT f.following.id " +
            "FROM Follow f " +
            "WHERE f.follower.id = :userId"
    )
    List<Long> findFollowingUserIdsByUserId(
            @Param("userId") Long userId
    );
}