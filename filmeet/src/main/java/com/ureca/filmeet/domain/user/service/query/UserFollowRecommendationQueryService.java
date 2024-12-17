package com.ureca.filmeet.domain.user.service.query;

import com.ureca.filmeet.domain.follow.repository.FollowRepository;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.movie.dto.response.RecommendationMoviesResponse;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieRecommendation;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.movie.service.query.MovieScoreService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.global.util.CosineSimilarityUtil;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFollowRecommendationQueryService {

    private final MovieRepository movieRepository;
    private final GenreScoreRepository genreScoreRepository;
    private final MovieScoreService movieScoreService;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    private static final int PAGE_NUMBER = 0;
    private static final int TOP_USER_LIMIT = 5;
    private static final int TOP_GENRE_LIMIT = 5;
    private static final int MOVIES_PER_GENRE = 20;
    private static final int TOP_MOVIE_LIMIT = 20;

    public List<RecommendationMoviesResponse> getMoviesFollowingPreference(Long followerId, List<Long> movieIds) {
        User user = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("no user"));

        List<Long> followingUserIds = followRepository.findFollowingUserIdsByUserId(followerId);

        List<User> similarUsers = getTopSimilarUsers(user, followingUserIds);

        List<Long> similarUsersIds = similarUsers.stream()
                .map(User::getId)
                .toList();
        List<Long> similarUsersGenreIds = genreScoreRepository.findTopGenresBySimilarUsersIds(
                similarUsersIds,
                Pageable.ofSize(TOP_GENRE_LIMIT)
        );

        Set<Movie> movieSet = new LinkedHashSet<>();
        for (Long genreId : similarUsersGenreIds) {
            List<Movie> moviesByGenre = movieRepository.findMoviesBySimilarUsersGenreIds(
                    genreId, movieIds, PageRequest.of(PAGE_NUMBER, MOVIES_PER_GENRE)
            );
            movieSet.addAll(moviesByGenre);
        }

        Map<Movie, Double> movieScores = movieScoreService.calculateMovieScores(new ArrayList<>(movieSet));

        return movieScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(TOP_MOVIE_LIMIT)
                .map(entry -> RecommendationMoviesResponse.of(entry.getKey()))
                .toList();
    }

    /**
     * 팔로우한 유저의 관심사 기반 추천 영화 생성
     */
    public List<MovieRecommendation> getFollowBasedRecommendations(User user, List<Long> contentBasedMovieIds) {
        List<Long> followingUserIds = followRepository.findFollowingUserIdsByUserId(user.getId());

        // 관심사 유사도 점수를 기준으로 상위 유저 5명 선택
        List<User> similarUsers = getTopSimilarUsers(user, followingUserIds);

        // 선택된 유저들의 관심 장르 상위 5개 선택
        List<Long> similarUsersIds = similarUsers.stream()
                .map(User::getId)
                .toList();
        List<Long> similarUsersGenreIds = genreScoreRepository.findTopGenresBySimilarUsersIds(
                similarUsersIds,
                PageRequest.ofSize(TOP_GENRE_LIMIT)
        );

        Set<Movie> movieSet = new LinkedHashSet<>();
        for (Long genreId : similarUsersGenreIds) {
            List<Movie> moviesByGenre = movieRepository.findMoviesBySimilarUsersGenreIds(
                    genreId, contentBasedMovieIds, PageRequest.of(PAGE_NUMBER, MOVIES_PER_GENRE)
            );

            movieSet.addAll(moviesByGenre);
        }

        // 점수 계산 및 최종 추천 영화 반환
        Map<Movie, Double> movieScores = movieScoreService.calculateMovieScores(new ArrayList<>(movieSet));

        return movieScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(TOP_MOVIE_LIMIT)
                .map(entry -> MovieRecommendation.builder()
                        .user(user)
                        .movie(entry.getKey())
                        .build())
                .toList();
    }

    /**
     * 관심사 유사도 점수를 계산하여 상위 유저를 가져옴
     */
    private List<User> getTopSimilarUsers(User user, List<Long> followingUserIds) {
        List<User> followingUsers = userRepository.findUsersByFollowingUserIds(followingUserIds);

        return followingUsers.stream()
                .map(followingUser -> Map.entry(followingUser, calculateProfileSimilarity(user, followingUser)))
                .sorted(Map.Entry.<User, Double>comparingByValue().reversed())
                .limit(TOP_USER_LIMIT)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * 관심사 유사도 점수를 계산 (코사인 유사도 기반)
     */
    private double calculateProfileSimilarity(User user, User followedUser) {
        return CosineSimilarityUtil.calculateProfileSimilarity(user, followedUser);
    }
}
