package com.ureca.filmeet.domain.collection.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createCollection;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.collection.dto.request.CollectionCommentCreateRequest;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.repository.CollectionCommentRepository;
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
public class CollectionCommentLockServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollectionCommentCommandService collectionCommentCommandService;

    @Autowired
    private CollectionCommentRepository collectionCommentRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @AfterEach
    void tearDown() {
        collectionCommentRepository.deleteAll();
        collectionRepository.deleteAll(); // Collection 삭제
        userRepository.deleteAll();       // User 삭제
    }

    @DisplayName("댓글 저장 - 댓글_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void commentCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test2() throws InterruptedException {
        // given
        User owner = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Collection collection = createCollection("컬렉션 제목1", "컬렉션 내용1", owner);
        userRepository.save(owner);
        collectionRepository.save(collection);
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            User user = createUser("username" + i, "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임" + i,
                    "https://example.com/profile" + i + ".jpg");
            users.add(user);
        }
        userRepository.saveAll(users);

        // when
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            int commentNum = i + 1;
            CollectionCommentCreateRequest createCommentRequest = new CollectionCommentCreateRequest(collection.getId(),
                    "내용" + commentNum);

            executorService.submit(() -> {
                try {
                    collectionCommentCommandService.createCollectionComment(createCommentRequest, userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Optional<Collection> findCollection = collectionRepository.findById(collection.getId());
        assertThat(findCollection).isPresent();
        assertThat(findCollection.get().getCommentCounts()).isEqualTo(1000);
    }

}