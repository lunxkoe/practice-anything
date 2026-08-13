package lunxkoe.practice.security.token.provider;

import java.time.Clock;
import lunxkoe.practice.security.token.properties.TokenProperties;
import lunxkoe.practice.security.token.provider.impl.JjwtTokenProvider;

class JjwtTokenProviderTest extends TokenProviderContractTest {

  @Override
  protected TokenProvider createProvider(TokenProperties properties, Clock clock) {
    return new JjwtTokenProvider(properties, clock);
  }
}
