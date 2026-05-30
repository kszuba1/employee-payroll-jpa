package com.github.kszuba1.employeepayrolljpa.controller.docs

import io.swagger.v3.oas.annotations.Parameter

@Parameter(
    description = "Calendar year to aggregate over.",
    example = "2025",
)
annotation class YearParameter
