package rider.nbc.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDto<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PageResponseDto<T> of(List<T> content, long totalElements, int page, int size) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasNext = page < totalPages - 1;
        boolean hasPrevious = page > 0;

        return PageResponseDto.<T>builder()
                .content(content)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .page(page)
                .size(size)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }
}