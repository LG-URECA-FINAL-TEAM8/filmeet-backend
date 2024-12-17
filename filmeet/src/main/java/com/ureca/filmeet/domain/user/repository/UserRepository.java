package com.ureca.filmeet.domain.user.repository;

import com.ureca.filmeet.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByNickname(String nickname);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.id IN :followingUserIds"
    )
    List<User> findUsersByFollowingUserIds(
            List<Long> followingUserIds
    );
}
