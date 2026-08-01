package org.example.tasks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;          // pagina curenta (0-based)
    private int size;          // cate elemente pe pagina
    private long totalElements;
    private int totalPages;
}
