package com.ureca.filmeet.domain.follow.controller.query;

import com.ureca.filmeet.domain.follow.dto.response.FollowerResponse;
import com.ureca.filmeet.domain.follow.dto.response.FollowingResponse;
import com.ureca.filmeet.domain.follow.service.query.FollowQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
public class FollowQueryController {
    private final FollowQueryService followQueryService;

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<SliceResponseDto<FollowerResponse>>> getFollowers(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ApiResponse.ok(followQueryService.getFollowers(userId, pageable));
    }

    @GetMapping("/followings/{userId}")
    public ResponseEntity<ApiResponse<SliceResponseDto<FollowingResponse>>> getFollowings(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ApiResponse.ok(followQueryService.getFollowings(userId, pageable));
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCounts(
            @PathVariable Long userId
    ) {
        Map<String, Long> counts = Map.of(
                "followerCount", followQueryService.getFollowerCount(userId),
                "followingCount", followQueryService.getFollowingCount(userId)
        );
        return ApiResponse.ok(counts);
    }
}
