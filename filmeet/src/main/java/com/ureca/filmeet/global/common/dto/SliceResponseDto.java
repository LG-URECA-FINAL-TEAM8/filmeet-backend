package com.ureca.filmeet.global.common.dto;

import java.util.List;
import org.springframework.data.domain.Slice;

public record SliceResponseDto<T>(
        List<T> content,
        int currentPage,
        int size,
        boolean first,
        boolean last,
        boolean hasNext
) {
    public static <T> SliceResponseDto<T> of(Slice<T> sliceContent) {
        return new SliceResponseDto<>(
                sliceContent.getContent(),
                sliceContent.getNumber(),
                sliceContent.getSize(),
                sliceContent.isFirst(),
                sliceContent.isLast(),
                sliceContent.hasNext()
        );
    }
}