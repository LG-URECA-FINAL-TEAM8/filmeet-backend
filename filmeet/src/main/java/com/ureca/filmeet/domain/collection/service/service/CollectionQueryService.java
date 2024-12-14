package com.ureca.filmeet.domain.collection.service.service;

import com.ureca.filmeet.domain.collection.dto.response.CollectionCommentsResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionDetailResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionMovieInfoResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionSearchByTitleResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionsResponse;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
import com.ureca.filmeet.domain.collection.exception.CollectionNotFoundException;
import com.ureca.filmeet.domain.collection.exception.CollectionUserNotFoundException;
import com.ureca.filmeet.domain.collection.repository.CollectionCommentRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionLikeRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionMovieRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
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
    private final CollectionLikeRepository collectionLikeRepository;
    private final CollectionCommentRepository collectionCommentRepository;

    public Slice<CollectionsResponse> getCollections(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(CollectionUserNotFoundException::new);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Slice<Collection> collections = collectionRepository.findCollectionsByUserId(user.getId(), pageable);

        List<Long> collectionIds = collections.getContent()
                .stream()
                .map(Collection::getId)
                .toList();

        List<CollectionMovie> collectionMovies = collectionMovieRepository.findMoviesByCollectionIds(collectionIds);

        return collections.map(collection -> {
            // 각 컬렉션의 영화 데이터를 가져오기
            List<CollectionMovieInfoResponse> movies = getCollectionMovies(collection.getId(), collectionMovies);
            return CollectionsResponse.from(collection, movies);
        });
    }

    private List<CollectionMovieInfoResponse> getCollectionMovies(Long collectionId,
                                                                  List<CollectionMovie> collectionMovies) {
        return collectionMovies.stream()
                .filter(cm -> cm.getCollection().getId().equals(collectionId))
                .map(cm -> new CollectionMovieInfoResponse(
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

    public CollectionDetailResponse getCollection(Long collectionId, Long userId) {
        Collection collection = collectionRepository.findCollectionByCollectionIdAndUserId(collectionId)
                .orElseThrow(CollectionNotFoundException::new);

        boolean existsCollectionLike = collectionLikeRepository.existsByCollectionIdAndUserId(collectionId, userId);

        return CollectionDetailResponse.from(collection, existsCollectionLike);
    }

    public Slice<CollectionMovieInfoResponse> getCollectionMovies(Long collectionId, Pageable pageable) {
        return collectionMovieRepository.findMoviesBy(collectionId, pageable)
                .map(CollectionMovieInfoResponse::of);
    }

    public Slice<CollectionCommentsResponse> getCollectionComments(Long collectionId, Pageable pageable) {
        return collectionCommentRepository.findCommentsBy(collectionId, pageable)
                .map(CollectionCommentsResponse::of);
    }

    public Slice<CollectionSearchByTitleResponse> searchCollectionByTitle(String titleKeyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "created_at");
        return collectionRepository.findCollectionsByTitleKeyword(titleKeyword, pageable)
                .map(CollectionSearchByTitleResponse::of);
    }
}
