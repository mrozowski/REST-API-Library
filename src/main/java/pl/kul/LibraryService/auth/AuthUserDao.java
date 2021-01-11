package pl.kul.LibraryService.auth;

import java.util.Optional;

public interface AuthUserDao {

    Optional<AuthUser> selectAuthUserByUsername(String username);
}
