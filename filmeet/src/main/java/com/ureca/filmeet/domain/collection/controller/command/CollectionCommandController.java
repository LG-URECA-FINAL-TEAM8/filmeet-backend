package com.ureca.filmeet.domain.collection.controller.command;

import com.ureca.filmeet.domain.collection.dto.request.CollectionCreateRequest;
import com.ureca.filmeet.domain.collection.dto.request.CollectionModifyRequest;
import com.ureca.filmeet.domain.collection.dto.response.CollectionCreateResponse;
import com.ureca.filmeet.domain.collection.dto.response.CollectionModifyResponse;
import com.ureca.filmeet.domain.collection.service.command.CollectionCommandService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/collections")
public class CollectionCommandController {

    private final CollectionCommandService collectionCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<CollectionCreateResponse>> createCollection(
            @RequestBody CollectionCreateRequest collectionCreateRequest,
            @AuthenticationPrincipal User user
    ) {
        Long collectionId = collectionCommandService.createCollection(collectionCreateRequest, user.getId());
        CollectionCreateResponse collectionCreateResponse = new CollectionCreateResponse(collectionId);
        return ApiResponse.ok(collectionCreateResponse);
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<CollectionModifyResponse>> modifyCollection(
            @RequestBody CollectionModifyRequest collectionModifyRequest) {
        Long collectionId = collectionCommandService.modifyCollection(collectionModifyRequest);
        CollectionModifyResponse collectionCreateResponse = new CollectionModifyResponse(collectionId);
        return ApiResponse.ok(collectionCreateResponse);
    }

    @DeleteMapping("/{collectionId}")
    public void deleteCollection(@PathVariable("collectionId") Long collectionId) {
        collectionCommandService.deleteCollection(collectionId);
    }
}