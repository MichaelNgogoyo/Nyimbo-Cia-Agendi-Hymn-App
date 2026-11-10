package org.nyimbo.agendi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.util.Assert;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService users(@Value("${NYIMBO_ADMIN_USERNAME:admin}") String username,
                                   @Value("${NYIMBO_ADMIN_PASSWORD:}") String password,
                                   PasswordEncoder encoder) {
        Assert.hasText(password, "Set NYIMBO_ADMIN_PASSWORD before starting the server.");
        return new InMemoryUserDetailsManager(User.withUsername(username).password(encoder.encode(password)).roles("ADMIN").build());
    }
    @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/", "/admin", "/admin/**", "/uploads/**", "/api/v1/events", "/api/v1/news", "/api/v1/hymns", "/api/v1/health").permitAll()
                .requestMatchers("/api/v1/admin/**", "/api/v1/hymns/import", "/api/v1/uploads/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
