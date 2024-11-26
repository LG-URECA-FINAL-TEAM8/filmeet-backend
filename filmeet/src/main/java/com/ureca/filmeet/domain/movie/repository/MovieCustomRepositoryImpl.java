package com.ureca.filmeet.domain.movie.repository;

import static com.ureca.filmeet.domain.genre.entity.QGenre.genre;
import static com.ureca.filmeet.domain.genre.entity.QMovieGenre.movieGenre;
import static com.ureca.filmeet.domain.movie.entity.QMovie.movie;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.dto.response.MovieSearchByTitleResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesSearchByGenreResponse;
import com.ureca.filmeet.domain.movie.dto.response.QMovieSearchByTitleResponse;
import com.ureca.filmeet.domain.movie.dto.response.QMoviesResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class MovieCustomRepositoryImpl implements MovieCustomRepository {

    private final JPQLQueryFactory queryFactory;

    @Override
    public Slice<MoviesSearchByGenreResponse> searchMoviesByGenre(List<GenreType> genreTypes, Pageable pageable) {
        // 1. 영화 ID 목록 가져오기
        List<Long> movieIds = queryFactory
                .select(movie.id)
                .from(movie)
                .join(movie.movieGenres, movieGenre)
                .join(movieGenre.genre, genre)
                .where(isNotDeleted().and(genreTypeIn(genreTypes)))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        if (movieIds.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, false);
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

        boolean hasNext = movieIds.size() > pageable.getPageSize();

        return new SliceImpl<>(new ArrayList<>(movieMap.values()), pageable, hasNext);
    }

    // 동적 조건: 장르 필터링
    private BooleanExpression genreTypeIn(List<GenreType> genreTypes) {
        return (genreTypes == null || genreTypes.isEmpty()) ? null : genre.genreType.in(genreTypes);
    }

    @Override
    public Slice<MovieSearchByTitleResponse> searchMoviesByTitle(String title, Pageable pageable) {
        BooleanExpression predicate = isNotDeleted();
        predicate = predicate.and(titleContains(title));

        List<MovieSearchByTitleResponse> content = queryFactory
                .select(new QMovieSearchByTitleResponse(
                        movie.releaseDate,
                        movie.title,
                        movie.posterUrl,
                        movie.id
                ))
                .from(movie)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        // hasNext 판단: 반환된 데이터가 pageSize보다 많으면 true
        boolean hasNext = content.size() > pageable.getPageSize();

        // Slice에 맞게 데이터 자르기 (pageSize 크기만큼만 반환)
        if (hasNext) {
            content = content.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression isNotDeleted() {
        return movie.isDeleted.isFalse();
    }

    // 동적 조건: 제목 검색
    private BooleanExpression titleContains(String title) {
        if (title == null || title.isBlank()) {
            return null;
        }

        String cleanedTitle = preprocessTitle(title);
        return movie.title.containsIgnoreCase(title)
                .or(Expressions.booleanTemplate(
                        "function('REPLACE', {0}, ' ', '') like {1}",
                        movie.title, "%" + cleanedTitle + "%"
                ));
    }

    // 검색어 전처리
    private String preprocessTitle(String title) {
        return (title == null || title.isBlank()) ? "" : title.replace(" ", "");
    }

    @Override
    public Slice<MoviesResponse> findMoviesByGenre(GenreType genreType, Pageable pageable) {
        boolean isGenreFilterActive = genreType != null;

        JPQLQuery<MoviesResponse> query = queryFactory
                .select(new QMoviesResponse(
                        movie.id,
                        movie.title,
                        movie.posterUrl,
                        movie.releaseDate
                ))
                .from(movie);

        // 장르 필터에 따라 조건 추가
        addJoinAndWhereClause(query, isGenreFilterActive, genreType);
        List<MoviesResponse> movies = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = movies.size() > pageable.getPageSize();
        if (hasNext) {
            movies = movies.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(movies, pageable, hasNext);
    }

    private void addJoinAndWhereClause(JPQLQuery<?> query, boolean isGenreFilterActive, GenreType genreType) {
        if (isGenreFilterActive) {
            query.join(movie.movieGenres, movieGenre)
                    .join(movieGenre.genre, genre)
                    .where(isNotDeleted(), genreTypeEquals(genreType));
        } else {
            query.where(isNotDeleted());
        }
    }

    private BooleanExpression genreTypeEquals(GenreType genreType) {
        return genreType == null ? null : genre.genreType.eq(genreType);
    }
}