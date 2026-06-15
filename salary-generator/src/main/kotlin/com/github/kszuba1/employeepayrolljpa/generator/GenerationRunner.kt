package com.github.kszuba1.employeepayrolljpa.generator

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class GenerationRunner(
    private val salaryGenerator: SalaryGenerator,
    @param:Value("\${app.generation.months-per-user:3}") private val monthsPerUser: Int,
    @param:Value("\${app.generation.min-salary:3000.00}") private val minSalary: BigDecimal,
    @param:Value("\${app.generation.max-salary:9000.00}") private val maxSalary: BigDecimal,
    @param:Value("\${app.generation.max-bonus:1500.00}") private val maxBonus: BigDecimal,
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String) {
        val spec = GenerationSpec(monthsPerUser, minSalary, maxSalary, maxBonus)
        log.info("Starting salary generation: {}", spec)
        val summary = salaryGenerator.generate(spec)
        log.info(
            "Done — published {} salary message(s) for {} user(s) to '{}'.",
            summary.messagesPublished, summary.users, summary.queue,
        )
    }
}
