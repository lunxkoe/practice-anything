package lunxkoe.practice.security.config;

import java.util.List;
import lunxkoe.practice.security.exception.ErrorResponseWriter;
import lunxkoe.practice.security.filter.TokenAuthenticationFilter;
import lunxkoe.practice.security.token.provider.TokenProvider;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public AuthenticationManager authenticationManager(List<AuthenticationProvider> providers) {
    return new ProviderManager(providers);
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      TokenProvider tokenProvider,
      UserSessionRegistry userSessionRegistry,
      JsonMapper jsonMapper
  ) throws Exception {

    http.formLogin(AbstractHttpConfigurer::disable);
    http.httpBasic(AbstractHttpConfigurer::disable);
    http.logout(AbstractHttpConfigurer::disable);

    http.csrf(AbstractHttpConfigurer::disable);
    // TODO: CSRF 설정 추가

    http.sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    http.addFilterBefore(
        new TokenAuthenticationFilter(tokenProvider, userSessionRegistry),
        UsernamePasswordAuthenticationFilter.class
    );

    http.authorizeHttpRequests(auth -> auth

        .requestMatchers(HttpMethod.GET, "/api/sandbox/main").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/sandbox/my").hasAnyAuthority("USER", "ADMIN")
        .requestMatchers(HttpMethod.GET, "/api/sandbox/admin").hasAuthority("ADMIN")

        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ADMIN")

        .anyRequest().authenticated()
    );

    http.exceptionHandling(ex -> ex
        .defaultAuthenticationEntryPointFor((request, response, authException) ->
            ErrorResponseWriter.write(response, jsonMapper, HttpStatus.UNAUTHORIZED,
                authException, "인증이 필요합니다."), AnyRequestMatcher.INSTANCE)

        .accessDeniedHandler((request, response, accessDeniedException) ->
            ErrorResponseWriter.write(response, jsonMapper, HttpStatus.FORBIDDEN,
                accessDeniedException, "접근 권한이 없습니다."))
    );

    // TODO: OAuth2 설정 추가

    return http.build();
  }
}
