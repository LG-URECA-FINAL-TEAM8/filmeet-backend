package com.ureca.filmeet.domain.game.controller.query;

import com.ureca.filmeet.domain.game.dto.response.GameDetailResponse;
import com.ureca.filmeet.domain.game.dto.response.GameRankingResponse;
import com.ureca.filmeet.domain.game.dto.response.GameResponse;
import com.ureca.filmeet.domain.game.service.query.GameQueryService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameQueryController {
    private final GameQueryService gameQueryService;

    @GetMapping("/{gameId}")
    @Operation(summary = "게임 조회", description = "게임의 기본 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<GameResponse>> getGame(@PathVariable Long gameId) {
        GameResponse response = gameQueryService.getGame(gameId);
        return ApiResponse.ok(response);
    }

    @GetMapping("/{gameId}/detail")
    @Operation(summary = "게임 상세 조회", description = "게임의 상세 정보와 매치 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<GameDetailResponse>> getGameDetail(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User user) {
        GameDetailResponse response = gameQueryService.getGameDetail(
                gameId,
                user
        );
        return ApiResponse.ok(response);
    }

    @GetMapping
    @Operation(summary = "내 게임 목록 조회", description = "사용자의 게임 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<SliceResponseDto<GameResponse>>> getMyGames(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Slice<GameResponse> games = gameQueryService.getMyGames(
                Long.parseLong(userDetails.getUsername()),
                PageRequest.of(page, size)
        );
        return ApiResponse.ok(SliceResponseDto.of(games));
    }

    @GetMapping("/rankings")
    @Operation(summary = "게임 랭킹 조회", description = "영화의 우승 비율 및 승률 정보를 제공합니다.")
    public ResponseEntity<ApiResponse<List<GameRankingResponse>>> getMovieRankings() {
        List<GameRankingResponse> rankings = gameQueryService.getGameRankings();
        return ApiResponse.ok(rankings);
    }
}
