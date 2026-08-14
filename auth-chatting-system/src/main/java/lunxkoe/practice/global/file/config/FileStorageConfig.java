package lunxkoe.practice.global.file.config;

import static lunxkoe.practice.global.file.properties.enums.FileStorageType.LOCAL;
import static lunxkoe.practice.global.file.properties.enums.FileStorageType.S3;

import lunxkoe.practice.global.file.properties.FileProperties;
import lunxkoe.practice.global.file.storage.FileStorageService;
import lunxkoe.practice.global.file.storage.impl.LocalFileStorageService;
import lunxkoe.practice.global.file.storage.impl.S3FileStorageService;
import lunxkoe.practice.global.file.validator.FileValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class FileStorageConfig {

  @Bean
  public FileStorageService fileStorageService(FileProperties fileProperties, FileValidator fileValidator) {
    return switch (fileProperties.impl()) {
      case LOCAL -> new LocalFileStorageService(fileProperties, fileValidator);
      case S3 -> new S3FileStorageService(
          S3Client.builder()
              .region(Region.of(fileProperties.s3().region()))
              .build(),
          fileProperties, fileValidator
      );
    };
  }
}
