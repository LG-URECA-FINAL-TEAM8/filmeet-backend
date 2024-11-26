package com.ureca.filmeet.domain.user.service.command;

import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.GenreScore;
import com.ureca.filmeet.domain.genre.repository.GenreRepository;
import com.ureca.filmeet.domain.genre.repository.GenreScoreBulkRepository;
import com.ureca.filmeet.domain.user.dto.request.UserSignUpRequest;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenreRepository genreRepository;
    private final GenreScoreBulkRepository genreScoreBulkRepository;

    public User signUp(UserSignUpRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User newUser = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(Role.ROLE_USER)
                .build();
        User savedUser = userRepository.save(newUser);

        initializeGenreScores(savedUser);

        return savedUser;
    }

    public User createTemporaryUser(String providerId, String name, Provider provider, String profileImage) {
        User tempUser = User.builder()
                .username(providerId)
                .provider(provider)
                .nickname(name) // 이름
                .profileImage(profileImage)
                .role(Role.ROLE_USER)
                .build();
        User savedUser = userRepository.save(tempUser);

        initializeGenreScores(savedUser);

        return savedUser;
    }

    private void initializeGenreScores(User user) {
        List<Genre> genres = genreRepository.findAll();

        List<GenreScore> genreScores = genres.stream()
                .map(genre -> GenreScore.builder()
                        .user(user)
                        .genre(genre)
                        .score(0) // 초기 점수
                        .build())
                .toList();
        genreScoreBulkRepository.saveAll(genreScores);
    }
}
