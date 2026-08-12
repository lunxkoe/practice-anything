package lunxkoe.practice.domain.user.repository.querydsl.impl;

import static lunxkoe.practice.domain.user.entity.QUser.user;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lunxkoe.practice.domain.user.dto.request.UserListParams;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.domain.user.entity.enums.Role;
import lunxkoe.practice.domain.user.repository.querydsl.UserCustomRepository;
import lunxkoe.practice.global.dto.CursorPageResponse;
import lunxkoe.practice.global.dto.enums.SortDirection;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

  private static final String SORT_BY_EMAIL = "email";
  private static final String SORT_BY_CREATED_AT = "createdAt";

  private final JPAQueryFactory queryFactory;

  @Override
  public CursorPageResponse<UserDto> searchUserList(UserListParams condition) {
    List<UserDto> content = queryFactory
        .select(
            Projections.constructor(UserDto.class,
                user.id,
                user.createdAt,
                user.email,
                user.name,
                user.role,
                user.locked
            )
        )
        .from(user)
        .where(
            emailLikeCondition(condition.emailLike()),
            roleEqualCondition(condition.roleEqual()),
            lockedCondition(condition.locked()),
            cursorCondition(condition)
        )
        .orderBy(orderSpecifiers(condition))
        .limit(condition.limit() + 1)
        .fetch();

    boolean hasNext = content.size() > condition.limit();
    List<UserDto> data = hasNext ? content.subList(0, condition.limit()) : content;

    String nextCursor = null;
    UUID nextIdAfter = null;
    if (hasNext && !data.isEmpty()) {
      UserDto last = data.get(data.size() - 1);
      nextCursor = extractCursor(last, condition.sortBy());
      nextIdAfter = last.id();
    }

    // TODO: 카운트 쿼리 최적화 (성능 테스트 이후)
    long totalCount = Optional.ofNullable(
        queryFactory
            .select(user.count())
            .from(user)
            .where(
                emailLikeCondition(condition.emailLike()),
                roleEqualCondition(condition.roleEqual()),
                lockedCondition(condition.locked())
            )
            .fetchOne()
    ).orElse(0L);

    return new CursorPageResponse<>(
        data,
        nextCursor,
        nextIdAfter,
        hasNext,
        totalCount,
        condition.sortBy(),
        condition.sortDirection()
    );
  }

  private String extractCursor(UserDto last, String sortBy) {
    if (SORT_BY_EMAIL.equals(sortBy)) {
      return last.email();
    } else if (SORT_BY_CREATED_AT.equals(sortBy)) {
      return last.createdAt().toString();
    }
    throw new IllegalStateException("지원하지 않는 sortBy 값입니다: " + sortBy);
  }

  // TODO: 인덱스 못 탈 걸?
  private BooleanExpression emailLikeCondition(String emailLike) {
    return StringUtils.hasText(emailLike) ? user.email.containsIgnoreCase(emailLike) : null;
  }

  private BooleanExpression roleEqualCondition(Role roleEqual) {
    return roleEqual != null ? user.role.eq(roleEqual) : null;
  }

  private BooleanExpression lockedCondition(Boolean locked) {
    return locked != null ? user.locked.eq(locked) : null;
  }

  private BooleanExpression cursorCondition(UserListParams condition) {
    if (condition.cursor() == null) {
      return null; // 첫 페이지 요청
    }

    String sortBy = condition.sortBy();
    boolean ascending = condition.sortDirection() == SortDirection.ASCENDING;

    if (SORT_BY_EMAIL.equals(sortBy)) {
      if (ascending) {
        return user.email.gt(condition.cursor())
            .or(user.email.eq(condition.cursor()).and(user.id.gt(condition.idAfter())));
      } else {
        return user.email.lt(condition.cursor())
            .or(user.email.eq(condition.cursor()).and(user.id.lt(condition.idAfter())));
      }
    } else if (SORT_BY_CREATED_AT.equals(sortBy)) {
      Instant cursorInstant = Instant.parse(condition.cursor());
      if (ascending) {
        return user.createdAt.gt(cursorInstant)
            .or(user.createdAt.eq(cursorInstant).and(user.id.gt(condition.idAfter())));
      } else {
        return user.createdAt.lt(cursorInstant)
            .or(user.createdAt.eq(cursorInstant).and(user.id.lt(condition.idAfter())));
      }
    }
    throw new IllegalStateException("지원하지 않는 sortBy 값입니다: " + sortBy);
  }

  private OrderSpecifier<?>[] orderSpecifiers(UserListParams condition) {
    String sortBy = condition.sortBy();
    boolean ascending = condition.sortDirection() == SortDirection.ASCENDING;

    OrderSpecifier<?> primary;
    if (SORT_BY_EMAIL.equals(sortBy)) {
      primary = ascending ? user.email.asc() : user.email.desc();
    } else if (SORT_BY_CREATED_AT.equals(sortBy)) {
      primary = ascending ? user.createdAt.asc() : user.createdAt.desc();
    } else {
      throw new IllegalStateException("지원하지 않는 sortBy 값입니다: " + sortBy);
    }

    OrderSpecifier<?> tieBreaker = ascending ? user.id.asc() : user.id.desc();
    return new OrderSpecifier<?>[]{primary, tieBreaker};
  }
}
