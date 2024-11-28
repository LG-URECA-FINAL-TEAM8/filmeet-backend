package com.ureca.filmeet.domain.game.controller.command;

import com.ureca.filmeet.domain.game.dto.request.GameCreateRequest;
import com.ureca.filmeet.domain.game.dto.request.RoundMatchSelectionRequest;
import com.ureca.filmeet.domain.game.service.command.GameCommandService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameCommandController {
    private final GameCommandService gameCommandService;

    @PostMapping
    @Operation(summary = "게임 생성", description = "새로운 이상형 월드컵 게임을 생성합니다.")
    public ResponseEntity<ApiResponse<Long>> createGame(
            @Valid @RequestBody GameCreateRequest request,
            @AuthenticationPrincipal User user) {
        Long gameId = gameCommandService.createGame(request, user);
        return ApiResponse.created("/games/" + gameId, gameId);
    }

    @PostMapping("/matches/{matchId}/select")
    @Operation(summary = "승자 선택", description = "현재 라운드에서 승자를 선택합니다.")
    public ResponseEntity<ApiResponse<Void>> selectWinner(
            @PathVariable Long matchId,
            @Valid @RequestBody RoundMatchSelectionRequest request,
            @AuthenticationPrincipal User user) {
        gameCommandService.selectWinner(
                matchId,
                request,
                user
        );
        return ApiResponse.ok(null);
    }
}
