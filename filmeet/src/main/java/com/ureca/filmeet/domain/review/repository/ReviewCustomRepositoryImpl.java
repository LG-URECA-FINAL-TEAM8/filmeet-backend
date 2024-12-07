package com.ureca.filmeet.domain.review.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.filmeet.domain.admin.dto.response.AdminReviewResponse;
import com.ureca.filmeet.domain.admin.dto.response.QAdminReviewResponse;
import com.ureca.filmeet.domain.movie.entity.QMovie;
import com.ureca.filmeet.domain.review.entity.QReview;
import com.ureca.filmeet.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminReviewResponse> findReviewsByFilters(String movieTitle, String username, LocalDate createdAt, LocalDate lastModifiedAt, String sortDirection, Pageable pageable) {
        QReview review = QReview.review;
        QMovie movie = QMovie.movie;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        // 동적 조건 추가
        if (movieTitle != null) {
            builder.and(review.movie.title.containsIgnoreCase(movieTitle));
        }
        if (username != null) {
            builder.and(review.user.username.containsIgnoreCase(username));
        }
        if (createdAt != null) {
            LocalDateTime startOfDay = createdAt.atStartOfDay(); // 해당 날짜의 00:00:00
            LocalDateTime endOfDay = createdAt.atTime(23, 59, 59, 999999999); // 해당 날짜의 23:59:59
            builder.and(review.createdAt.between(startOfDay, endOfDay));
        }

        if (lastModifiedAt != null) {
            LocalDateTime startOfDay = lastModifiedAt.atStartOfDay(); // 해당 날짜의 00:00:00
            LocalDateTime endOfDay = lastModifiedAt.atTime(23, 59, 59, 999999999); // 해당 날짜의 23:59:59
            builder.and(review.modifiedAt.between(startOfDay, endOfDay));
        }

        // 정렬 조건
        OrderSpecifier<?> order = sortDirection.equalsIgnoreCase("asc")
                ? review.createdAt.asc()
                : review.createdAt.desc();

        // 쿼리 실행
        List<AdminReviewResponse> results = queryFactory
                .select(new QAdminReviewResponse(
                        review.id,
                        review.movie.title,
                        review.user.username,
                        review.createdAt,
                        review.modifiedAt,
                        review.content
                ))
                .from(review)
                .join(review.movie, movie)
                .join(review.user, user)
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트
        long total = queryFactory
                .select(review.count())
                .from(review)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
