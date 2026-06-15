package com.github.kszuba1.employeepayrolljpa.controller

import com.github.kszuba1.employeepayrolljpa.controller.docs.AddUserToDepartmentOperation
import com.github.kszuba1.employeepayrolljpa.controller.docs.CreateDepartmentOperation
import com.github.kszuba1.employeepayrolljpa.controller.docs.DepartmentAnnualTotalOperation
import com.github.kszuba1.employeepayrolljpa.controller.docs.ListDepartmentsOperation
import com.github.kszuba1.employeepayrolljpa.controller.docs.RemoveUserFromDepartmentOperation
import com.github.kszuba1.employeepayrolljpa.dto.CreateDepartmentRequest
import com.github.kszuba1.employeepayrolljpa.dto.DepartmentDto
import com.github.kszuba1.employeepayrolljpa.dto.SumResponse
import com.github.kszuba1.employeepayrolljpa.service.DepartmentService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Departments")
class DepartmentController(private val departmentService: DepartmentService) {

    @GetMapping
    @ListDepartmentsOperation
    fun findAll(
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) direction: Sort.Direction?,
    ): List<DepartmentDto> = departmentService.findAll(sortBy, direction)

    @GetMapping("/sortable-columns")
    fun sortableColumns(): List<String> = departmentService.sortableColumns()

    @PostMapping
    @CreateDepartmentOperation
    fun create(@RequestBody request: CreateDepartmentRequest): ResponseEntity<DepartmentDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request))

    @PutMapping("/{departmentId}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AddUserToDepartmentOperation
    fun addUser(@PathVariable departmentId: Long, @PathVariable userId: Long) =
        departmentService.addUser(departmentId, userId)

    @DeleteMapping("/{departmentId}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RemoveUserFromDepartmentOperation
    fun removeUser(@PathVariable departmentId: Long, @PathVariable userId: Long) =
        departmentService.removeUser(departmentId, userId)

    @GetMapping("/annual-total")
    @DepartmentAnnualTotalOperation
    fun annualTotal(
        @RequestParam year: Int,
        @RequestParam departmentName: String,
    ): SumResponse =
        SumResponse(departmentService.sumAnnualSalaries(year, departmentName))
}
