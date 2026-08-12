package lunxkoe.practice.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingFilter extends OncePerRequestFilter {

  private static final String REQUEST_ID = "requestId";
  private static final String CLIENT_IP = "clientIp";
  private static final String REQUEST_URL = "requestUrl";
  private static final String REQUEST_METHOD = "requestMethod";
  private static final String HEADER_REQUEST_ID = "Trace-Request-Id";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String requestId = UUID.randomUUID().toString();

      MDC.put(REQUEST_ID, requestId);
      response.addHeader(HEADER_REQUEST_ID, requestId);

      MDC.put(CLIENT_IP, extractClientIp(request));
      MDC.put(REQUEST_URL, request.getRequestURI());
      MDC.put(REQUEST_METHOD, request.getMethod());

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

  // TODO: 배포 환경에서는 추후 고려 (현재 로컬 개발 중)
  private String extractClientIp(HttpServletRequest request) {
    return request.getRemoteAddr();
  }
}
