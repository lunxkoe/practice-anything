package lunxkoe.practice.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

  private Double latitude;

  private Double longitude;

  @Column
  private Integer locationX;

  @Column
  private Integer locationY;

  @JdbcTypeCode(SqlTypes.JSON)
  private List<String> locationNames;

  private Location(Double latitude, Double longitude, Integer locationX, Integer locationY, List<String> locationNames) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.locationX = locationX;
    this.locationY = locationY;
    this.locationNames = locationNames != null ? List.copyOf(locationNames) : null;
  }

  public static Location create(Double latitude, Double longitude, Integer locationX, Integer locationY, List<String> locationNames) {
    return new Location(latitude, longitude, locationX, locationY, locationNames);
  }

  public List<String> getLocationNames() {
    return locationNames != null ? List.copyOf(locationNames) : null;
  }
}
