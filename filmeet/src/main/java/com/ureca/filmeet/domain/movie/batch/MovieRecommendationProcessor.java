package com.ureca.filmeet.domain.movie.batch;

import com.ureca.filmeet.domain.movie.entity.MovieRecommendation;
import com.ureca.filmeet.domain.movie.service.query.MovieRecommendationQueryService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.service.query.UserFollowRecommendationQueryService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieRecommendationProcessor implements ItemProcessor<User, List<MovieRecommendation>> {

    private final UserFollowRecommendationQueryService followRecommendationQueryService;
    private final MovieRecommendationQueryService movieRecommendationQueryService;

    @Override
    public List<MovieRecommendation> process(User user) {
        // 1. 콘텐츠 기반 추천 영화 가져오기
        List<MovieRecommendation> contentBasedRecommendations = movieRecommendationQueryService.getContentBasedRecommendations(
                user);
        List<Long> contentBasedMovieIds = contentBasedRecommendations.stream()
                .map(recommendation -> recommendation.getMovie().getId())
                .toList();

        // 2. 팔로우한 유저의 관심사 기반 추천 영화 가져오기
        List<MovieRecommendation> followBasedRecommendations = followRecommendationQueryService.getFollowBasedRecommendations(
                user,
                contentBasedMovieIds
        );

        // 3. 콘텐츠 기반(60%) + 팔로우 기반(40%) 비율로 추천 영화 합치기
        int totalRecommendations = 20;
        int followBasedCount = Math.min(followBasedRecommendations.size(), (int) (totalRecommendations * 0.4));
        int contentBasedCount = totalRecommendations - followBasedCount;

        List<MovieRecommendation> finalRecommendations = new ArrayList<>();
        finalRecommendations.addAll(followBasedRecommendations.stream().limit(followBasedCount).toList());
        finalRecommendations.addAll(contentBasedRecommendations.stream().limit(contentBasedCount).toList());

        return finalRecommendations;
    }
}