package com.ureca.filmeet.domain.user.entity;

import lombok.Getter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
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
            Stream.of(ADULT_USER_PRIVILEGES)
                    .flatMap(Set::stream).collect(Collectors.toSet())
    ),
    ROLE_MOVIE_ADMIN(
            Stream.of(ADMIN_MOVIE_PRIVILEGES, ADMIN_EXTERNAL_API_PRIVILEGES)
                    .flatMap(Set::stream).collect(Collectors.toSet())
    ),
    ROLE_REVIEW_ADMIN(
            Stream.of(ADMIN_REVIEW_PRIVILEGES)
                    .flatMap(Set::stream).collect(Collectors.toSet())
    ),
    ROLE_SUPER_ADMIN(Collections.emptySet());

    private final Set<Permission> permissions;
    private static RoleHierarchy roleHierarchy;

    public static void setRoleHierarchy(RoleHierarchy hierarchy) {
        roleHierarchy = hierarchy;
    }

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<Role> reachableRoles = roleHierarchy.getReachableGrantedAuthorities(
                        List.of(new SimpleGrantedAuthority(this.name()))
                ).stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_")) // ROLE_XXX만 필터링
                .map(Role::valueOf)
                .toList();

        List<SimpleGrantedAuthority> authorities = reachableRoles.stream()
                .flatMap(role -> role.permissions.stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());

        reachableRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .forEach(authorities::add);

        return authorities;
    }
}