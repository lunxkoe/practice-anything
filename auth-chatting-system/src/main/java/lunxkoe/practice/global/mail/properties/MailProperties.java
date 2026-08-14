package lunxkoe.practice.global.mail.properties;

import lunxkoe.practice.global.mail.properties.enums.MailServiceType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.mail")
public record MailProperties(

  MailServiceType impl
) {

  public MailProperties {
    if (impl == null) {
      impl = MailServiceType.GOOGLE;
    }
  }
}
