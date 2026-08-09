package authsystem.auth.dto.response;

public record JwtDto(
    AuthUserDto userDto,
    String accessToken
) {

}
