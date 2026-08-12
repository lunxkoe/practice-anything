package lunxkoe.practice.global.dto;

import java.util.List;
import java.util.UUID;
import lunxkoe.practice.global.dto.enums.SortDirection;

public record CursorPageResponse<T>(
    List<T> data,
    String nextCursor,
    UUID nextIdAfter,
    boolean hasNext,
    long totalCount,
    String sortBy,
    SortDirection sortDirection
) {

}
