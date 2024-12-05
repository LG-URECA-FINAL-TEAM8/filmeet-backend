package com.ureca.filmeet.domain.user.service.command;

import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.GenreScore;
import com.ureca.filmeet.domain.genre.entity.enums.GenreScoreAction;
import com.ureca.filmeet.domain.genre.repository.GenreRepository;
import com.ureca.filmeet.domain.genre.repository.GenreScoreBulkRepository;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.user.dto.request.UpdatePreferenceRequest;
import com.ureca.filmeet.domain.user.dto.request.UserSignUpRequest;
import com.ureca.filmeet.domain.user.dto.response.UserDetailResponse;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.exception.UserInvalidNicknameException;
import com.ureca.filmeet.domain.user.exception.UserInvalidUsernameException;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.domain.user.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenreRepository genreRepository;
    private final GenreScoreBulkRepository genreScoreBulkRepository;
    private final GenreScoreRepository genreScoreRepository;
    private final UserQueryService userQueryService;

    public UserDetailResponse signUp(UserSignUpRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserInvalidUsernameException();
        }
        if (userRepository.existsByNickname(request.nickname())) {
            throw new UserInvalidNicknameException();
        }

        User newUser = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(Role.ROLE_USER)
                .profileImage("https://filmeet-images.s3.ap-northeast-2.amazonaws.com/2024/12/03/1e928cad-c203-41e7-9184-8e46e6bf1ee0_default_profile.svg")
                .build();
        User savedUser = userRepository.save(newUser);

        initializeGenreScores(savedUser);
        return new UserDetailResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole(),
                savedUser.getNickname(),
                savedUser.getProfileImage()
        );
    }

    @Transactional
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

    @Transactional
    public void UpdateProfileImage(String profileImage, User loginUser) {
        User user = userQueryService.findById(loginUser.getId());
        user.updateProfileImage(profileImage);
    }

    @Transactional
    public void updatePreference(UpdatePreferenceRequest request, User loginUser) {
        User user = userQueryService.findById(loginUser.getId());
        user.updatePreference(request.mbti(), request.age());

        genreScoreRepository.bulkUpdateGenreScores(
                GenreScoreAction.PREFERRED_GENRE.getWeight(),
                request.genreIds(),
                user.getId());
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
