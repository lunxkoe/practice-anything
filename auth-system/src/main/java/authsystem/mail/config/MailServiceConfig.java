package authsystem.mail.config;

import authsystem.mail.service.MailService;
import authsystem.mail.service.impl.GoogleSmtpMailService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

@Configuration
public class MailServiceConfig {

  @Bean
  @ConditionalOnProperty(name = "auth-system.mail.impl", havingValue = "google", matchIfMissing = true)
  public MailService googleSmtpMailService(JavaMailSender mailSender,
      TemplateEngine templateEngine) {
    return new GoogleSmtpMailService(mailSender, templateEngine);
  }
}
