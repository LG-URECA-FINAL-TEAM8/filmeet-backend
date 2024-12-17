package com.ureca.filmeet.global.util;

import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.entity.enums.MbtiStatus;
import java.util.ArrayList;
import java.util.List;

public class CosineSimilarityUtil {

    private CosineSimilarityUtil() {
        // 유틸리티 클래스는 인스턴스화 방지
    }

    /**
     * 두 사용자 간의 코사인 유사도를 계산
     *
     * @param user1 첫 번째 사용자
     * @param user2 두 번째 사용자
     * @return 코사인 유사도 값
     */
    public static double calculateProfileSimilarity(User user1, User user2) {
        // 사용자 벡터 생성
        double[] vectorA = createFeatureVector(user1);
        double[] vectorB = createFeatureVector(user2);

        // 코사인 유사도 계산
        return calculate(vectorA, vectorB);
    }

    /**
     * 두 벡터 간의 코사인 유사도 계산
     *
     * @param vectorA 첫 번째 벡터
     * @param vectorB 두 번째 벡터
     * @return 코사인 유사도 값
     */
    private static double calculate(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        normA = Math.sqrt(normA);
        normB = Math.sqrt(normB);

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (normA * normB);
    }

    /**
     * 사용자 벡터 생성 (나이, MBTI, 행동 점수 포함)
     *
     * @param user 사용자 객체
     * @return 사용자 벡터
     */
    private static double[] createFeatureVector(User user) {
        List<Double> vector = new ArrayList<>();

        // 1. 나이 정규화 (null 또는 음수인 경우 기본값으로 처리)
        Integer age = user.getAge();
        vector.add(normalizeAge(age != null && age > 0 ? age : 25)); // 기본 나이 20로 설정

        // 2. MBTI 벡터화 (null 값이면 빈 벡터 반환)
        String mbti = user.getMbti();
        double[] mbtiVector = mbti != null ? oneHotEncodeMbti(mbti) : new double[MbtiStatus.values().length];
        for (double value : mbtiVector) {
            vector.add(value);
        }

        // 3. 행동 점수 추가
        vector.add((double) user.getGameActivityScore());
        vector.add((double) user.getLikeActivityScore());
        vector.add((double) user.getCollectionActivityScore());

        return vector.stream().mapToDouble(Double::doubleValue).toArray();
    }

    /**
     * 나이를 0~1 범위로 정규화
     *
     * @param age 나이
     * @return 정규화된 나이 값
     */
    private static double normalizeAge(int age) {
        final int MAX_AGE = 100; // 최대 나이 기준
        return age / (double) MAX_AGE;
    }

    /**
     * MBTI를 One-Hot Encoding하여 벡터화
     *
     * @param mbti 사용자 MBTI
     * @return MBTI One-Hot Encoding 벡터
     */
    private static double[] oneHotEncodeMbti(String mbti) {
        // MBTI Enum 확인 및 인덱스 찾기
        MbtiStatus[] mbtiValues = MbtiStatus.values();
        double[] encoding = new double[mbtiValues.length];

        try {
            MbtiStatus mbtiStatus = MbtiStatus.toMbtiStatus(mbti);
            int index = mbtiStatus.ordinal(); // Enum의 순서를 기반으로 인덱스 가져오기
            encoding[index] = 1.0;            // 해당 인덱스에 1.0 설정
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 MBTI 값 처리
            System.err.println("Invalid MBTI type: " + mbti);
        }

        return encoding;
    }
}