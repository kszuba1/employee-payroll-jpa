package com.github.kszuba1.employeepayrolljpa.dto

import com.github.kszuba1.employeepayrolljpa.entity.User

data class UserDto(
    val id: Long?,
    val firstName: String,
    val lastName: String,
    val userName: String,
    val description: String,
    val departmentNames: List<String>,
    val salaries: List<SalarySummaryDto>,
)

data class UserSummaryDto(
    val id: Long?,
    val firstName: String,
    val lastName: String,
)

fun User.toDto(): UserDto = UserDto(
    id = id,
    firstName = firstName,
    lastName = lastName,
    userName = userName,
    description = description,
    departmentNames = departments.map { it.departmentName }.sorted(),
    salaries = salaries.map { it.toSummary() },
)

fun User.toSummary(): UserSummaryDto = UserSummaryDto(
    id = id,
    firstName = firstName,
    lastName = lastName,
)
