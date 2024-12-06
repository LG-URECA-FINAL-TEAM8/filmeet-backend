package com.ureca.filmeet.domain.collection.service.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.collection.dto.request.CollectionCommentCreateRequest;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
public class CollectionCommentLockServiceTest {

    @Autowired
    private CollectionCommentCommandService collectionCommentCommandService;

    @Autowired
    private CollectionRepository collectionRepository;

    @DisplayName("댓글 저장 - 댓글_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void commentCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 1; i <= numberOfThreads; i++) {
            int num = 138 + i;
            CollectionCommentCreateRequest createCommentRequest = new CollectionCommentCreateRequest(1L, "내용" + num);
            executorService.submit(() -> {
                try {
                    collectionCommentCommandService.createCollectionComment(createCommentRequest, (long) num);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Optional<Collection> collection = collectionRepository.findById(1L);
        assertThat(collection).isPresent();
        assertThat(collection.get().getCommentCounts()).isEqualTo(1000);
    }
}