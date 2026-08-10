package authsystem.security.web.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface HttpSecurityCustomizer {

  void customize(HttpSecurity http) throws Exception;
}
