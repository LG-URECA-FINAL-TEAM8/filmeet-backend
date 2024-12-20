package com.ureca.filmeet.domain.user.service.query;

import com.ureca.filmeet.domain.follow.repository.FollowRepository;
import com.ureca.filmeet.domain.follow.service.query.FollowQueryService;
import com.ureca.filmeet.domain.movie.repository.MovieRatingsRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.dto.response.TargetUserDetailResponse;
import com.ureca.filmeet.domain.user.dto.response.UserDetailResponse;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final MovieRatingsRepository movieRatingsRepository;
    private final FollowQueryService followQueryService;
    private final FollowRepository followRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by username: " + username));
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public TargetUserDetailResponse getUserDetailById(Long userId, User user) {
        User targeUser = findById(userId);
        Integer reviewCount = reviewRepository.countByUserId(userId);
        Integer movieRatingCount = movieRatingsRepository.countByUserId(userId);
        long followerCount = followQueryService.getFollowerCount(userId);
        long followingCount = followQueryService.getFollowingCount(userId);
        boolean isFollowed = followRepository.existsByFollowerAndFollowing(user, targeUser);
        return new TargetUserDetailResponse(
                targeUser.getId(),
                targeUser.getUsername(),
                targeUser.getRole(),
                targeUser.getNickname(),
                targeUser.getProfileImage(),
                targeUser.isFirstLogin(),
                targeUser.getAge(),
                targeUser.getMbti(),
                targeUser.getTotalMovieLikes(),
                targeUser.getTotalCollections(),
                targeUser.getTotalGames(),
                reviewCount,
                movieRatingCount,
                followerCount,
                followingCount,
                isFollowed
        );
    }

    public UserDetailResponse getUserDetailByUser(User user) {
        Integer reviewCount = reviewRepository.countByUserId(user.getId());
        Integer movieRatingCount = movieRatingsRepository.countByUserId(user.getId());
        long followerCount = followQueryService.getFollowerCount(user.getId());
        long followingCount = followQueryService.getFollowingCount(user.getId());
        return new UserDetailResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getNickname(),
                user.getProfileImage(),
                user.isFirstLogin(),
                user.getAge(),
                user.getMbti(),
                user.getTotalMovieLikes(),
                user.getTotalCollections(),
                user.getTotalGames(),
                reviewCount,
                movieRatingCount,
                followerCount,
                followingCount
        );
    }
}
