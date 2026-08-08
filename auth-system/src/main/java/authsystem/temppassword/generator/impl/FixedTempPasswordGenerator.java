package authsystem.temppassword.generator.impl;

import authsystem.temppassword.generator.TempPasswordGenerator;

public class FixedTempPasswordGenerator implements TempPasswordGenerator {

  @Override
  public String generate() {
    return "temporary1!!";
  }
}
