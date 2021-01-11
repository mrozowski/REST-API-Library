package pl.kul.LibraryService.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static pl.kul.LibraryService.security.AppUserPermission.*;

public enum AppUserRole {
    USER(Sets.newHashSet(ORDERS_READ, ORDERS_WRITE, BOOKS_READ, USER_READ, USER_WRITE)),
    ADMIN(Sets.newHashSet(ORDERS_READ, ORDERS_WRITE, BOOKS_READ, BOOKS_WRITE, USER_READ, USER_WRITE, BOOK_COPY_READ, BOOK_COPY_WRITE));

    private final Set<AppUserPermission> permissions;

    AppUserRole(Set<AppUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<AppUserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
