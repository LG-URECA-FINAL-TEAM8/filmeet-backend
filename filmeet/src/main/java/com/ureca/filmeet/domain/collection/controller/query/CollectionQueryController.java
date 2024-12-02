package com.ureca.filmeet.domain.collection.controller.query;

import com.ureca.filmeet.domain.collection.dto.response.CollectionCommentsResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionDetailResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionMovieInfoResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionSearchByTitleResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionsResponse;
import com.ureca.filmeet.domain.collection.service.service.CollectionQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/collections")
public class CollectionQueryController {

    private final CollectionQueryService collectionQueryService;

    @GetMapping("/list/users/{userId}")
    public ResponseEntity<ApiResponse<SliceResponseDto<CollectionsResponse>>> getCollections(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Slice<CollectionsResponse> collectionGetResponses = collectionQueryService.getCollections(userId, page, size);
        return ApiResponse.ok(SliceResponseDto.of(collectionGetResponses));
    }

    @GetMapping("/{collectionId}")
    public ResponseEntity<ApiResponse<CollectionDetailResponse>> getCollection(
            @PathVariable("collectionId") Long collectionId
    ) {

        CollectionDetailResponse collectionsResponse = collectionQueryService.getCollection(collectionId);
        return ApiResponse.ok(collectionsResponse);
    }

    @GetMapping("/{collectionId}/movies")
    public ResponseEntity<ApiResponse<SliceResponseDto<CollectionMovieInfoResponse>>> getCollectionMovies(
            @PathVariable("collectionId") Long collectionId,
            Pageable pageable
    ) {

        Slice<CollectionMovieInfoResponse> collectionMovies = collectionQueryService.getCollectionMovies(collectionId,
                pageable);
        return ApiResponse.ok(SliceResponseDto.of(collectionMovies));
    }

    @GetMapping("/{collectionId}/comments")
    public ResponseEntity<ApiResponse<SliceResponseDto<CollectionCommentsResponse>>> getCollectionComments(
            @PathVariable("collectionId") Long collectionId,
            Pageable pageable
    ) {

        Slice<CollectionCommentsResponse> collectionComments = collectionQueryService.getCollectionComments(
                collectionId,
                pageable);
        return ApiResponse.ok(SliceResponseDto.of(collectionComments));
    }

    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<SliceResponseDto<CollectionSearchByTitleResponse>>> searchMoviesByTitle(
            @RequestParam(value = "titleKeyword") String titleKeyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Slice<CollectionSearchByTitleResponse> collectionSearchByTitleResponses = collectionQueryService.searchCollectionByTitle(
                titleKeyword, page, size);
        return ApiResponse.ok(SliceResponseDto.of(collectionSearchByTitleResponses));
    }
}