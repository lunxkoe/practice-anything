package authsystem.auth.dto.response;

public record JwtDto(
    AuthUserDto user,
    String accessToken
) {

}
