package com.ureca.filmeet.domain.collection.controller.command;

import com.ureca.filmeet.domain.collection.service.command.CollectionLikeCommandService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class CollectionCommentLikeController {

    private final CollectionLikeCommandService collectionLikeCommandService;

    @PostMapping("/collections/{collectionId}")
    public ResponseEntity<ApiResponse<String>> collectionLikes(
            @PathVariable Long collectionId,
            @AuthenticationPrincipal User user
    ) {

        collectionLikeCommandService.collectionLikes(collectionId, user.getId());
        return ApiResponse.ok("좋아요를 눌렀습니다.");
    }

    @DeleteMapping("/cancel/collections/{collectionId}")
    public ResponseEntity<ApiResponse<String>> collectionLikesCancel(
            @PathVariable Long collectionId,
            @AuthenticationPrincipal User user
    ) {

        collectionLikeCommandService.collectionLikesCancel(collectionId, user.getId());
        return ApiResponse.ok("좋아요를 취소 했습니다.");
    }
}
