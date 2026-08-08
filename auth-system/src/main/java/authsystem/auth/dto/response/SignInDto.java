package authsystem.auth.dto.response;

public record SignInDto(
    JwtDto jwtDto,
    String refreshToken
) {

}
