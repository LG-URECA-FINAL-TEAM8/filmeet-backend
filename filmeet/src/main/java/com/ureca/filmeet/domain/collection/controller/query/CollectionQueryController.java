package com.ureca.filmeet.domain.collection.controller.query;

import com.ureca.filmeet.domain.collection.dto.response.CollectionGetResponse;
import com.ureca.filmeet.domain.collection.service.service.CollectionQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collections")
public class CollectionQueryController {

    private final CollectionQueryService collectionQueryService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Slice<CollectionGetResponse>>> getCollections(@PathVariable("userId") Long userId,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size) {

        Slice<CollectionGetResponse> collectionGetResponses = collectionQueryService.getCollections(userId, page, size);
        return ApiResponse.ok(collectionGetResponses);
    }
}
