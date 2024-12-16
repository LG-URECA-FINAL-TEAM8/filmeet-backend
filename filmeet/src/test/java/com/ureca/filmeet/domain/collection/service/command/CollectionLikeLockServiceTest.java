package com.ureca.filmeet.domain.collection.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.repository.CollectionLikeRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
public class CollectionLikeLockServiceTest {

    @Autowired
    private CollectionLikeCommandService collectionLikeCommandService;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollectionLikeRepository collectionLikeRepository;

    @AfterEach
    void tearDown() {
        collectionLikeRepository.deleteAllInBatch();
        collectionRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("좋아요_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void likeCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        // given
        User owner = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목1", "컬렉션 내용1", owner);

        userRepository.saveAndFlush(owner);
        collectionRepository.saveAndFlush(collection);
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            User user = createUser("username" + i, "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임" + i,
                    "https://example.com/profile" + i + ".jpg");
            users.add(user);
        }
        userRepository.saveAll(users);
        userRepository.flush();

        // when
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            executorService.submit(() -> {
                try {
                    collectionLikeCommandService.collectionLikes(collection.getId(), userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Optional<Collection> findCollection = collectionRepository.findById(collection.getId());
        assertThat(findCollection).isPresent();
        assertThat(findCollection.get().getLikeCounts()).isEqualTo(1000);
    }

    @DisplayName("좋아요_취소_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void likeCancelCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        // given
        User owner = createUser("owner", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목", "컬렉션 내용", owner);
        userRepository.saveAndFlush(owner);
        collectionRepository.saveAndFlush(collection);
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            User user = createUser("user" + i, "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임" + i,
                    "https://example.com/profile" + i + ".jpg");
            users.add(user);
        }
        userRepository.saveAll(users);
        userRepository.flush();

        // when
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch likeLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch cancelLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            executorService.submit(() -> {
                try {
                    collectionLikeCommandService.collectionLikes(collection.getId(), userId);
                } finally {
                    likeLatch.countDown();
                }
            });
        }
        likeLatch.await();

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            executorService.submit(() -> {
                try {
                    collectionLikeCommandService.collectionLikesCancel(collection.getId(), userId);
                } finally {
                    cancelLatch.countDown();
                }
            });
        }
        cancelLatch.await();

        executorService.shutdown();

        // then
        Optional<Collection> findCollection = collectionRepository.findById(collection.getId());
        assertThat(findCollection).isPresent();
        assertThat(findCollection.get().getLikeCounts()).isEqualTo(0);
    }
}
