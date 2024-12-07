package com.ureca.filmeet.domain.game.service.query;

import com.ureca.filmeet.domain.game.dto.response.GameDetailResponse;
import com.ureca.filmeet.domain.game.dto.response.GameRankingResponse;
import com.ureca.filmeet.domain.game.dto.response.GameResponse;
import com.ureca.filmeet.domain.game.dto.response.RoundMatchResponse;
import com.ureca.filmeet.domain.game.exception.GameNotFoundException;
import com.ureca.filmeet.domain.game.repository.GameRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameQueryService {

    private final GameRepository gameRepository;
    private final ReviewRepository reviewRepository;


    public GameResponse getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .map(GameResponse::from)
                .orElseThrow(GameNotFoundException::new);  // 또는 적절한 예외
    }

    public GameDetailResponse getGameDetail(Long gameId, User user) {
        Long userId = user.getId();
        return gameRepository.findByIdWithMatches(gameId)
                .filter(game -> game.getMatches().get(0).getUser().getId().equals(userId))
                .map(game -> {
                    // 각 RoundMatch의 댓글 수를 계산
                    List<RoundMatchResponse> matchResponses = game.getMatches().stream()
                            .map(match -> {
                                Integer movie1CommentCounts = Optional.ofNullable(reviewRepository.findTotalCommentCountsByMovieId(match.getMovie1().getId()))
                                        .orElse(0);
                                Integer movie2CommentCounts = Optional.ofNullable(reviewRepository.findTotalCommentCountsByMovieId(match.getMovie2().getId()))
                                        .orElse(0);return RoundMatchResponse.from(match, movie1CommentCounts, movie2CommentCounts);
                            })
                            .collect(Collectors.toList());
                    // GameDetailResponse 생성
                    return GameDetailResponse.from(game, matchResponses);
                })
                .orElseThrow(GameNotFoundException::new);
    }

    public Slice<GameResponse> getMyGames(Long userId, Pageable pageable) {
        return gameRepository.findAllByUserId(userId, pageable)
                .map(GameResponse::from);
    }

    @Cacheable(value = "movieRankings", key = "'rankings'")
    public List<GameRankingResponse> getGameRankings() {
        List<Object[]> results = gameRepository.findAllMovieRankings();
        return mapToDto(results);
    }


    @Scheduled(fixedRate = 600_000)
    @CacheEvict(value = "movieRankings", key = "'rankings'")
    public void refreshMovieRankingsCache() {
        getGameRankings(); // 캐시를 갱신
    }

    public List<GameRankingResponse> mapToDto(List<Object[]> results) {
        AtomicInteger rankCounter = new AtomicInteger(1);

        return results.stream()
                .map(row -> {
                    // 데이터 타입 안전하게 변환
                    Long id = ((Number) row[0]).longValue();  // ID는 Long으로 변환
                    String title = (String) row[1];  // 제목
                    String posterUrl = (String) row[2];  // 포스터 URL
                    Double victoryRatio = row[5] != null ? ((Number) row[5]).doubleValue() : 0.0;
                    Double winRate = row[6] != null ? ((Number) row[6]).doubleValue() : 0.0; // 승률

                    return GameRankingResponse.from(
                            id,
                            title,
                            posterUrl,
                            victoryRatio,
                            winRate,
                            rankCounter.getAndIncrement()  // 랭킹 부여
                    );
                })
                .collect(Collectors.toList());
    }

}
