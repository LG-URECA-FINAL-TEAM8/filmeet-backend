package com.ureca.filmeet.domain.collection.service.command;

import com.ureca.filmeet.domain.collection.dto.request.CollectionCreateRequest;
import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.collection.repository.CollectionMovieBulkRepository;
import com.ureca.filmeet.domain.collection.repository.CollectionRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollectionCommandService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CollectionRepository collectionRepository;
    private final CollectionMovieBulkRepository collectionMovieBulkRepository;

    @Transactional
    public Long createCollection(CollectionCreateRequest collectionCreateRequest) {

        User user = userRepository.findById(collectionCreateRequest.userId())
                .orElseThrow(() -> new RuntimeException("no user"));

        List<Movie> movies = movieRepository.findMoviesByMovieIds(collectionCreateRequest.movieIds());

        if (movies.isEmpty()) {
            throw new RuntimeException("No movies found for the given IDs");
        }

        Collection collection = Collection.builder()
                .title(collectionCreateRequest.title())
                .content(collectionCreateRequest.content())
                .user(user)
                .build();
        Collection savedCollection = collectionRepository.save(collection);

        collectionMovieBulkRepository.saveAll(collection, movies);

        return savedCollection.getId();
    }
}
