package com.ureca.filmeet.domain.user.entity;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ureca.filmeet.domain.user.entity.PrivilegeSets.*;

@Getter
public enum Role {
    ROLE_MINOR_USER(
            USER_COMMON_PRIVILEGES
    ),
    ROLE_ADULT_USER(
            Stream.of(USER_COMMON_PRIVILEGES, ADULT_USER_PRIVILEGES)
                    .flatMap(Set::stream).collect(Collectors.toSet())
    ),
    ROLE_MOVIE_ADMIN(
            Stream.of(USER_COMMON_PRIVILEGES, ADMIN_MOVIE_PRIVILEGES, ADMIN_EXTERNAL_API_PRIVILEGES)
                    .flatMap(Set::stream).collect(Collectors.toSet())
    ),
    ROLE_REVIEW_ADMIN(
            Stream.of(USER_COMMON_PRIVILEGES, ADMIN_REVIEW_PRIVILEGES)
                    .flatMap(Set::stream).collect(Collectors.toSet())
    ),
    ROLE_SUPER_ADMIN(
            Stream.of(USER_COMMON_PRIVILEGES, ADMIN_MOVIE_PRIVILEGES, ADMIN_REVIEW_PRIVILEGES, ADMIN_EXTERNAL_API_PRIVILEGES)
                    .flatMap(Set::stream).collect(Collectors.toSet())
    );

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority(this.name()));

        return authorities;
    }
}