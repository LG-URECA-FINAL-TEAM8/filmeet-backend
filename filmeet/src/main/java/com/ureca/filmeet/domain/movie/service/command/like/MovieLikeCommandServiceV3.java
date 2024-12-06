package com.ureca.filmeet.domain.movie.service.command.like;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

//@Service
@RequiredArgsConstructor
public class MovieLikeCommandServiceV3 implements MovieLikeCommandService {

    private final RedissonClient redissonClient;
    private final MovieLikeHelperService movieLikeHelperService;

    @Override
    public void movieLikes(Long movieId, Long userId) {
        RLock lock = redissonClient.getLock("v3:movieLikes:" + movieId);
        try {
            if (lock.tryLock(10000, 3000, TimeUnit.MILLISECONDS)) {
                movieLikeHelperService.movieLikes(userId, movieId);
            } else {
                throw new RuntimeException("Unable to acquire lock for movieLikes: " + movieId);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Error while acquiring lock", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void movieLikesCancel(Long movieId, Long userId) {
    }
}
