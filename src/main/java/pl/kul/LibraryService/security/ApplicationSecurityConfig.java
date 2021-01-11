package pl.kul.LibraryService.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kul.LibraryService.auth.AuthUserService;
import pl.kul.LibraryService.jwt.JwtAuthFilter;
import pl.kul.LibraryService.jwt.JwtConfig;
import pl.kul.LibraryService.jwt.JwtTokenVerifier;

import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //for @PreAuthorize
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;
    private final AuthUserService authUserService;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, AuthUserService authUserService, JwtConfig jwtConfig) {
        this.passwordEncoder = passwordEncoder;
        this.authUserService = authUserService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new ThrottleFilter(), JwtAuthFilter.class)
                .addFilter(new JwtAuthFilter(authenticationManager(), jwtConfig))
                .addFilterAfter(new JwtTokenVerifier(jwtConfig), JwtAuthFilter.class)
                .authorizeRequests()
                .antMatchers("/*", "index", "/pl/*", "/css/*", "/graphic/*").permitAll()  //free access to main index page
                .antMatchers("/api/library/books/**").permitAll()  //reading list of books doesn't need an authorization
                .antMatchers("/register").permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth){
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(authUserService);
        return provider;
    }
}
