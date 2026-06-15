package com.github.kszuba1.employeepayrolljpa.service

import com.github.kszuba1.employeepayrolljpa.dto.CreateUserRequest
import com.github.kszuba1.employeepayrolljpa.dto.UserDto
import com.github.kszuba1.employeepayrolljpa.dto.toDto
import com.github.kszuba1.employeepayrolljpa.entity.User
import com.github.kszuba1.employeepayrolljpa.repository.DepartmentRepository
import com.github.kszuba1.employeepayrolljpa.repository.UserRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UserService(
    private val userRepository: UserRepository,
    private val departmentRepository: DepartmentRepository,
) {

    fun findAll(sortBy: String?, direction: Sort.Direction?): List<UserDto> =
        userRepository.findAll(resolveSort(sortBy, direction, SORTABLE_COLUMNS)).map { it.toDto() }

    fun sortableColumns(): List<String> = SORTABLE_COLUMNS.sorted()

    fun findById(id: Long): UserDto? =
        userRepository.findById(id).map { it.toDto() }.orElse(null)

    fun sumAnnualSalaries(year: Int, firstName: String, lastName: String): BigDecimal =
        userRepository.sumAnnualSalariesByYearAndFullName(year, firstName, lastName)

    @Transactional
    fun create(request: CreateUserRequest): UserDto {
        val user = User(
            firstName = request.firstName,
            lastName = request.lastName,
            userName = request.userName,
            password = request.password,
            description = request.description,
        )
        request.departmentIds.forEach { id ->
            val department = departmentRepository.findById(id).orElseThrow {
                IllegalArgumentException("Department $id not found")
            }
            user.addDepartment(department)
        }
        return userRepository.save(user).toDto()
    }

    private companion object {
        val SORTABLE_COLUMNS = setOf("id", "firstName", "lastName", "userName", "description")
    }
}
