package com.github.kszuba1.employeepayrolljpa.controller

import com.github.kszuba1.employeepayrolljpa.dto.SalaryDto
import com.github.kszuba1.employeepayrolljpa.dto.SumResponse
import com.github.kszuba1.employeepayrolljpa.service.SalaryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/salaries")
class SalaryController(private val salaryService: SalaryService) {

    @GetMapping
    fun list(): List<SalaryDto> = salaryService.findAll()

    @GetMapping("/monthly-tax")
    fun monthlyTax(
        @RequestParam year: Int,
        @RequestParam month: Int,
        @RequestParam taxPercentage: BigDecimal,
    ): SumResponse =
        SumResponse(salaryService.calculateMonthlyTax(year, month, taxPercentage))
}
