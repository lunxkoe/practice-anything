package authsystem.security.web.config;

import authsystem.security.core.session.registry.UserSessionRegistry;
import authsystem.security.core.token.provider.TokenProvider;
import authsystem.security.web.details.CustomUserDetailsService;
import authsystem.security.web.exception.ErrorResponseWriter;
import authsystem.security.web.filter.TokenAuthenticationFilter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(proxyTargetClass = true)
public class SecurityConfig {

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider(
      CustomUserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder
  ) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(List<AuthenticationProvider> providers) {
    return new ProviderManager(providers);
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      JsonMapper jsonMapper,
      TokenProvider tokenProvider,
      UserSessionRegistry userSessionRegistry,
      List<SecurityAuthorizationRules> authorizationRulesList
  ) {
    http.formLogin(AbstractHttpConfigurer::disable);
    http.httpBasic(AbstractHttpConfigurer::disable);
    http.logout(AbstractHttpConfigurer::disable);

    http.csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
    );

    http.sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    http.addFilterBefore(
        new TokenAuthenticationFilter(tokenProvider, userSessionRegistry),
        UsernamePasswordAuthenticationFilter.class
    );

    http.exceptionHandling(ex -> ex
        .authenticationEntryPoint((request, response, authException) ->
            ErrorResponseWriter.write(response, jsonMapper, HttpStatus.UNAUTHORIZED,
                authException, "인증이 필요합니다."))
        .accessDeniedHandler((request, response, accessDeniedException) ->
            ErrorResponseWriter.write(response, jsonMapper, HttpStatus.FORBIDDEN,
                accessDeniedException, "접근 권한이 없습니다."))
    );

    http.authorizeHttpRequests(registry -> {
      // 등록된 SecurityAuthorizationRules 빈을 전부 모아 경로별 규칙을 쌓는다.
      // 각 구현체는 anyRequest()를 호출하지 않는다 — 한 레지스트리에 anyRequest()를
      // 두 번 호출하면 IllegalStateException이 나므로, 마지막 기본값은 여기서 한 번만 정한다.
      for (SecurityAuthorizationRules rules : authorizationRulesList) {
        rules.configure(registry);
      }
      registry.anyRequest().authenticated();
    });

    return http.build();
  }
}
