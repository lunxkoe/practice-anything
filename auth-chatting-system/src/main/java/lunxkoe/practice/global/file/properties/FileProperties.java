package lunxkoe.practice.global.file.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lunxkoe.practice.global.file.properties.enums.FileStorageType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.file")
public record FileProperties(

    @NotBlank
    String publicBaseUrl,

    @Positive
    long maxSizeBytes,

    @NotEmpty
    Set<String> allowedExtensions,

    @Valid
    Local local,

    @Valid
    S3 s3,

    FileStorageType impl
) {

  public FileProperties {
    if (impl == null) {
      impl = FileStorageType.LOCAL;
    }
    if (allowedExtensions != null) {
      allowedExtensions = allowedExtensions.stream()
          .map(extension -> extension.toLowerCase(Locale.ROOT))
          .collect(Collectors.toUnmodifiableSet());
    }
  }

  @AssertTrue(message = "impl이 local이면 local.upload-dir이, s3면 s3.bucket과 s3.region이 필요합니다.")
  private boolean isModeConfigValid() {
    if (impl == FileStorageType.LOCAL) {
      return local != null && StringUtils.hasText(local.uploadDir());
    }
    if (impl == FileStorageType.S3) {
      return s3 != null && StringUtils.hasText(s3.bucket()) && StringUtils.hasText(s3.region());
    }
    return true;
  }

  public record Local(@NotBlank String uploadDir) {

  }

  public record S3(@NotBlank String bucket, @NotBlank String region) {

  }
}
