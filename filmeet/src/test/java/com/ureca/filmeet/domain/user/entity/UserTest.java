package com.ureca.filmeet.domain.user.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    @DisplayName("모든 입력값이 주어진 경우 정확한 점수를 계산한다.")
    void testAdjustActivityScores_withAllParameters() {
        // Given
        User user = new User(1L, 90, 40, 10);

        int maxLikes = 100, minLikes = 10;
        int maxGames = 50, minGames = 5;
        int maxCollections = 50, minCollections = 5;
        double averageLikeCount = 50.0, averageCollectionCount = 30.0, averageGameCount = 20.0;

        // When
        user.adjustActivityScores(maxLikes, minLikes, maxGames, minGames, maxCollections, minCollections,
                averageLikeCount, averageGameCount, averageCollectionCount);

        // Then
        assertEquals(1, user.getLikeActivityScore());
        assertEquals(2, user.getCollectionActivityScore());
        assertEquals(4, user.getGameActivityScore());
    }

    @Test
    @DisplayName("활동량이 최대일 경우 1점을 부여한다.")
    void testAdjustActivityScores_withPartialParameters() {
        // Given
        User user = new User(1L, 120, 50, 80);

        int maxLikes = 120, minLikes = 10;
        int maxGames = 80, minGames = 20;
        int maxCollections = 50, minCollections = 0;
        double averageLikeCount = 70.0, averageGameCount = 50.0, averageCollectionCount = 10.0;

        // When
        user.adjustActivityScores(maxLikes, minLikes, maxGames, minGames, maxCollections, minCollections,
                averageLikeCount, averageGameCount, averageCollectionCount);

        // Then
        assertEquals(1, user.getLikeActivityScore());
        assertEquals(1, user.getGameActivityScore());
        assertEquals(1, user.getCollectionActivityScore());
    }

    @Test
    @DisplayName("활동량이 최소일 경우 4점을 부여한다.")
    void testAdjustActivityScores_withNoActivities() {
        // Given
        User user = new User(1L, 0, 0, 0);

        int maxLikes = 50, minLikes = 0;
        int maxGames = 50, minGames = 0;
        int maxCollections = 50, minCollections = 0;
        double averageLikeCount = 20.0, averageGameCount = 20.0, averageCollectionCount = 20.0;

        // When
        user.adjustActivityScores(maxLikes, minLikes, maxGames, minGames, maxCollections, minCollections,
                averageLikeCount, averageGameCount, averageCollectionCount);

        // Then
        assertEquals(4, user.getLikeActivityScore());
        assertEquals(4, user.getGameActivityScore());
        assertEquals(4, user.getCollectionActivityScore());
    }

    @Test
    @DisplayName("max 값이 min 값보다 작은 경우 예외를 발생시킨다.")
    void testAdjustActivityScores_withInvalidMaxMinValues() {
        // Given
        User user = new User(1L, 30, 20, 10);

        int invalidMaxLikes = 10, invalidMinLikes = 50; // Invalid max < min
        int maxGames = 50, minGames = 10;
        int maxCollections = 30, minCollections = 10;
        double averageLikeCount = 30.0, averageGameCount = 30.0, averageCollectionCount = 20.0;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> user.adjustActivityScores(
                invalidMaxLikes, invalidMinLikes, maxGames, minGames, maxCollections, minCollections,
                averageLikeCount, averageGameCount, averageCollectionCount
        ));
    }

    @Test
    @DisplayName("max와 min 값이 같은 경우 기본 점수 3을 반환한다.")
    void testAdjustActivityScores_withEdgeCases() {
        // Given
        User user = new User(1L, 20, 10, 5);

        int maxLikes = 20, minLikes = 20; // Edge case: max == min
        int maxGames = 10, minGames = 10;
        int maxCollections = 0, minCollections = 0;
        double averageLikeCount = 20.0, averageGameCount = 10.0, averageCollectionCount = 5.0;

        // When
        user.adjustActivityScores(maxLikes, minLikes, maxGames, minGames, maxCollections, minCollections,
                averageLikeCount, averageGameCount, averageCollectionCount);

        // Then
        assertEquals(3, user.getLikeActivityScore());
        assertEquals(3, user.getGameActivityScore());
        assertEquals(3, user.getCollectionActivityScore());
    }

    @Test
    @DisplayName("활동량이 평균보다 높을 경우 낮은 점수를 부여한다.")
    void testAdjustActivityScores_withHighActivity() {
        // Given
        User user = new User(1L, 230, 160, 180);

        int maxLikes = 250, minLikes = 50;
        int maxGames = 200, minGames = 20;
        int maxCollections = 180, minCollections = 30;
        double averageLikeCount = 180.0, averageGameCount = 150.0, averageCollectionCount = 100.0;

        // When
        user.adjustActivityScores(maxLikes, minLikes, maxGames, minGames, maxCollections, minCollections,
                averageLikeCount, averageGameCount, averageCollectionCount);

        // Then
        assertEquals(1, user.getLikeActivityScore());
        assertEquals(2, user.getCollectionActivityScore());
        assertEquals(1, user.getGameActivityScore());
    }

    @Test
    @DisplayName("활동량이 평균보다 적을 경우 높은 점수를 부여한다.")
    void testAdjustActivityScores_withMinimumActivity() {
        // Given
        User user = new User(1L, 2, 3, 2);

        int maxLikes = 30, minLikes = 1;
        int maxGames = 10, minGames = 1;
        int maxCollections = 50, minCollections = 1;
        double averageLikeCount = 5.0, averageGameCount = 4.0, averageCollectionCount = 10.0;

        // When
        user.adjustActivityScores(maxLikes, minLikes, maxGames, minGames, maxCollections, minCollections,
                averageLikeCount, averageGameCount, averageCollectionCount);

        // Then
        assertEquals(4, user.getLikeActivityScore());
        assertEquals(4, user.getGameActivityScore());
        assertEquals(4, user.getCollectionActivityScore());
    }

    @Test
    @DisplayName("addTotalMovieLikes 호출 시 totalMovieLikes가 1 증가해야 한다")
    void addTotalMovieLikes_ShouldIncreaseByOne_WhenCalled() {
        // Given
        User user = new User(1L, 2, 3, 2);
        int initialLikes = user.getTotalMovieLikes();

        // When
        user.addTotalMovieLikes();

        // Then
        assertEquals(initialLikes + 1, user.getTotalMovieLikes());
    }

    @Test
    @DisplayName("decrementTotalMovieLikes 호출 시 totalMovieLikes가 0보다 클 때 1 감소해야 한다")
    void decrementTotalMovieLikes_ShouldDecreaseByOne_WhenGreaterThanZero() {
        // Given
        User user = new User(1L, 2, 3, 2);
        int initialLikes = user.getTotalMovieLikes();

        // When
        user.decrementTotalMovieLikes();

        // Then
        assertEquals(initialLikes - 1, user.getTotalMovieLikes());
    }

    @Test
    @DisplayName("decrementTotalMovieLikes 호출 시 totalMovieLikes가 0일 경우 감소하지 않아야 한다")
    void decrementTotalMovieLikes_ShouldNotDecrease_WhenZero() {
        // Given
        User user = new User(1L, 0, 3, 2);

        // When
        user.decrementTotalMovieLikes();

        // Then
        assertEquals(0, user.getTotalMovieLikes());
    }

    @Test
    @DisplayName("addTotalCollections 호출 시 totalCollections가 1 증가해야 한다")
    void addTotalCollections_ShouldIncreaseByOne_WhenCalled() {
        // Given
        User user = new User(1L, 2, 3, 2);
        int initialCollection = user.getTotalCollections();

        // When
        user.addTotalCollections();

        // Then
        assertEquals(initialCollection + 1, user.getTotalCollections());
    }

    @Test
    @DisplayName("decrementTotalCollections 호출 시 totalCollections가 0보다 클 때 1 감소해야 한다")
    void decrementTotalCollections_ShouldDecreaseByOne_WhenGreaterThanZero() {
        // Given
        User user = new User(1L, 2, 3, 2);
        int initialCollection = user.getTotalCollections();

        // When
        user.decrementTotalCollections();

        // Then
        assertEquals(initialCollection - 1, user.getTotalCollections());
    }

    @Test
    @DisplayName("decrementTotalCollections 호출 시 totalCollections가 0일 경우 감소하지 않아야 한다")
    void decrementTotalCollections_ShouldNotDecrease_WhenZero() {
        // Given
        User user = new User(1L, 2, 0, 2); // memberId = 1, totalMovieLikes = 2, totalCollections = 3, totalGames = 2

        // When
        user.decrementTotalCollections();

        // Then
        assertEquals(0, user.getTotalCollections());
    }

    @Test
    @DisplayName("addTotalMovieLikes와 decrementTotalMovieLikes를 조합하여 동작 확인")
    void addAndDecrementMovieLikes_ShouldWorkTogether() {
        // Given
        User user = new User(1L, 5, 3, 2); // memberId = 1, totalMovieLikes = 2, totalCollections = 3, totalGames = 2

        // When
        user.addTotalMovieLikes();
        user.decrementTotalMovieLikes();
        user.decrementTotalMovieLikes();
        user.decrementTotalMovieLikes();

        // Then
        assertEquals(3, user.getTotalMovieLikes());
    }

    @Test
    @DisplayName("addTotalCollections와 decrementTotalCollections를 조합하여 동작 확인")
    void addAndDecrementCollections_ShouldWorkTogether() {
        // Given
        User user = new User(1L, 2, 13, 2); // memberId = 1, totalMovieLikes = 2, totalCollections = 3, totalGames = 2

        // When
        user.addTotalCollections();
        user.decrementTotalCollections();
        user.decrementTotalCollections();
        user.decrementTotalCollections();
        user.decrementTotalCollections();

        // Then
        assertEquals(10, user.getTotalCollections());
    }
}
