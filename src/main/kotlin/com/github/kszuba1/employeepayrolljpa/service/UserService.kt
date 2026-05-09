package com.github.kszuba1.employeepayrolljpa.service

import com.github.kszuba1.employeepayrolljpa.dto.UserDto
import com.github.kszuba1.employeepayrolljpa.dto.toDto
import com.github.kszuba1.employeepayrolljpa.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class UserService(private val userRepository: UserRepository) {

    fun findAll(): List<UserDto> = userRepository.findAll().map { it.toDto() }

    fun findById(id: Long): UserDto? = userRepository.findById(id).map { it.toDto() }.orElse(null)

    fun sumAnnualSalaries(year: Int, firstName: String, lastName: String): BigDecimal =
        userRepository.sumAnnualSalariesByYearAndFullName(year, firstName, lastName)
}
