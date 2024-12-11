//package com.ureca.filmeet.domain.review.repository;
//
//import static com.ureca.filmeet.global.util.TestUtils.createMovie;
//import static com.ureca.filmeet.global.util.TestUtils.createMovieRatings;
//import static com.ureca.filmeet.global.util.TestUtils.createReview;
//import static com.ureca.filmeet.global.util.TestUtils.createReviewComment;
//import static com.ureca.filmeet.global.util.TestUtils.createReviewLikes;
//import static com.ureca.filmeet.global.util.TestUtils.createUser;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.tuple;
//
//import com.ureca.filmeet.domain.movie.entity.Movie;
//import com.ureca.filmeet.domain.movie.entity.MovieRatings;
//import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
//import com.ureca.filmeet.domain.movie.repository.MovieRatingsRepository;
//import com.ureca.filmeet.domain.movie.repository.MovieRepository;
//import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse;
//import com.ureca.filmeet.domain.review.dto.response.UserReviewsResponse;
//import com.ureca.filmeet.domain.review.dto.response.trending.ReviewResponse;
//import com.ureca.filmeet.domain.review.entity.Review;
//import com.ureca.filmeet.domain.review.entity.ReviewComment;
//import com.ureca.filmeet.domain.review.entity.ReviewLikes;
//import com.ureca.filmeet.domain.user.entity.Provider;
//import com.ureca.filmeet.domain.user.entity.Role;
//import com.ureca.filmeet.domain.user.entity.User;
//import com.ureca.filmeet.domain.user.repository.UserRepository;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.util.Optional;
//import java.util.UUID;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.domain.Sort;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//@ActiveProfiles("local")
//@Transactional
//@SpringBootTest
//class ReviewRepositoryTest {
//
//    @Autowired
//    private MovieRepository movieRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ReviewRepository reviewRepository;
//
//    @Autowired
//    private ReviewCommentRepository reviewCommentRepository;
//
//    @Autowired
//    private ReviewLikesRepository reviewLikesRepository;
//
//    @Autowired
//    private MovieRatingsRepository movieRatingsRepository;
//
//    @DisplayName("삭제되지 않았고 숨김 처리가 안된 리뷰를 성공적으로 조회한다.")
//    @Test
//    void findVisibleReviewByMovieAndUser() {
//        // given
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review = createReview("리뷰 내용", movie, user);
//
//        // when
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review);
//        Optional<Review> findReview = reviewRepository.findReviewBy(movie.getId(), user.getId());
//
//        // then
//        assertThat(findReview)
//                .isPresent()
//                .get()
//                .extracting("content", "user", "movie")
//                .contains(
//                        "리뷰 내용", user, movie
//                );
//    }
//
//    @DisplayName("삭제된 리뷰는 조회되지 않는다.")
//    @Test
//    void reviewMarkedAsDeleted_isNotPresentWhenQueried() {
//        // given
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review = createReview("리뷰 내용", movie, user);
//
//        // when
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review);
//        review.delete();
//        Optional<Review> findReview = reviewRepository.findReviewBy(movie.getId(), user.getId());
//
//        // then
//        assertThat(findReview).isNotPresent();
//    }
//
//    @DisplayName("리뷰 ID로 리뷰를 성공적으로 조회한다.")
//    @Test
//    void findVisibleReviewByReviewId() {
//        // given
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review = createReview("리뷰 내용", movie, user);
//
//        // when
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review);
//        Optional<Review> findReview = reviewRepository.findReviewBy(review.getId());
//
//        // then
//        assertThat(findReview)
//                .isPresent()
//                .get()
//                .extracting("content", "user", "movie")
//                .contains(
//                        "리뷰 내용", user, movie
//                );
//    }
//
//    @DisplayName("삭제된 리뷰는 리뷰 ID로 조회되지 않는다.")
//    @Test
//    void findReviewById_whenDeleted_returnsEmpty() {
//        // given
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review = createReview("리뷰 내용", movie, user);
//
//        // when
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review);
//        review.delete();
//        Optional<Review> findReview = reviewRepository.findReviewBy(review.getId());
//
//        // then
//        assertThat(findReview).isNotPresent();
//    }
//
//    @DisplayName("영화 ID와 리뷰 ID가 일치하고 리뷰가 삭제되지 않고 숨겨지지 않았을 경우 리뷰를 반환한다.")
//    @Test
//    void findReviewByMovieIdAndReviewId_whenReviewIsVisibleAndNotDeleted_returnsReview() {
//        // given
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review = createReview("리뷰 내용", movie, user);
//
//        // when
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review);
//        Optional<Review> result = reviewRepository.findReviewByMovieIdAndReviewId(review.getId(), movie.getId());
//
//        // then
//        assertThat(result)
//                .isPresent()
//                .get()
//                .extracting("content", "movie")
//                .contains("리뷰 내용", movie);
//    }
//
//    @DisplayName("리뷰가 삭제된 경우 조회되지 않는다.")
//    @Test
//    void findReviewByMovieIdAndReviewId_whenReviewIsDeleted_returnsEmpty() {
//        // given
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review = createReview("리뷰 내용", movie, user);
//
//        // when
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review);
//        review.delete();
//        Optional<Review> result = reviewRepository.findReviewByMovieIdAndReviewId(review.getId(), movie.getId());
//
//        // then
//        assertThat(result).isNotPresent();
//    }
//
//    @DisplayName("영화 ID가 일치하지 않을 경우 조회되지 않는다.")
//    @Test
//    void findReviewByMovieIdAndReviewId_whenMovieIdDoesNotMatch_returnsEmpty() {
//        // given
//        Movie movie1 = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        Movie movie2 = createMovie("제목2", "다른 줄거리", LocalDate.now(), 120, "https://def", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review = createReview("리뷰 내용", movie1, user);
//
//        movieRepository.save(movie1);
//        movieRepository.save(movie2);
//        userRepository.save(user);
//        reviewRepository.save(review);
//
//        // when
//        Optional<Review> result = reviewRepository.findReviewByMovieIdAndReviewId(review.getId(), movie2.getId());
//
//        // then
//        assertThat(result).isNotPresent();
//    }
//
//    @DisplayName("리뷰 ID가 일치하지 않을 경우 조회되지 않는다.")
//    @Test
//    void findReviewByMovieIdAndReviewId_whenReviewIdDoesNotMatch_returnsEmpty() {
//        // given
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review1 = createReview("리뷰 내용1", movie, user);
//        Review review2 = createReview("리뷰 내용2", movie, user);
//
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review1);
//        reviewRepository.save(review2);
//
//        // when
//        Optional<Review> result = reviewRepository.findReviewByMovieIdAndReviewId(review2.getId() + 1, movie.getId());
//
//        // then
//        assertThat(result).isNotPresent();
//    }
//
//    @DisplayName("댓글 ID와 리뷰 ID가 일치하고 리뷰가 삭제되지 않고 숨겨지지 않았을 경우 리뷰를 성공적으로 조회한다.")
//    @Test
//    void test() {
//        // given
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review = createReview("리뷰 내용", movie, user);
//        ReviewComment reviewComment = createReviewComment("리뷰 댓글 내용", review, user);
//
//        // when
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review);
//        reviewCommentRepository.save(reviewComment);
//        Optional<Review> findReview = reviewRepository.findReviewByReviewIdAndCommentId(
//                review.getId(), reviewComment.getId());
//
//        // then
//        assertThat(findReview)
//                .isPresent()
//                .get()
//                .extracting("content", "user", "movie")
//                .contains(
//                        "리뷰 내용", user, movie
//                );
//    }
//
//    @DisplayName("댓글 ID가 일치하지 않는 경우 조회되지 않는다.")
//    @Test
//    void findReviewByReviewIdAndCommentId_whenCommentIdDoesNotMatch_returnsEmpty() {
//        // given
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review1 = createReview("리뷰 내용", movie, user);
//        Review review2 = createReview("리뷰 내용", movie, user);
//        ReviewComment comment1 = createReviewComment("댓글 내용", review1, user);
//        ReviewComment comment2 = createReviewComment("댓글 내용", review2, user);
//
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review1);
//        reviewRepository.save(review2);
//        reviewCommentRepository.save(comment1);
//        reviewCommentRepository.save(comment2);
//
//        // when
//        Optional<Review> result1 = reviewRepository.findReviewByReviewIdAndCommentId(review1.getId(), comment2.getId());
//        Optional<Review> result2 = reviewRepository.findReviewByReviewIdAndCommentId(review2.getId(), comment1.getId());
//
//        // then
//        assertThat(result1).isNotPresent();
//        assertThat(result2).isNotPresent();
//    }
//
//    @DisplayName("리뷰 ID가 일치하지 않는 경우 조회되지 않는다.")
//    @Test
//    void findReviewByReviewIdAndCommentId_whenReviewIdDoesNotMatch_returnsEmpty() {
//        // given
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
//                "https://example.com/profile.jpg");
//        Review review1 = createReview("리뷰 내용1", movie, user);
//        Review review2 = createReview("리뷰 내용2", movie, user);
//        ReviewComment comment = createReviewComment("댓글 내용", review1, user);
//
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review1);
//        reviewRepository.save(review2);
//        reviewCommentRepository.save(comment);
//
//        // when
//        Optional<Review> result = reviewRepository.findReviewByReviewIdAndCommentId(review2.getId(), comment.getId());
//
//        // then
//        assertThat(result).isNotPresent();
//    }
//
//    @DisplayName("영화에 대한 리뷰와 특정 사용자의 좋아요 여부를 반환한다.")
//    @Test
//    void findMovieReviewsWithLikes_whenMovieHasReviews_returnsReviewsWithLikeStatus() {
//        // given
//        String username1 = UUID.randomUUID() + "";
//        String username2 = UUID.randomUUID() + "";
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user1 = createUser(username1, "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임1",
//                "https://example.com/profile1.jpg");
//        User user2 = createUser(username2, "securePassword", Role.ROLE_USER, Provider.GOOGLE, "닉네임2",
//                "https://example.com/profile2.jpg");
//        Review review1 = createReview("리뷰 내용1", movie, user1);
//        Review review2 = createReview("리뷰 내용2", movie, user1);
//        ReviewLikes like = createReviewLikes(review1, user2);
//
//        // when
//        movieRepository.save(movie);
//        userRepository.save(user1);
//        userRepository.save(user2);
//        reviewRepository.save(review1);
//        reviewRepository.save(review2);
//        reviewLikesRepository.save(like);
//        Pageable pageable = PageRequest.of(0, 10);
//        Slice<GetMovieReviewsResponse> result1 = reviewRepository.findMovieReviewsWithLikes(movie.getId(),
//                user2.getId(),
//                pageable);
//        Slice<GetMovieReviewsResponse> result2 = reviewRepository.findMovieReviewsWithLikes(movie.getId(),
//                user1.getId(),
//                pageable);
//
//        // then
//        assertThat(result1.getContent())
//                .hasSize(2)
//                .extracting("reviewId", "userId", "content", "likeCounts", "commentCounts", "nickName", "profileImage",
//                        "isLiked")
//                .containsExactlyInAnyOrder(
//                        tuple(review1.getId(), user1.getId(), "리뷰 내용1", 0, 0, "닉네임1",
//                                "https://example.com/profile1.jpg", true),
//                        tuple(review2.getId(), user1.getId(), "리뷰 내용2", 0, 0, "닉네임1",
//                                "https://example.com/profile1.jpg", false)
//                );
//        assertThat(result2.getContent())
//                .hasSize(2)
//                .extracting("reviewId", "userId", "content", "likeCounts", "commentCounts", "nickName", "profileImage",
//                        "isLiked")
//                .containsExactlyInAnyOrder(
//                        tuple(review1.getId(), user1.getId(), "리뷰 내용1", 0, 0, "닉네임1",
//                                "https://example.com/profile1.jpg", false),
//                        tuple(review2.getId(), user1.getId(), "리뷰 내용2", 0, 0, "닉네임1",
//                                "https://example.com/profile1.jpg", false)
//                );
//    }
//
//    @DisplayName("삭제된 리뷰는 반환되지 않는다.")
//    @Test
//    void findMovieReviewsWithLikes_whenReviewIsDeleted_returnsEmpty() {
//        // given
//        String username = UUID.randomUUID() + "";
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user = createUser(username, "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임1",
//                "https://example.com/profile1.jpg");
//        Review review = createReview("리뷰 내용", movie, user);
//        review.delete(); // 리뷰 삭제
//
//        movieRepository.save(movie);
//        userRepository.save(user);
//        reviewRepository.save(review);
//
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // when
//        Slice<GetMovieReviewsResponse> result = reviewRepository.findMovieReviewsWithLikes(movie.getId(), user.getId(),
//                pageable);
//
//        // then
//        assertThat(result).isEmpty();
//    }
//
//    @DisplayName("리뷰 상세 정보를 조회할때 리뷰와 연관된 영화, 유저, 댓글, 댓글에 대한 유저 정보를 성공적으로 조회한다.")
//    @Test
//    void shouldRetrieveReviewWithAssociatedDetails() {
//        // given
//        String username1 = UUID.randomUUID() + "";
//        String username2 = UUID.randomUUID() + "";
//        String username3 = UUID.randomUUID() + "";
//        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
//        User user1 = createUser(username1, "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임1",
//                "https://example.com/profile1.jpg");
//        User user2 = createUser(username2, "securePassword", Role.ROLE_ADMIN, Provider.GOOGLE, "닉네임2",
//                "https://example.com/profile1.jpg");
//        User user3 = createUser(username3, "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임3",
//                "https://example.com/profile1.jpg");
//        Review review = createReview("리뷰 내용", movie, user1);
//        ReviewComment reviewComment1 = createReviewComment("리뷰 댓글 내용1", review, user1);
//        ReviewComment reviewComment2 = createReviewComment("리뷰 댓글 내용2", review, user2);
//        ReviewComment reviewComment3 = createReviewComment("리뷰 댓글 내용3", review, user3);
//
//        // when
//        movieRepository.save(movie);
//        userRepository.save(user1);
//        userRepository.save(user2);
//        userRepository.save(user3);
//        reviewRepository.save(review);
//        reviewCommentRepository.save(reviewComment1);
//        reviewCommentRepository.save(reviewComment2);
//        reviewCommentRepository.save(reviewComment3);
//        Optional<Review> movieReviewDetail = reviewRepository.findMovieReviewDetailBy(review.getId());
//
//        // then
//        assertThat(movieReviewDetail)
//                .isPresent()
//                .get()
//                .satisfies(foundReview -> {
//                    // 리뷰 자체의 정보 검증
//                    assertThat(foundReview.getContent()).isEqualTo("리뷰 내용");
//                    assertThat(foundReview.getUser()).isEqualTo(user1);
//                    assertThat(foundReview.getMovie()).isEqualTo(movie);
//
//                    // 리뷰와 연관된 유저 정보 검증
//                    assertThat(foundReview.getUser())
//                            .isNotNull()
//                            .satisfies(user -> {
//                                assertThat(user.getUsername()).isEqualTo(username1);
//                                assertThat(user.getNickname()).isEqualTo(user1.getNickname());
//                            });
//
//                    // 리뷰와 연관된 무비 정보 검증
//                    assertThat(foundReview.getMovie())
//                            .isNotNull()
//                            .satisfies(findMovie -> {
//                                assertThat(findMovie.getTitle()).isEqualTo(movie.getTitle());
//                                assertThat(findMovie.getPlot()).isEqualTo(movie.getPlot());
//                            });
//
//                    assertThat(foundReview.getReviewComments())
//                            .hasSize(3)
//                            .extracting("content", "user.id", "user.profileImage", "user.nickname", "user")
//                            .containsExactlyInAnyOrder(
//                                    tuple("리뷰 댓글 내용1", user1.getId(), user1.getProfileImage(), user1.getNickname(),
//                                            user1),
//                                    tuple("리뷰 댓글 내용2", user2.getId(), user2.getProfileImage(), user2.getNickname(),
//                                            user2),
//                                    tuple("리뷰 댓글 내용3", user3.getId(), user3.getProfileImage(), user3.getNickname(),
//                                            user3)
//                            );
//                });
//    }
//
//    @DisplayName("유저가 작성한 리뷰 목록과 각 리뷰의 좋아요 여부 및 평점 정보를 정확히 조회한다.")
//    @Test
//    void findTrendingReviewsByUser() {
//        // given
//        String username1 = UUID.randomUUID() + "";
//        String username2 = UUID.randomUUID() + "";
//        Movie movie1 = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc",
//                FilmRatings.ADULT);
//        Movie movie2 = createMovie("제목2", "줄거리", LocalDate.now(), 250, "https://abc",
//                FilmRatings.ADULT);
//        Movie movie3 = createMovie("제목3", "줄거리", LocalDate.now(), 350, "https://abc",
//                FilmRatings.ADULT);
//        User user1 = createUser(username1, "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임1",
//                "https://example.com/profile1.jpg");
//        User user2 = createUser(username2, "securePassword", Role.ROLE_USER, Provider.GOOGLE, "닉네임2",
//                "https://example.com/profile2.jpg");
//        Review review1 = createReview("리뷰 내용1", movie1, user1);
//        Review review2 = createReview("리뷰 내용2", movie2, user1);
//        Review review3 = createReview("리뷰 내용3", movie3, user2);
//        ReviewLikes like1 = createReviewLikes(review1, user2);
//        ReviewLikes like2 = createReviewLikes(review2, user1);
//        ReviewLikes like3 = createReviewLikes(review3, user1);
//
//        MovieRatings rating1 = createMovieRatings(movie1, user1, BigDecimal.valueOf(4.5));
//        MovieRatings rating2 = createMovieRatings(movie2, user1, BigDecimal.valueOf(1.5));
//        MovieRatings rating3 = createMovieRatings(movie2, user2, BigDecimal.valueOf(3.0));
//        MovieRatings rating4 = createMovieRatings(movie3, user2, BigDecimal.valueOf(2.0));
//
//        // when
//        movieRepository.save(movie1);
//        movieRepository.save(movie2);
//        movieRepository.save(movie3);
//        userRepository.save(user1);
//        userRepository.save(user2);
//        reviewRepository.save(review1);
//        reviewRepository.save(review2);
//        reviewRepository.save(review3);
//        reviewLikesRepository.save(like1);
//        reviewLikesRepository.save(like2);
//        reviewLikesRepository.save(like3);
//        movieRatingsRepository.save(rating1);
//        movieRatingsRepository.save(rating2);
//        movieRatingsRepository.save(rating3);
//        movieRatingsRepository.save(rating4);
//        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "createdAt");
//        Slice<ReviewResponse> result1 = reviewRepository.findTrendingReviewsBy(user1.getId(), pageable);
//        Slice<ReviewResponse> result2 = reviewRepository.findTrendingReviewsBy(user2.getId(), pageable);
//
//        // then
//        assertThat(result1.getContent())
//                .hasSize(3)
//                .extracting(
//                        ReviewResponse::reviewId,
//                        ReviewResponse::userId,
//                        ReviewResponse::movieId,
//                        ReviewResponse::content,
//                        ReviewResponse::likeCounts,
//                        ReviewResponse::commentCounts,
//                        ReviewResponse::createdAt,
//                        ReviewResponse::nickname,
//                        ReviewResponse::profileImage,
//                        ReviewResponse::movieTitle,
//                        ReviewResponse::posterUrl,
//                        content -> content.ratingScore().setScale(1, RoundingMode.HALF_UP), // 스케일 통일
//                        ReviewResponse::isLiked
//                )
//                .containsExactlyInAnyOrder(
//                        tuple(review1.getId(), user1.getId(), movie1.getId(), "리뷰 내용1", 0, 0, review1.getCreatedAt(),
//                                "닉네임1", "https://example.com/profile1.jpg", "제목1", "https://abc",
//                                BigDecimal.valueOf(4.5).setScale(1, RoundingMode.HALF_UP), false),
//                        tuple(review2.getId(), user1.getId(), movie2.getId(), "리뷰 내용2", 0, 0, review2.getCreatedAt(),
//                                "닉네임1", "https://example.com/profile1.jpg", "제목2", "https://abc",
//                                BigDecimal.valueOf(1.5).setScale(1, RoundingMode.HALF_UP), true),
//                        tuple(review3.getId(), user2.getId(), movie3.getId(), "리뷰 내용3", 0, 0, review3.getCreatedAt(),
//                                "닉네임2", "https://example.com/profile2.jpg", "제목3", "https://abc",
//                                BigDecimal.valueOf(2.0).setScale(1, RoundingMode.HALF_UP), true)
//                );
//
//        assertThat(result2.getContent())
//                .hasSize(3)
//                .extracting(
//                        ReviewResponse::reviewId,
//                        ReviewResponse::userId,
//                        ReviewResponse::movieId,
//                        ReviewResponse::content,
//                        ReviewResponse::likeCounts,
//                        ReviewResponse::commentCounts,
//                        ReviewResponse::createdAt,
//                        ReviewResponse::nickname,
//                        ReviewResponse::profileImage,
//                        ReviewResponse::movieTitle,
//                        ReviewResponse::posterUrl,
//                        content -> content.ratingScore().setScale(1, RoundingMode.HALF_UP), // 스케일 통일
//                        ReviewResponse::isLiked
//                )
//                .containsExactlyInAnyOrder(
//                        tuple(review1.getId(), user1.getId(), movie1.getId(), "리뷰 내용1", 0, 0, review1.getCreatedAt(),
//                                "닉네임1", "https://example.com/profile1.jpg", "제목1", "https://abc",
//                                BigDecimal.valueOf(4.5).setScale(1, RoundingMode.HALF_UP), true),
//                        tuple(review2.getId(), user1.getId(), movie2.getId(), "리뷰 내용2", 0, 0, review2.getCreatedAt(),
//                                "닉네임1", "https://example.com/profile1.jpg", "제목2", "https://abc",
//                                BigDecimal.valueOf(1.5).setScale(1, RoundingMode.HALF_UP), false),
//                        tuple(review3.getId(), user2.getId(), movie3.getId(), "리뷰 내용3", 0, 0, review3.getCreatedAt(),
//                                "닉네임2", "https://example.com/profile2.jpg", "제목3", "https://abc",
//                                BigDecimal.valueOf(2.0).setScale(1, RoundingMode.HALF_UP), false)
//                );
//    }
//
//    @DisplayName("특정 유저의 리뷰 목록과 각 리뷰에 대한 영화 정보, 좋아요 여부 및 평점 정보를 조회한다.")
//    @Test
//    void findUserReviews_whenUserHasReviews_returnsPagedUserReviews() {
//        // given
//        String username = UUID.randomUUID() + "";
//        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.of(2023, 1, 1), 120, "https://poster1.jpg",
//                FilmRatings.ADULT);
//        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.of(2023, 2, 1), 130, "https://poster2.jpg",
//                FilmRatings.ALL);
//        Movie movie3 = createMovie("제목3", "줄거리3", LocalDate.of(2023, 2, 1), 140, "https://poster3.jpg",
//                FilmRatings.ALL);
//        User user = createUser(username, "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임1",
//                "https://example.com/profile1.jpg");
//        Review review1 = createReview("리뷰 내용1", movie1, user);
//        Review review2 = createReview("리뷰 내용2", movie2, user);
//        Review review3 = createReview("리뷰 내용3", movie3, user);
//        ReviewLikes like1 = createReviewLikes(review1, user);
//
//        MovieRatings rating1 = createMovieRatings(movie1, user, BigDecimal.valueOf(4.5));
//        MovieRatings rating2 = createMovieRatings(movie2, user, BigDecimal.valueOf(3.0));
//
//        // when
//        movieRepository.save(movie1);
//        movieRepository.save(movie2);
//        movieRepository.save(movie3);
//        userRepository.save(user);
//        reviewRepository.save(review1);
//        reviewRepository.save(review2);
//        reviewRepository.save(review3);
//        reviewLikesRepository.save(like1);
//        movieRatingsRepository.save(rating1);
//        movieRatingsRepository.save(rating2);
//
//        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
//        Slice<UserReviewsResponse> result = reviewRepository.findUserReviews(user.getId(), pageable);
//
//        // then
//        assertThat(result.getContent())
//                .hasSize(3)
//                .extracting(
//                        UserReviewsResponse::reviewId,
//                        UserReviewsResponse::userId,
//                        UserReviewsResponse::movieId,
//                        UserReviewsResponse::reviewContent,
//                        UserReviewsResponse::likeCounts,
//                        UserReviewsResponse::commentCounts,
//                        UserReviewsResponse::nickname,
//                        UserReviewsResponse::profileImage,
//                        UserReviewsResponse::movieTitle,
//                        UserReviewsResponse::posterUrl,
//                        UserReviewsResponse::releaseDate,
//                        content -> {
//                            BigDecimal ratingScore = content.ratingScore();
//                            return ratingScore != null ? ratingScore.setScale(1, RoundingMode.HALF_UP)
//                                    : null; // Handle null
//                        }, UserReviewsResponse::isLiked
//                )
//                .containsExactlyInAnyOrder(
//                        tuple(review1.getId(), user.getId(), movie1.getId(), "리뷰 내용1", 0, 0, "닉네임1",
//                                "https://example.com/profile1.jpg", "제목1", "https://poster1.jpg",
//                                LocalDate.of(2023, 1, 1), BigDecimal.valueOf(4.5).setScale(1, RoundingMode.HALF_UP),
//                                true),
//                        tuple(review2.getId(), user.getId(), movie2.getId(), "리뷰 내용2", 0, 0, "닉네임1",
//                                "https://example.com/profile1.jpg", "제목2", "https://poster2.jpg",
//                                LocalDate.of(2023, 2, 1), BigDecimal.valueOf(3.0).setScale(1, RoundingMode.HALF_UP),
//                                false),
//                        tuple(review3.getId(), user.getId(), movie3.getId(), "리뷰 내용3", 0, 0, "닉네임1",
//                                "https://example.com/profile1.jpg", "제목3", "https://poster3.jpg",
//                                LocalDate.of(2023, 2, 1), null,
//                                false)
//                );
//    }
//}