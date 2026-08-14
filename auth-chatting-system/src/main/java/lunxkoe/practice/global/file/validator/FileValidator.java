package lunxkoe.practice.global.file.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lunxkoe.practice.global.file.exception.EmptyFileException;
import lunxkoe.practice.global.file.exception.FileStorageException;
import lunxkoe.practice.global.file.exception.FileTooLargeException;
import lunxkoe.practice.global.file.exception.InvalidFileTypeException;
import lunxkoe.practice.global.file.properties.FileProperties;
import lunxkoe.practice.global.file.util.FileExtensionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileValidator {

  private static final int SIGNATURE_PEEK_SIZE = 12;

  private static final Map<String, Predicate<byte[]>> SIGNATURE_VALIDATORS = Map.of(
      "jpg", FileValidator::isJpeg,
      "jpeg", FileValidator::isJpeg,
      "png", FileValidator::isPng,
      "webp", FileValidator::isWebp
  );

  private final FileProperties fileProperties;

  public void validate(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw EmptyFileException.withNone();
    }

    if (file.getSize() > fileProperties.maxSizeBytes()) {
      throw FileTooLargeException.withSize(file.getSize(), fileProperties.maxSizeBytes());
    }

    String extension = FileExtensionUtils.extract(file.getOriginalFilename())
        .orElseThrow(() -> InvalidFileTypeException.withExtension("unknown"));

    if (!fileProperties.allowedExtensions().contains(extension)) {
      throw InvalidFileTypeException.withExtension(extension);
    }

    validateSignature(file, extension);
  }

  private void validateSignature(MultipartFile file, String extension) {
    Predicate<byte[]> matchesSignature = SIGNATURE_VALIDATORS.get(extension);
    if (matchesSignature == null) {
      return;
    }

    byte[] header = readHeader(file);
    if (!matchesSignature.test(header)) {
      throw InvalidFileTypeException.withExtension(extension);
    }
  }

  private byte[] readHeader(MultipartFile file) {
    byte[] header = new byte[SIGNATURE_PEEK_SIZE];
    try (InputStream inputStream = file.getInputStream()) {
      int read = inputStream.readNBytes(header, 0, header.length);
      return Arrays.copyOf(header, read);
    } catch (IOException e) {
      throw FileStorageException.withCause(e);
    }
  }

  private static boolean isJpeg(byte[] header) {
    return startsWith(header, 0xFF, 0xD8, 0xFF);
  }

  private static boolean isPng(byte[] header) {
    return startsWith(header, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);
  }

  private static boolean isWebp(byte[] header) {
    return startsWith(header, 'R', 'I', 'F', 'F')
        && header.length >= 12
        && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
  }

  private static boolean startsWith(byte[] header, int... expectedBytes) {
    if (header.length < expectedBytes.length) {
      return false;
    }
    for (int i = 0; i < expectedBytes.length; i++) {
      if (header[i] != (byte) expectedBytes[i]) {
        return false;
      }
    }
    return true;
  }
}
