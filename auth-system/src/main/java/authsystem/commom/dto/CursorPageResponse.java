package authsystem.commom.dto;

import java.util.List;
import org.hibernate.validator.constraints.UUID;

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
