package lunxkoe.practice.domain.user.dto.response;

import java.time.LocalDate;
import java.util.UUID;
import lunxkoe.practice.domain.user.entity.enums.Gender;

public record ProfileDto(
    UUID userId,
    String name,
    Gender gender,
    LocalDate birthDate,
    LocationDto location,
    int temperatureSensitivity,
    String profileImageUrl
) {

}
