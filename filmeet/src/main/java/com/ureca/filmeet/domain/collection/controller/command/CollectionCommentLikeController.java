package com.ureca.filmeet.domain.collection.controller.command;

import com.ureca.filmeet.domain.collection.service.command.CollectionCommentLikeService;
import com.ureca.filmeet.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
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

    private final CollectionCommentLikeService collectionCommentLikeService;

    @PostMapping("/collections/{collectionId}")
    public void collectionLikes(
            @PathVariable Long collectionId,
            @AuthenticationPrincipal User user
    ) {

        collectionCommentLikeService.collectionLikes(collectionId, user.getId());
    }

    @DeleteMapping("/cancel/collections/{collectionId}")
    public void collectionLikesCancel(
            @PathVariable Long collectionId,
            @AuthenticationPrincipal User user
    ) {

        collectionCommentLikeService.collectionLikesCancel(collectionId, user.getId());
    }
}