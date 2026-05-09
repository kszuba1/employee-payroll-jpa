package com.github.kszuba1.employeepayrolljpa.controller

import com.github.kszuba1.employeepayrolljpa.dto.DepartmentDto
import com.github.kszuba1.employeepayrolljpa.dto.SumResponse
import com.github.kszuba1.employeepayrolljpa.service.DepartmentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/departments")
class DepartmentController(private val departmentService: DepartmentService) {

    @GetMapping
    fun list(): List<DepartmentDto> = departmentService.findAll()

    @GetMapping("/annual-total")
    fun annualTotal(
        @RequestParam year: Int,
        @RequestParam departmentName: String,
    ): SumResponse =
        SumResponse(departmentService.sumAnnualSalaries(year, departmentName))
}
