package com.github.kszuba1.employeepayrolljpa.service

import com.github.kszuba1.employeepayrolljpa.dto.CreateDepartmentRequest
import com.github.kszuba1.employeepayrolljpa.dto.DepartmentDto
import com.github.kszuba1.employeepayrolljpa.dto.toDto
import com.github.kszuba1.employeepayrolljpa.entity.Department
import com.github.kszuba1.employeepayrolljpa.exception.ResourceNotFoundException
import com.github.kszuba1.employeepayrolljpa.repository.DepartmentRepository
import com.github.kszuba1.employeepayrolljpa.repository.UserRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository,
    private val userRepository: UserRepository,
) {

    fun findAll(sortBy: String?, direction: Sort.Direction?): List<DepartmentDto> =
        departmentRepository.findAll(resolveSort(sortBy, direction, SORTABLE_COLUMNS)).map { it.toDto() }

    fun sortableColumns(): List<String> = SORTABLE_COLUMNS.sorted()

    fun sumAnnualSalaries(year: Int, departmentName: String): BigDecimal =
        departmentRepository.sumAnnualSalariesByYearAndDepartmentName(year, departmentName)

    @Transactional
    fun create(request: CreateDepartmentRequest): DepartmentDto {
        val department = Department(
            address = request.address,
            departmentName = request.departmentName,
            mail = request.mail,
            phone = request.phone,
            description = request.description,
        )
        return departmentRepository.save(department).toDto()
    }

    @Transactional
    fun addUser(departmentId: Long, userId: Long) {
        val department = findDepartment(departmentId)
        findUser(userId).addDepartment(department)
    }

    @Transactional
    fun removeUser(departmentId: Long, userId: Long) {
        val department = findDepartment(departmentId)
        findUser(userId).removeDepartment(department)
    }

    private fun findDepartment(departmentId: Long): Department =
        departmentRepository.findById(departmentId).orElseThrow {
            ResourceNotFoundException("Department $departmentId not found")
        }

    private fun findUser(userId: Long) =
        userRepository.findById(userId).orElseThrow {
            ResourceNotFoundException("User $userId not found")
        }

    private companion object {
        val SORTABLE_COLUMNS = setOf("id", "address", "departmentName", "mail", "phone", "description")
    }
}
