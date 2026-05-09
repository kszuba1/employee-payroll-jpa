package com.github.kszuba1.employeepayrolljpa.dto

import com.github.kszuba1.employeepayrolljpa.entity.Salary
import java.math.BigDecimal
import java.time.LocalDate

data class SalaryDto(
    val id: Long?,
    val dateOfSalary: LocalDate,
    val salary: BigDecimal,
    val bonus: String?,
    val user: UserSummaryDto,
)

data class SalarySummaryDto(
    val id: Long?,
    val dateOfSalary: LocalDate,
    val salary: BigDecimal,
    val bonus: String?,
)

fun Salary.toDto(): SalaryDto = SalaryDto(
    id = id,
    dateOfSalary = dateOfSalary,
    salary = salary,
    bonus = bonus,
    user = user!!.toSummary(),
)

fun Salary.toSummary(): SalarySummaryDto = SalarySummaryDto(
    id = id,
    dateOfSalary = dateOfSalary,
    salary = salary,
    bonus = bonus,
)
