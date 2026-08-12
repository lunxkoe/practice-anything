package lunxkoe.practice.common.exception;

import java.util.Map;

public record ErrorResponse(
    String exceptionName,
    String message,
    Map<String, Object> details
) {

}
