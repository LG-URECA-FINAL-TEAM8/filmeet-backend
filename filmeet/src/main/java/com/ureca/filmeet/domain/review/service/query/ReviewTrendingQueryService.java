package com.ureca.filmeet.domain.review.service.query;

import com.ureca.filmeet.domain.review.dto.response.trending.ReviewResponse;
import com.ureca.filmeet.domain.review.dto.response.trending.ReviewTrendingResponse;
import com.ureca.filmeet.domain.review.entity.enums.PopularityWeight;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewTrendingQueryService {

    private final ReviewRepository reviewRepository;

    public Slice<ReviewTrendingResponse> getTrendingReviews(Long userId, Pageable pageable, LocalDateTime currentTime) {
        Slice<ReviewResponse> reviews = reviewRepository.findTrendingReviewsBy(userId, pageable);
        List<ReviewTrendingResponse> reviewTrendingResponses = reviews.getContent()
                .stream()
                .map(review -> {
                    double popularityScore = calculatePopularityScore(review, currentTime);
                    return ReviewTrendingResponse.from(review, popularityScore);
                })
                .sorted(Comparator.comparingDouble(ReviewTrendingResponse::popularityScore).reversed())
                .collect(Collectors.toList());

        return new SliceImpl<>(reviewTrendingResponses, pageable, reviews.hasNext());
    }

    private double calculatePopularityScore(ReviewResponse review, LocalDateTime currentTime) {
        long maxDays = (long) PopularityWeight.MAX_DAYS.getWeight();
        long daysSinceReview = ChronoUnit.DAYS.between(review.createdAt(), currentTime);
        long boundedDays = Math.min(daysSinceReview, maxDays); // 최대 50일까지 감소 반영

        double popularityScore = (review.likeCounts() * PopularityWeight.LIKE.getWeight()) +
                (review.commentCounts() * PopularityWeight.COMMENT.getWeight()) +
                (PopularityWeight.BASE_SCORE.getWeight() - boundedDays * PopularityWeight.TIME_DECAY.getWeight());

        return Math.max(0, popularityScore); // 점수가 음수가 되지 않도록 최소값 설정
    }

    public Slice<ReviewTrendingResponse> getRecentReviews(Long userId, Pageable pageable) {
        return reviewRepository.findTrendingReviewsBy(userId, pageable)
                .map(reviewResponse -> ReviewTrendingResponse.from(reviewResponse, 0.0));
    }
}
