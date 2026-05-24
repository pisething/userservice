package com.pisethjavaschool.userservice.repository.model;

import java.util.List;

/**
 * Lightweight pagination result used at repository/query layer.
 *
 * @param items items for the current page
 * @param total total number of matched records
 * @param page  zero-based page index
 * @param size  page size
 */
public record PageResult<T>(
        List<T> items,
        long total,
        int page,
        int size
) {
}
