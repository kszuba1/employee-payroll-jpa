package com.github.kszuba1.employeepayrolljpa.controller.docs

import io.swagger.v3.oas.annotations.Parameter

@Parameter(
    description = "Column to sort by. Must be one of the values returned by the matching " +
        "`/sortable-columns` endpoint; an unknown column yields 400.",
    example = "lastName",
)
annotation class SortByParameter
