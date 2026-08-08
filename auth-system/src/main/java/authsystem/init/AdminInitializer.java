package authsystem.init;

import authsystem.user.entity.Profile;
import authsystem.user.entity.User;
import authsystem.user.repository.ProfileRepository;
import authsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@Configuration
@EnableConfigurationProperties(AdminProperties.class)
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;
  private final PasswordEncoder passwordEncoder;
  private final AdminProperties adminProperties;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (!StringUtils.hasText(adminProperties.password())) {
      log.warn("ADMIN_PASSWORD가 설정되지 않아 관리자 계정 초기화를 건너뜁니다.");
      return;
    }

    if (userRepository.existsByEmail(adminProperties.email())) {
      log.info("관리자 계정이 이미 존재합니다: {}", adminProperties.email());
      return;
    }

    User admin = User.createAdmin(
        adminProperties.name(),
        adminProperties.email(),
        passwordEncoder.encode(adminProperties.password())
    );

    User savedAdmin;
    try {
      savedAdmin = userRepository.saveAndFlush(admin);
    } catch (DataIntegrityViolationException e) {
      log.info("관리자 계정이 이미 존재합니다: {}", adminProperties.email());
      return;
    }

    profileRepository.save(Profile.create(savedAdmin));
    log.info("관리자 계정이 생성되었습니다: {}", adminProperties.email());
  }
}
