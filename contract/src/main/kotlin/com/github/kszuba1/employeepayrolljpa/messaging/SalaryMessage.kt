package com.github.kszuba1.employeepayrolljpa.messaging

import java.math.BigDecimal
import java.time.LocalDate

data class SalaryMessage(
    val userId: Long,
    val dateOfSalary: LocalDate,
    val salary: BigDecimal,
    val bonus: BigDecimal? = null,
)
