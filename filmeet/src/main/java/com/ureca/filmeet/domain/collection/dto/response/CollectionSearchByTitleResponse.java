package com.ureca.filmeet.domain.collection.dto.response;


import java.math.BigInteger;

public record CollectionSearchByTitleResponse(

        Long collectionId,
        Long userId,
        String title,
        String content,
        String nickname,
        String profileImage
) {

    public static CollectionSearchByTitleResponse of(Object[] result) {
        return new CollectionSearchByTitleResponse(
                ((BigInteger) result[0]).longValue(), // collection_id
                ((BigInteger) result[1]).longValue(), // member_id
                (String) result[2],                  // title
                (String) result[3],                  // content
                (String) result[4],                  // nickname
                (String) result[5]                   // profile_image
        );
    }
}
