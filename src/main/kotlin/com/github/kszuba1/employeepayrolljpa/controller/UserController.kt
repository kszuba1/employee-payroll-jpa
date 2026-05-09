package com.github.kszuba1.employeepayrolljpa.controller

import com.github.kszuba1.employeepayrolljpa.dto.SumResponse
import com.github.kszuba1.employeepayrolljpa.dto.UserDto
import com.github.kszuba1.employeepayrolljpa.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun findAll(): List<UserDto> = userService.findAll()

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<UserDto> =
        userService.findById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @GetMapping("/annual-total")
    fun annualTotal(
        @RequestParam year: Int,
        @RequestParam firstName: String,
        @RequestParam lastName: String,
    ): SumResponse =
        SumResponse(userService.sumAnnualSalaries(year, firstName, lastName))
}
