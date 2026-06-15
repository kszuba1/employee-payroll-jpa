package com.github.kszuba1.employeepayrolljpa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmployeePayrollJpaApplication

fun main(args: Array<String>) {
	runApplication<EmployeePayrollJpaApplication>(*args)
}
