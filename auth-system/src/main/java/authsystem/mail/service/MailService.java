package authsystem.mail.service;

public interface MailService {

  void sendTempPassword(String toEmail, String rawTempPassword, int expireMinutes);
}
