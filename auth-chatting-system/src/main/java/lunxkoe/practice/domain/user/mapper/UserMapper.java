package lunxkoe.practice.domain.user.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lunxkoe.practice.domain.user.dto.request.LocationRequest;
import lunxkoe.practice.domain.user.dto.response.LocationDto;
import lunxkoe.practice.domain.user.dto.response.ProfileDto;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.domain.user.entity.Location;
import lunxkoe.practice.domain.user.entity.Profile;
import lunxkoe.practice.domain.user.entity.User;
import lunxkoe.practice.global.file.util.FileUrlResolver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final FileUrlResolver fileUrlResolver;

  public UserDto userDtoFrom(User user) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getEmail(),
        user.getName(),
        user.getRole(),
        user.isLocked()
    );
  }

  public ProfileDto profileDtoFrom(Profile profile) {
    return new ProfileDto(
        profile.getId(),
        profile.getUser().getName(),
        profile.getGender(),
        profile.getBirthDate(),
        locationDtoFrom(profile.getLocation()),
        profile.getTemperatureSensitivity(),
        fileUrlResolver.resolve(profile.getProfileImageUrl())
    );
  }

  public LocationDto locationDtoFrom(Location location) {
    return location == null ? null
        : new LocationDto(
            location.getLatitude(),
            location.getLongitude(),
            location.getLocationX(),
            location.getLocationY(),
            location.getLocationNames() == null ? null
                : List.copyOf(location.getLocationNames())
        );
  }

  public Location locationFrom(LocationRequest request) {
    return request == null ? null
        : Location.create(request.latitude(), request.longitude(), request.x(), request.y(),
            request.locationNames());
  }
}
