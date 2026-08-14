package lunxkoe.practice.domain.auth.event;

import lombok.RequiredArgsConstructor;
import lunxkoe.practice.domain.auth.event.request.TempPasswordRequestedEvent;
import lunxkoe.practice.global.mail.service.MailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TempPasswordEventListener {

  private final MailService mailService;

  @Async("mailExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void tempPasswordRequestHandler(TempPasswordRequestedEvent event) {
    mailService.sendTempPassword(event.email(), event.rawTempPassword(), event.expireMinutes());
  }
}
