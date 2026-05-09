package com.github.kszuba1.employeepayrolljpa.dto

import com.github.kszuba1.employeepayrolljpa.entity.Department

data class DepartmentDto(
    val id: Long?,
    val address: String,
    val departmentName: String,
    val mail: String,
    val phone: String,
    val description: String?,
    val userCount: Int,
)

data class DepartmentSummaryDto(
    val id: Long?,
    val departmentName: String,
)

fun Department.toDto(): DepartmentDto = DepartmentDto(
    id = id,
    address = address,
    departmentName = departmentName,
    mail = mail,
    phone = phone,
    description = description,
    userCount = users.size,
)

fun Department.toSummary(): DepartmentSummaryDto = DepartmentSummaryDto(
    id = id,
    departmentName = departmentName,
)
