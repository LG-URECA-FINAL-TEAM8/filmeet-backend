package com.ureca.filmeet.domain.movie.batch;

import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class MovieRecommendationReader implements ItemReader<User> {

    private final UserRepository userRepository;

    private int currentPage = 0;
    private int currentIndex = 0;
    private Page<User> currentBatch;

    @Override
    public User read() {
        if (currentBatch == null || currentIndex >= currentBatch.getContent().size()) {
            currentBatch = fetchNextPage();
            if (currentBatch.isEmpty()) {
                return null;
            }
            currentIndex = 0;
        }

        return currentBatch.getContent().get(currentIndex++);
    }

    private Page<User> fetchNextPage() {
        // 한 번에 읽을 데이터 크기
        int pageSize = 100;
        Pageable pageable = PageRequest.of(currentPage++, pageSize);
        log.debug("Fetching page: {} with size: {}", currentPage - 1, pageSize);
        Page<User> page = userRepository.findAll(pageable);
        log.info("Fetched {} users from page {}", page.getNumberOfElements(), currentPage - 1);
        return page;
    }
}

