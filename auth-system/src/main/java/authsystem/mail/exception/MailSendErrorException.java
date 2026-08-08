package authsystem.mail.exception;

public class MailSendErrorException extends RuntimeException {

  public MailSendErrorException(String toEmail, Throwable cause) {
    super("메일 발송 실패: to=" + toEmail, cause);
  }
}
