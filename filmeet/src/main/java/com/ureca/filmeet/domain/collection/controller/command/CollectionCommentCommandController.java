package com.ureca.filmeet.domain.collection.controller.command;

import com.ureca.filmeet.domain.collection.dto.request.CollectionCommentCreateRequest;
import com.ureca.filmeet.domain.collection.dto.request.CollectionCommentModifyRequest;
import com.ureca.filmeet.domain.collection.service.command.CollectionCommentCommandService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/collections/comments")
public class CollectionCommentCommandController {

    private final CollectionCommentCommandService collectionCommentCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createCollectionComment(
            @RequestBody CollectionCommentCreateRequest collectionCommentCreateRequest,
            @AuthenticationPrincipal User user
    ) {

        Long collectionCommentId = collectionCommentCommandService.createCollectionComment(
                collectionCommentCreateRequest,
                user.getId());
        return ApiResponse.ok(collectionCommentId);
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Long>> modifyCollectionComment(
            @RequestBody CollectionCommentModifyRequest collectionCommentModifyRequest,
            @AuthenticationPrincipal User user
    ) {
        Long collectionCommentId = collectionCommentCommandService.modifyCollectionComment(
                collectionCommentModifyRequest, user.getId());
        return ApiResponse.ok(collectionCommentId);
    }
}