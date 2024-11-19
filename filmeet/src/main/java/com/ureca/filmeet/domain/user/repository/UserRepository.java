package com.ureca.filmeet.domain.user.repository;

import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByProviderId(String providerId);

    Optional<User> findByProviderAndProviderId(Provider Provider, String ProviderId);

    Boolean existsByUsername(String username);
}
