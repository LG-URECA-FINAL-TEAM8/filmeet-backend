package com.ureca.filmeet.domain.user.entity;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ureca.filmeet.domain.user.entity.Permission.*;

@Getter
public enum Role {
    ROLE_ADMIN(Set.of(
            READ,  //Permission enum class
            CREATE,
            UPDATE,
            DELETE
    )),
    ROLE_USER(Set.of(
            READ,
            CREATE,
            UPDATE
    ));

    private final Set<Permission> permissions;  //Permission enum class

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