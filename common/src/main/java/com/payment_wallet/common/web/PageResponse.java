package com.payment_wallet.common.web;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Stable, serialization-friendly pagination envelope returned by paginated endpoints
 * (avoids leaking the full Spring Data {@code Page} JSON shape, which is unstable across versions).
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
