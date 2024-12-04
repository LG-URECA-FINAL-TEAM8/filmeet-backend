package com.ureca.filmeet.domain.follow.service.command;

import com.ureca.filmeet.domain.follow.entity.Follow;
import com.ureca.filmeet.domain.follow.exception.FollowAlreadyExistsException;
import com.ureca.filmeet.domain.follow.exception.FollowNotFoundException;
import com.ureca.filmeet.domain.follow.exception.FollowUserNotFoundException;
import com.ureca.filmeet.domain.follow.exception.SelfFollowNotAllowedException;
import com.ureca.filmeet.domain.follow.repository.FollowRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.global.notification.service.command.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowCommandService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationCommandService notificationCommandService;

    /**
     * 팔로우 하기
     */
    public void follow(Long followingId, User follower) {
        // 자기 자신을 팔로우하는지 체크
        if (follower.getId().equals(followingId)) {
            throw new SelfFollowNotAllowedException();
        }

        // 팔로우 대상 사용자 조회
        User following = userRepository.findById(followingId)
                .orElseThrow(FollowUserNotFoundException::new);

        // 이미 팔로우하고 있는지 체크
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new FollowAlreadyExistsException();
        }

        // 팔로우 관계 생성 및 저장
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        // 팔로우 알림 발송
        notificationCommandService.sendFollowNotification(follower, following);
    }

    /**
     * 언팔로우
     */
    public void unfollow(Long followingId, User follower) {
        // 팔로우 대상 사용자 조회
        User following = userRepository.findById(followingId)
                .orElseThrow(FollowUserNotFoundException::new);

        // 팔로우 관계 조회
        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(FollowNotFoundException::new);

        // 팔로우 관계 삭제
        followRepository.delete(follow);
    }
}