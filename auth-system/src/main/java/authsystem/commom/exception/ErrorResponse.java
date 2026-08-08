package authsystem.commom.exception;

import java.util.Map;

public record ErrorResponse(
    String exceptionName,
    String message,
    Map<String, Object> details
) {

}
