package com.ureca.filmeet.domain.collection.service.command;

import com.ureca.filmeet.domain.collection.dto.request.CollectionCreateRequest;
import com.ureca.filmeet.domain.collection.dto.request.CollectionModifyRequest;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.repository.CollectionMovieBulkRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionMovieRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollectionCommandService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CollectionRepository collectionRepository;
    private final CollectionMovieRepository collectionMovieRepository;
    private final CollectionMovieBulkRepository collectionMovieBulkRepository;

    @Transactional
    public Long createCollection(CollectionCreateRequest collectionCreateRequest) {

        User user = userRepository.findById(collectionCreateRequest.userId())
                .orElseThrow(() -> new RuntimeException("no user"));

        List<Movie> movies = movieRepository.findMoviesByMovieIds(collectionCreateRequest.movieIds());

        if (movies.isEmpty()) {
            throw new RuntimeException("No movies found for the given IDs");
        }

        Collection collection = Collection.builder()
                .title(collectionCreateRequest.title())
                .content(collectionCreateRequest.content())
                .user(user)
                .build();
        Collection savedCollection = collectionRepository.save(collection);

        collectionMovieBulkRepository.saveAll(collection, movies);

        return savedCollection.getId();
    }

    @Transactional
    public Long modifyCollection(CollectionModifyRequest modifyRequest) {
        // 1. 컬렉션 조회
        Collection collection = collectionRepository.findById(modifyRequest.collectionId())
                .orElseThrow(() -> new RuntimeException("no collection"));

        // 2. 컬렉션 제목과 내용 수정
        collection.modifyCollection(modifyRequest.title(), modifyRequest.content());

        // 3. 기존에 저장된 영화 ID 목록 가져오기
        List<Long> existingMovieIds = collectionMovieRepository.findMovieIdsByCollectionId(collection.getId());

        // 4. 새로 요청된 영화 ID 목록
        List<Long> newMovieIds = modifyRequest.movieIds();

        // 5. 삭제할 영화와 추가할 영화 계산
        List<Long> moviesToRemove = existingMovieIds.stream()
                .filter(movieId -> !newMovieIds.contains(movieId))
                .collect(Collectors.toList());

        List<Long> moviesToAdd = newMovieIds.stream()
                .filter(movieId -> !existingMovieIds.contains(movieId))
                .collect(Collectors.toList());

        // 6. 삭제할 영화 처리
        if (!moviesToRemove.isEmpty()) {
            collectionMovieRepository.deleteByCollectionIdAndMovieIds(collection.getId(), moviesToRemove);
        }

        // 7. 추가할 영화 처리
        if (!moviesToAdd.isEmpty()) {
            List<Movie> movies = movieRepository.findMoviesByMovieIds(moviesToAdd);
            collectionMovieBulkRepository.saveAll(collection, movies);
        }

        return collection.getId();
    }
}
