package pl.kul.LibraryService.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import pl.kul.LibraryService.security.AppUserRole;

import java.util.Optional;

@Repository("auth_postgres")
public class PostgresAuthUserDaoService implements AuthUserDao{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostgresAuthUserDaoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<AuthUser> selectAuthUserByUsername(String username) {
        String sql = "Select username, password, email, role from users where username = ?";
        AuthUser user = null;
        try{
            user = jdbcTemplate.queryForObject(sql, new Object[]{username}, mapAuthUsers());
            return Optional.ofNullable(user);
        }
        catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }

    }

    private RowMapper<AuthUser> mapAuthUsers() {
        return ((rs, rowNum) -> new AuthUser(
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                AppUserRole.valueOf(rs.getString("role")).getGrantedAuthorities(),
                true,
                true,
                true,
                true
        ));
    }
}
