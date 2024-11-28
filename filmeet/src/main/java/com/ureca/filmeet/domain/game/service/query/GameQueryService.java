package com.ureca.filmeet.domain.game.service.query;

import com.amazonaws.services.kms.model.NotFoundException;
import com.ureca.filmeet.domain.game.dto.response.GameDetailResponse;
import com.ureca.filmeet.domain.game.dto.response.GameResponse;
import com.ureca.filmeet.domain.game.repository.GameRepository;
import com.ureca.filmeet.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameQueryService {

    private final GameRepository gameRepository;

    public GameResponse getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .map(GameResponse::from)
                .orElseThrow(() -> new NotFoundException("game not found"));  // 또는 적절한 예외
    }

    public GameDetailResponse getGameDetail(Long gameId, User user) {
        Long userId = user.getId();
        return gameRepository.findByIdWithMatches(gameId)
                .filter(game -> game.getMatches().get(0).getUser().getId().equals(userId))
                .map(GameDetailResponse::from)
                .orElseThrow(() -> new NotFoundException("game not found"));  // 또는 적절한 예외
    }

    public Slice<GameResponse> getMyGames(Long userId, Pageable pageable) {
        return gameRepository.findAllByUserId(userId, pageable)
                .map(GameResponse::from);
    }
}
