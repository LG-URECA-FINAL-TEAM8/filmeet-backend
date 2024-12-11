package com.ureca.filmeet.domain.movie.repository.querydsl;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

@Getter
public class SliceWithCount<T> extends SliceImpl<T> {

    private final long ratedMovieCount;

    public SliceWithCount(List<T> content, Pageable pageable, boolean hasNext, long ratedMovieCount) {
        super(content, pageable, hasNext);
        this.ratedMovieCount = ratedMovieCount;
    }
}