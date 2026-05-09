package com.github.kszuba1.employeepayrolljpa.service

import com.github.kszuba1.employeepayrolljpa.dto.SalaryDto
import com.github.kszuba1.employeepayrolljpa.dto.toDto
import com.github.kszuba1.employeepayrolljpa.repository.SalaryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class SalaryService(private val salaryRepository: SalaryRepository) {

    fun findAll(): List<SalaryDto> = salaryRepository.findAll().map { it.toDto() }

    fun calculateMonthlyTax(year: Int, month: Int, taxPercentage: BigDecimal): BigDecimal =
        salaryRepository.calculateMonthlyTax(year, month, taxPercentage)
}
