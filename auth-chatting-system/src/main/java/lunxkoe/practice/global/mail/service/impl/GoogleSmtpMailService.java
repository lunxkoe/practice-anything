package lunxkoe.practice.global.mail.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lunxkoe.practice.global.mail.exception.MailSendErrorException;
import lunxkoe.practice.global.mail.service.MailService;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class GoogleSmtpMailService implements MailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  public GoogleSmtpMailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
    this.mailSender = mailSender;
    this.templateEngine = templateEngine;
  }

  @Override
  public void sendTempPassword(String toEmail, String rawTempPassword, int expireMinutes) {
    try {
      Context context = new Context();
      context.setVariable("temporaryPassword", rawTempPassword);
      context.setVariable("expireMinutes", expireMinutes);
      String html = templateEngine.process("mail/temporary-password", context);

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
      helper.setTo(toEmail);
      helper.setSubject("[옷장을 부탁해] 임시 비밀번호 안내");
      helper.setText(html, true);

      mailSender.send(message);
    } catch (MessagingException | MailException e) {
      throw new MailSendErrorException(toEmail, e);
    }
  }
}
