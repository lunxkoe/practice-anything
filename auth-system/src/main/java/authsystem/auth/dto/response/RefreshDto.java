package authsystem.auth.dto.response;

public record RefreshDto(
    JwtDto jwtDto,
    String refreshToken
) {

}
