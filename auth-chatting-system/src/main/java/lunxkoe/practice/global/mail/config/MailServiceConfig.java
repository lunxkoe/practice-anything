package lunxkoe.practice.global.mail.config;

import lunxkoe.practice.global.mail.properties.MailProperties;
import lunxkoe.practice.global.mail.service.MailService;
import lunxkoe.practice.global.mail.service.impl.GoogleSmtpMailService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

@Configuration
@EnableConfigurationProperties({
    MailProperties.class
})
public class MailServiceConfig {

  @Bean
  public MailService mailService(MailProperties mailProperties, JavaMailSender javaMailSender, TemplateEngine templateEngine) {
    return switch (mailProperties.impl()) {
      case GOOGLE -> new GoogleSmtpMailService(
          javaMailSender,
          templateEngine
      );
    };
  }
}
