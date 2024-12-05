package com.ureca.filmeet.domain.movie.service.command.like;

import com.ureca.filmeet.domain.genre.entity.enums.GenreScoreAction;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import com.ureca.filmeet.domain.movie.exception.MovieLikeAlreadyExistsException;
import com.ureca.filmeet.domain.movie.exception.MovieNotFoundException;
import com.ureca.filmeet.domain.movie.exception.MovieUserNotFoundException;
import com.ureca.filmeet.domain.movie.repository.MovieLikesRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.annotation.Transactional;

//@Service
@RequiredArgsConstructor
public class MovieLikeCommandServiceV2 implements MovieLikeCommandService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieLikesRepository movieLikesRepository;
    private final GenreScoreRepository genreScoreRepository;
    private final RedissonClient redissonClient;

    @Transactional
    @Override
    public void movieLikes(Long movieId, Long userId) {
        RLock lock = redissonClient.getLock("v2:movieLikes:" + movieId);
        try {
            // 분산 락 획득
            if (lock.tryLock(10, 3, TimeUnit.SECONDS)) { // waitTime: 10초, leaseTime: 3초
                // 좋아요 존재 여부 확인
                boolean isAlreadyLiked = movieLikesRepository.existsByMovieIdAndUserId(movieId, userId);
                if (isAlreadyLiked) {
                    throw new MovieLikeAlreadyExistsException();
                }

                // 영화 데이터 가져오기
                Movie movie = movieRepository.findMovieWithGenreByMovieId(movieId)
                        .orElseThrow(MovieNotFoundException::new);

                // 사용자 데이터 가져오기
                User user = userRepository.findById(userId)
                        .orElseThrow(MovieUserNotFoundException::new);

                // 좋아요 데이터 생성 및 저장
                MovieLikes movieLikes = MovieLikes.builder()
                        .movie(movie)
                        .user(user)
                        .build();
                movieLikesRepository.save(movieLikes);

                // 장르 점수 업데이트
                updateGenreScoresForUser(userId, movie, GenreScoreAction.LIKE);

                // 영화 좋아요 수 증가
                movie.addLikeCounts();
            } else {
                throw new RuntimeException("Unable to acquire lock for movieLikes");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to acquire lock due to interruption", e);
        } finally {
            // 락 해제
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void movieLikesCancel(Long movieId, Long userId) {
    }

    private void updateGenreScoresForUser(Long userId, Movie movie, GenreScoreAction genreScoreAction) {
        List<Long> genreIds = Optional.ofNullable(movie.getMovieGenres())
                .orElse(Collections.emptyList())
                .stream()
                .map(movieGenre -> movieGenre.getGenre().getId())
                .toList();

        genreScoreRepository.bulkUpdateGenreScores(
                genreScoreAction.getWeight(),
                genreIds,
                userId
        );
    }
}
