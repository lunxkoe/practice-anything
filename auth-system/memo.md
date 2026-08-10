# Security / Auth / User 영역 작업 정리

## 한 줄 요약
Token과 UserSession의 책임을 분리하고, 정책(단일/다중 기기, 만료 방식)을 설정만으로 바꿀 수 있게 재설계했습니다.

---

## 1. 구조 변경

| 영역 | 기존 | 변경 | 이유 |
|---|---|---|---|
| **Token** | 토큰 만료 시간이 Redis `UserSession`의 TTL에 종속 | 발급 시각 기준으로 Token/Session이 각자 독립적인 만료 시간을 가짐 | 토큰 유효성과 "로그인 중인가"를 분리 — 로그인 여부의 진실은 항상 `UserSessionRegistry` |
| **UserSession** | 단일 기기 로그인만 지원하도록 코드에 고정 | 만료 정책(absolute/sliding), 동시접속 정책(single/multi/max-device)을 인터페이스로 분리, `application.yaml` 설정만으로 전환 | 요구사항 변경 시 코드 수정 없이 정책만 교체 가능하게 |
| **UserSession 동시성** | 세션 교체·회전 로직이 조회 후 별도 쓰기(레이스 가능) | Redis Lua 스크립트로 원자화 (`compare-and-rotate`, `replace-all`) | 동시 요청 시 재사용 토큰 오탐/세션 꼬임 방지 |
| **Security 패키지** | `global.security.*`에 인증 관련 코드가 흩어져 있음 | 최상위 `security.*` 패키지로 승격 | 향후 멀티모듈/서비스 분리를 염두에 둔 경계 설정 |
| **Admin/User 컨트롤러** | 관리자 전용 기능(목록조회/역할변경/잠금)이 `UserController`에 섞여 있음 | `AdminController`+`AdminService`로 분리 | 일반 사용자 API와 관리자 API의 책임 분리 |
| **Auth ↔ Mapper** | `AuthController`가 `AuthMapper`를 직접 호출해 응답 DTO 조립 | `SignInDto`/`RefreshDto`가 `JwtDto`를 내부에 포함하도록 변경, 컨트롤러는 `result.jwtDto()`만 반환 | 컨트롤러 계층에서 불필요한 변환 책임 제거 |

---

## 2. 트러블슈팅 하이라이트

### 임시 비밀번호 발급 기능 초기 버그 3종
- 비밀번호 검증 로직(`matches()`)이 실제로는 호출되지 않던 버그
- 메일 템플릿 경로 오류로 발송 실패
- record의 기본 `toString()`으로 인해 **원문 비밀번호가 로그에 그대로 노출**되던 문제
→ 전부 발견 후 수정, 관련 단위 테스트 전체 작성

### Refresh가 병렬적으로 호출될 시 마지막 요청으로 덮어써버리는 문제
- Refresh 탈취 로직의 원자성이 보장되지 않아 악의적인 공격자와 선의의 사용자가 모두 유효 검증을 통과할 수 있는 문제 발견 - 테스트 코드로 실험
- 원자성 보장으로 위해 Lua 스크립트를 작성하여 원자성을 보장할 수 있게 함

### CI에서만 실패하는 `AdminControllerTest` — 단계별 진단
1. **1차 추정(오판)**: 설정 프로퍼티 prefix 불일치로 의심 → 확인해보니 이미 해결된 사항이었음
2. **근본 원인**: `AdminController`가 인터페이스(`AdminApi`)를 구현하는데, 클래스 레벨 `@PreAuthorize`가 있으면 Spring Security가 기본적으로 **JDK 동적 프록시**를 생성 → 이 프록시는 인터페이스 메서드만 노출해서, 구현 클래스에 붙은 `@GetMapping`/`@PatchMapping`을 라우팅 엔진이 못 찾고 **전체 요청이 404**
   → `@EnableMethodSecurity(proxyTargetClass = true)`로 CGLIB 프록시를 강제해 해결 (직접 여러 번 실행해서 검증)
3. **별개 문제**: `application-test.yaml`의 토큰 만료 시간에 단위(`m`/`d`)가 빠져서 Spring이 밀리초로 해석 → 최소 시간 검증(`@DurationMin`)에 걸려 전체 애플리케이션 컨텍스트 부팅 실패

---

## 3. 남은 과제
- 다중 기기 로그인 정책에서도 기존 SSE 연결을 강제 종료하는 로직이 그대로 남아있음 — 다중 기기 정책에 맞는 종료 기준 팀 논의 필요
- `docs/api-docs.json`/`api-spec.md`와 실제 API 경로(Admin API 이동 등) 재확인 필요 — FE 영향 있음
- 선의의 경쟁에도 세션 탈취 감지 시나리오가 발생