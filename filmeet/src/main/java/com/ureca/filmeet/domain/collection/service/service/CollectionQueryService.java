package com.ureca.filmeet.domain.collection.service.service;

import com.ureca.filmeet.domain.collection.dto.response.CollectionGetResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionSearchByTitleResponse;
import com.ureca.filmeet.domain.collection.dto.response.MovieInfoResponse;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
import com.ureca.filmeet.domain.collection.repository.CollectionMovieRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollectionQueryService {

    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;
    private final CollectionMovieRepository collectionMovieRepository;

    public Slice<CollectionGetResponse> getCollections(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("no user"));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Slice<Collection> collections = collectionRepository.findCollectionsByUserId(user.getId(), pageable);

        List<Long> collectionIds = collections.getContent()
                .stream()
                .map(Collection::getId)
                .toList();

        List<CollectionMovie> collectionMovies = collectionMovieRepository.findMoviesByCollectionIds(collectionIds);

        return collections.map(collection -> {
            // 각 컬렉션의 영화 데이터를 가져오기
            List<MovieInfoResponse> movies = getMoviesForCollection(collection.getId(), collectionMovies);
            return CollectionGetResponse.from(collection, movies);
        });
    }

    private List<MovieInfoResponse> getMoviesForCollection(Long collectionId, List<CollectionMovie> collectionMovies) {
        return collectionMovies.stream()
                .filter(cm -> cm.getCollection().getId().equals(collectionId))
                .map(cm -> new MovieInfoResponse(
                        cm.getMovie().getId(),
                        cm.getMovie().getTitle(),
                        cm.getMovie().getPosterUrl(),
                        cm.getMovie().getReleaseDate(),
                        cm.getMovie().getRuntime(),
                        cm.getMovie().getFilmRatings(),
                        cm.getMovie().getAverageRating(),
                        cm.getMovie().getLikeCounts(),
                        cm.getMovie().getRatingCounts()
                ))
                .toList();
    }

    public CollectionGetResponse getCollection(Long collectionId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("no user"));

        Collection collection = collectionRepository.findCollectionByCollectionIdAndUserId(collectionId, user.getId())
                .orElseThrow(() -> new RuntimeException("no collection"));

        List<CollectionMovie> collectionMovies = collectionMovieRepository.findMoviesByCollectionId(
                collection.getId());

        List<MovieInfoResponse> movies = getMoviesForCollection(collectionMovies);
        return CollectionGetResponse.from(collection, movies);
    }

    private List<MovieInfoResponse> getMoviesForCollection(List<CollectionMovie> collectionMovies) {
        return collectionMovies.stream()
                .sorted(Comparator.comparing(CollectionMovie::getId).reversed())
                .map(cm -> new MovieInfoResponse(
                        cm.getMovie().getId(),
                        cm.getMovie().getTitle(),
                        cm.getMovie().getPosterUrl(),
                        cm.getMovie().getReleaseDate(),
                        cm.getMovie().getRuntime(),
                        cm.getMovie().getFilmRatings(),
                        cm.getMovie().getAverageRating(),
                        cm.getMovie().getLikeCounts(),
                        cm.getMovie().getRatingCounts()
                ))
                .toList();
    }

    public Slice<CollectionSearchByTitleResponse> searchCollectionByTitle(String titleKeyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return collectionRepository.findCollectionsByTitleKeyword(titleKeyword, pageable)
                .map(CollectionSearchByTitleResponse::of);
    }
}
