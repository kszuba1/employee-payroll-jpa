package com.github.kszuba1.employeepayrolljpa.dto

data class CreateUserRequest(
    val firstName: String,
    val lastName: String,
    val userName: String,
    val password: String,
    val description: String,
    val departmentIds: List<Long> = emptyList(),
)

data class CreateDepartmentRequest(
    val address: String,
    val departmentName: String,
    val mail: String,
    val phone: String,
    val description: String? = null,
)
