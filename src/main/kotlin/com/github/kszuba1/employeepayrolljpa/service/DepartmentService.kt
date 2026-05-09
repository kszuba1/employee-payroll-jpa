package com.github.kszuba1.employeepayrolljpa.service

import com.github.kszuba1.employeepayrolljpa.dto.DepartmentDto
import com.github.kszuba1.employeepayrolljpa.dto.toDto
import com.github.kszuba1.employeepayrolljpa.repository.DepartmentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class DepartmentService(private val departmentRepository: DepartmentRepository) {

    fun findAll(): List<DepartmentDto> = departmentRepository.findAll().map { it.toDto() }

    fun sumAnnualSalaries(year: Int, departmentName: String): BigDecimal =
        departmentRepository.sumAnnualSalariesByYearAndDepartmentName(year, departmentName)
}
