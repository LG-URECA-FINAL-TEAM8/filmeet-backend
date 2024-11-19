package com.ureca.filmeet.domain.user.repository;

import com.ureca.filmeet.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByProviderId(String providerId);

    Boolean existsByUsername(String username);
}
