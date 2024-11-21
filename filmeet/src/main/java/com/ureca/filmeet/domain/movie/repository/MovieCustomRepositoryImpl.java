package com.ureca.filmeet.domain.movie.repository;

import static com.ureca.filmeet.domain.genre.entity.QGenre.genre;
import static com.ureca.filmeet.domain.genre.entity.QMovieGenre.movieGenre;
import static com.ureca.filmeet.domain.movie.entity.QMovie.movie;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.dto.response.MoviesSearchByGenreResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class MovieCustomRepositoryImpl implements MovieCustomRepository {

    private final JPQLQueryFactory queryFactory;

    @Override
    public Page<MoviesSearchByGenreResponse> searchMoviesByGenre(List<GenreType> genreTypes, Pageable pageable) {
        // 1. 영화 ID 목록 가져오기
        List<Long> movieIds = queryFactory
                .select(movie.id)
                .from(movie)
                .join(movie.movieGenres, movieGenre)
                .join(movieGenre.genre, genre)
                .where(genreTypeIn(genreTypes))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (movieIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 2. 영화별 데이터 및 장르 수집
        List<Tuple> tuples = queryFactory
                .select(
                        movie.id,
                        movie.title,
                        movie.posterUrl,
                        movie.releaseDate,
                        movie.runtime,
                        movie.likeCounts,
                        movie.reviewCounts,
                        movie.averageRating,
                        movie.filmRatings,
                        genre.genreType
                )
                .from(movie)
                .join(movie.movieGenres, movieGenre)
                .join(movieGenre.genre, genre)
                .where(movie.id.in(movieIds))
                .orderBy(movie.releaseDate.desc())
                .fetch();

        // 3. 영화별로 장르 리스트를 그룹화
        Map<Long, MoviesSearchByGenreResponse> movieMap = new LinkedHashMap<>();
        for (Tuple tuple : tuples) {
            Long movieId = tuple.get(movie.id);
            MoviesSearchByGenreResponse response = movieMap.computeIfAbsent(movieId,
                    id -> new MoviesSearchByGenreResponse(
                            id,
                            tuple.get(movie.title),
                            tuple.get(movie.posterUrl),
                            tuple.get(movie.releaseDate),
                            tuple.get(movie.runtime),
                            tuple.get(movie.likeCounts),
                            tuple.get(movie.reviewCounts),
                            tuple.get(movie.averageRating),
                            tuple.get(movie.filmRatings),
                            new ArrayList<>()
                    ));
            response.genreTypes().add(tuple.get(genre.genreType));
        }

        JPQLQuery<Long> countQuery = queryFactory
                .select(movie.id)
                .from(movie)
                .join(movie.movieGenres, movieGenre)
                .join(movieGenre.genre, genre)
                .where(genreTypeIn(genreTypes))
                .distinct();

        return PageableExecutionUtils.getPage(new ArrayList<>(movieMap.values()), pageable, countQuery::fetchCount);
    }

    // 동적 조건: 장르 필터링
    private BooleanExpression genreTypeIn(List<GenreType> genreTypes) {
        return (genreTypes == null || genreTypes.isEmpty()) ? null : genre.genreType.in(genreTypes);
    }
}
