package lunxkoe.practice.domain.auth.dto.response;

public record SignInDto(
    JwtDto jwtDto,
    String refreshToken
) {

}
