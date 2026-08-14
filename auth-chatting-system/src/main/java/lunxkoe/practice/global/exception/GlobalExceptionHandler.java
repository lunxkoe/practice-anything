package lunxkoe.practice.global.exception;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lunxkoe.practice.global.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ErrorResponse> handleAppException(AppException e) {
    String exceptionName = e.getClass().getSimpleName();
    String message = e.getMessage();
    HttpStatus status = e.getStatus();
    Map<String, Object> details = e.getDetails();
    log.warn("[{}] {}", exceptionName, message);
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(
                exceptionName,
                message,
                details
            )
        );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    String exceptionName = e.getClass().getSimpleName();
    String message = "요청 값이 올바르지 않습니다.";
    Map<String, Object> details = e.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fieldError -> fieldError.getDefaultMessage(),
            (existing, replacement) -> existing
        ));
    log.warn("[{}] {}", exceptionName, details);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(
                exceptionName,
                message,
                details
            )
        );
  }

  @ExceptionHandler(MissingRequestCookieException.class)
  public ResponseEntity<ErrorResponse> handleMissingRequestCookieException(MissingRequestCookieException e) {
    String exceptionName = e.getClass().getSimpleName();
    String message = "요청에 필요한 쿠키(" + e.getCookieName() + ")가 없습니다.";
    log.warn("[{}] {}", exceptionName, message);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(
                exceptionName,
                message,
                Map.of()
            )
        );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    String exceptionName = "InternalServerException";
    String message = "서버 내부에 오류가 발생했습니다. 불편을 드려 죄송합니다.";
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    Map<String, Object> details = Map.of();
    log.error("[{}] {}", e.getClass().getSimpleName(), e.getMessage(), e);
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(
                exceptionName,
                message,
                details
            )
        );
  }
}
