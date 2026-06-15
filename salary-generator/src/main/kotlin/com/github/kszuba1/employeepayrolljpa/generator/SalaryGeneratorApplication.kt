package com.github.kszuba1.employeepayrolljpa.generator

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.system.exitProcess

@SpringBootApplication
class SalaryGeneratorApplication

fun main(args: Array<String>) {
    val context = runApplication<SalaryGeneratorApplication>(*args)
    exitProcess(SpringApplication.exit(context))
}
