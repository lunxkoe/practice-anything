package authsystem.security.core.token.exception.system;

public class TokenProviderException extends RuntimeException {

  private TokenProviderException(String message, Throwable cause) {
    super(message, cause);
  }

  public static TokenProviderException withMessageAndCause(String message, Throwable cause) {
    return new TokenProviderException(message, cause);
  }
}
