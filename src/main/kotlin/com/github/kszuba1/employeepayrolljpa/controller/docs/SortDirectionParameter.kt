package com.github.kszuba1.employeepayrolljpa.controller.docs

import io.swagger.v3.oas.annotations.Parameter

@Parameter(
    description = "Sort direction (case-insensitive). Defaults to ascending when omitted.",
    example = "asc",
)
annotation class SortDirectionParameter
