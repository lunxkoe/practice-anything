package lunxkoe.practice.global.file.config;

import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lunxkoe.practice.global.file.properties.FileProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class FileWebConfig implements WebMvcConfigurer {

  private final FileProperties fileProperties;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    switch (fileProperties.impl()) {
      case LOCAL -> registerLocalResourceHandler(registry);
      case S3 -> { }
    }
  }

  private void registerLocalResourceHandler(ResourceHandlerRegistry registry) {
    Path uploadPath = Path.of(fileProperties.local().uploadDir()).toAbsolutePath().normalize();
    String location = "file:///" + uploadPath.toString().replace("\\", "/") + "/";

    registry.addResourceHandler("/uploads/**")
        .addResourceLocations(location);
  }
}
