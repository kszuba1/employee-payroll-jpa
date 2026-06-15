package com.github.kszuba1.employeepayrolljpa.dto

import com.github.kszuba1.employeepayrolljpa.entity.Salary
import java.math.BigDecimal
import java.time.LocalDate

data class SalaryDto(
    val id: Long?,
    val dateOfSalary: LocalDate,
    val salary: BigDecimal,
    val bonus: BigDecimal?,
    val user: UserSummaryDto,
    val departmentNames: List<String>,
)

data class SalarySummaryDto(
    val id: Long?,
    val dateOfSalary: LocalDate,
    val salary: BigDecimal,
    val bonus: BigDecimal?,
)

fun Salary.toDto(): SalaryDto = SalaryDto(
    id = id,
    dateOfSalary = dateOfSalary,
    salary = salary,
    bonus = bonus,
    user = user!!.toSummary(),
    departmentNames = user!!.departments.map { it.departmentName }.sorted(),
)

fun Salary.toSummary(): SalarySummaryDto = SalarySummaryDto(
    id = id,
    dateOfSalary = dateOfSalary,
    salary = salary,
    bonus = bonus,
)
