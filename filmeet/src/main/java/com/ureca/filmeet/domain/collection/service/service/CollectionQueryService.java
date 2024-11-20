package com.ureca.filmeet.domain.collection.service.service;

import com.ureca.filmeet.domain.collection.dto.response.CollectionGetResponse;
import com.ureca.filmeet.domain.collection.dto.response.MovieInfoResponse;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
import com.ureca.filmeet.domain.collection.repository.CollectionMovieRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
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

    private final CollectionMovieRepository collectionMovieRepository;
    private final CollectionRepository collectionRepository;

    public Slice<CollectionGetResponse> getCollections(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Slice<Collection> collections = collectionRepository.findCollectionsByUserId(userId, pageable);

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
                        cm.getMovie().getReviewCounts()
                ))
                .toList();
    }
}
