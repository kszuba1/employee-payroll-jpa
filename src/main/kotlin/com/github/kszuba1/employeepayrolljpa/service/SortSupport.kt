package com.github.kszuba1.employeepayrolljpa.service

import org.springframework.data.domain.Sort

internal fun resolveSort(sortBy: String?, direction: Sort.Direction?, allowed: Set<String>): Sort {
    if (sortBy.isNullOrBlank()) return Sort.unsorted()
    require(sortBy in allowed) {
        "Unknown sort column '$sortBy'. Allowed columns: ${allowed.sorted().joinToString()}"
    }
    return Sort.by(direction ?: Sort.Direction.ASC, sortBy)
}
