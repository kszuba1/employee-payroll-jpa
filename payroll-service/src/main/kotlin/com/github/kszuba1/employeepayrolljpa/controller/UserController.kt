package com.github.kszuba1.employeepayrolljpa.controller

import com.github.kszuba1.employeepayrolljpa.controller.docs.CreateUserOperation
import com.github.kszuba1.employeepayrolljpa.controller.docs.GetUserOperation
import com.github.kszuba1.employeepayrolljpa.controller.docs.ListUsersOperation
import com.github.kszuba1.employeepayrolljpa.controller.docs.SortByParameter
import com.github.kszuba1.employeepayrolljpa.controller.docs.SortDirectionParameter
import com.github.kszuba1.employeepayrolljpa.controller.docs.UserAnnualTotalOperation
import com.github.kszuba1.employeepayrolljpa.controller.docs.YearParameter
import com.github.kszuba1.employeepayrolljpa.dto.CreateUserRequest
import com.github.kszuba1.employeepayrolljpa.dto.SumResponse
import com.github.kszuba1.employeepayrolljpa.dto.UserDto
import com.github.kszuba1.employeepayrolljpa.service.UserService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
class UserController(private val userService: UserService) {

    @GetMapping
    @ListUsersOperation
    fun findAll(
        @RequestParam(required = false) @SortByParameter sortBy: String?,
        @RequestParam(required = false) @SortDirectionParameter direction: Sort.Direction?,
    ): List<UserDto> = userService.findAll(sortBy, direction)

    @GetMapping("/sortable-columns")
    fun sortableColumns(): List<String> = userService.sortableColumns()

    @GetMapping("/{id}")
    @GetUserOperation
    fun findById(@PathVariable id: Long): ResponseEntity<UserDto> =
        userService.findById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    @CreateUserOperation
    fun create(@RequestBody request: CreateUserRequest): ResponseEntity<UserDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request))

    @GetMapping("/annual-total")
    @UserAnnualTotalOperation
    fun annualTotal(
        @RequestParam @YearParameter year: Int,
        @RequestParam @Parameter(description = "Person's first name.", example = "Anna") firstName: String,
        @RequestParam @Parameter(description = "Person's last name.", example = "Kowalska") lastName: String,
    ): SumResponse =
        SumResponse(userService.sumAnnualSalaries(year, firstName, lastName))
}
