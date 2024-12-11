package com.ureca.filmeet.global.util.string;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import com.ureca.filmeet.infra.s3.service.command.S3CommandService;
import com.ureca.filmeet.infra.s3.service.query.S3QueryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class BadWordService {
    private final AhoCorasickDoubleArrayTrie<String> trie = new AhoCorasickDoubleArrayTrie<>();
    private final S3CommandService s3CommandService;
    private final S3QueryService s3QueryService;
    private static final String S3_KEY = "trie_data.dat";

    @PostConstruct
    public void initTrie() {
        try {
            log.info("Initializing Trie from S3...");
            loadTrieFromS3(S3_KEY);
            log.info("Trie successfully loaded from S3.");
        } catch (Exception e) {
            log.error("Failed to initialize Trie from S3: {}", e.getMessage(), e);
        }
    }

    // CSV 파일에서 트라이 빌드 및 S3에 저장
    public void buildAndSaveTrie(String s3Key) throws IOException {
        // 1. CSV 파일 읽기
        List<String> badWords = loadCsvFile("bad_words_dataset.csv");

        // 2. 트라이 빌드
        Map<String, String> map = badWords.stream()
                .collect(Collectors.toMap(word -> word, word -> "MASK"));
        trie.build(map);

        // 3. 직렬화하여 S3에 업로드
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            trie.save(objectOutputStream); // 트라이 직렬화
            byte[] serializedTrie = byteArrayOutputStream.toByteArray();
            s3CommandService.uploadSerializedTrie(s3Key, serializedTrie); // S3 업로드
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("Trie built and saved to S3 with key: {}", s3Key);
    }

    public void loadTrieFromS3(String s3Key) throws IOException, ClassNotFoundException {
        // S3에서 직렬화된 Trie 다운로드
        byte[] serializedTrie = s3QueryService.downloadSerializedFile(s3Key);

        // 역직렬화하여 Trie 복원
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedTrie);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

            trie.load(objectInputStream); // 트라이 로드
            log.info("Trie loaded from S3 with key: {}", s3Key);
        }
    }


    public String maskText(String text) {
        StringBuilder maskedText = new StringBuilder(text);
        List<AhoCorasickDoubleArrayTrie.Hit<String>> hits = trie.parseText(text);

        // hits가 비어 있다면 원본 텍스트 반환
        if (hits.isEmpty()) {
            return text;
        }

        // 범위를 병합하여 중복 제거
        List<int[]> mergedRanges = mergeRanges(hits);

        // 병합된 범위를 기반으로 마스킹 처리
        for (int i = mergedRanges.size() - 1; i >= 0; i--) {
            int[] range = mergedRanges.get(i);
            int start = range[0];
            int end = range[1];
            maskedText.replace(start, end, "***");
        }

        return maskedText.toString();
    }

    private List<int[]> mergeRanges(List<AhoCorasickDoubleArrayTrie.Hit<String>> hits) {
        List<int[]> ranges = new ArrayList<>();

        // hits가 비어 있다면 빈 리스트 반환
        if (hits.isEmpty()) {
            return ranges;
        }

        // 히트 범위 수집
        for (AhoCorasickDoubleArrayTrie.Hit<String> hit : hits) {
            ranges.add(new int[]{hit.begin, hit.end});
        }

        // 범위를 시작 기준으로 정렬
        ranges.sort(Comparator.comparingInt(range -> range[0]));

        // 범위 병합
        List<int[]> merged = new ArrayList<>();
        int[] current = ranges.get(0);

        for (int i = 1; i < ranges.size(); i++) {
            int[] next = ranges.get(i);

            if (current[1] >= next[0]) {
                // 겹치는 범위 병합
                current[1] = Math.max(current[1], next[1]);
            } else {
                // 겹치지 않는 경우 추가
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);

        return merged;
    }

    // CSV 파일 읽기
    private List<String> loadCsvFile(String csvFilePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(csvFilePath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines()
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
    }
}
