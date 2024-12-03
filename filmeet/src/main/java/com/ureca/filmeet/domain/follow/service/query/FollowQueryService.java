package com.ureca.filmeet.domain.follow.service.query;

import com.ureca.filmeet.domain.follow.dto.response.FollowerResponse;
import com.ureca.filmeet.domain.follow.dto.response.FollowingResponse;
import com.ureca.filmeet.domain.follow.entity.Follow;
import com.ureca.filmeet.domain.follow.exception.FollowUserNotFoundException;
import com.ureca.filmeet.domain.follow.repository.FollowRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowQueryService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    /**
     * 팔로워 목록 조회 (나를 팔로우하는 사람들)
     */
    public SliceResponseDto<FollowerResponse> getFollowers(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(FollowUserNotFoundException::new);

        Slice<Follow> followers = followRepository.findAllByFollowing(user, pageable);
        return SliceResponseDto.of(followers.map(FollowerResponse::from));
    }

    /**
     * 팔로잉 목록 조회 (내가 팔로우하는 사람들)
     */
    public SliceResponseDto<FollowingResponse> getFollowings(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(FollowUserNotFoundException::new);

        Slice<Follow> followings = followRepository.findAllByFollower(user, pageable);
        return SliceResponseDto.of(followings.map(FollowingResponse::from));
    }

    /**
     * 팔로워 수 조회
     */
    public long getFollowerCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(FollowUserNotFoundException::new);

        return followRepository.countByFollowing(user);
    }

    /**
     * 팔로잉 수 조회
     */
    public long getFollowingCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(FollowUserNotFoundException::new);

        return followRepository.countByFollower(user);
    }
}
