package lunxkoe.practice.domain.auth.dto.response;

public record RefreshDto(
    JwtDto jwtDto,
    String refreshToken
) {

}
