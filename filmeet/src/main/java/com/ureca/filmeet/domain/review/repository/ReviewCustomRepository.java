package com.ureca.filmeet.domain.review.repository;

import com.ureca.filmeet.domain.admin.dto.response.AdminReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReviewCustomRepository {
    Page<AdminReviewResponse> findReviewsByFilters(String movieTitle, String username,
                                                   LocalDate createdAt, LocalDate lastModifiedAt,
                                                   String sortDirection, Pageable pageable);
}
