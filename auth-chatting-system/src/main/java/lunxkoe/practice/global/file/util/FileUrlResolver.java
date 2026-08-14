package lunxkoe.practice.global.file.util;

import lombok.RequiredArgsConstructor;
import lunxkoe.practice.global.file.properties.FileProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class FileUrlResolver {

  private final FileProperties fileProperties;

  public String resolve(String key) {
    if (key == null) {
      return null;
    }

    String baseUrl = StringUtils.trimTrailingCharacter(fileProperties.publicBaseUrl(), '/');
    String trimmedKey = StringUtils.trimLeadingCharacter(key, '/');
    return baseUrl + "/" + trimmedKey;
  }
}
