package lunxkoe.practice.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.formLogin(AbstractHttpConfigurer::disable);
    http.httpBasic(AbstractHttpConfigurer::disable);
    http.logout(AbstractHttpConfigurer::disable);

    http.csrf(AbstractHttpConfigurer::disable);
    // TODO: CSRF 설정 추가

    http.sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    // TODO: Token Authentication Filter 설정 추가

    http.authorizeHttpRequests(auth -> auth

        .requestMatchers(HttpMethod.GET, "/api/sandbox/main").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/sandbox/my").hasAnyAuthority("USER", "ADMIN")
        .requestMatchers(HttpMethod.GET, "/api/sandbox/admin").hasAuthority("ADMIN")

        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

        .anyRequest().authenticated()
    );

    // TODO: Security Exception Handler 설정 추가

    // TODO: OAuth2 설정 추가

    return http.build();
  }
}
