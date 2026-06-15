package com.github.kszuba1.employeepayrolljpa.generator

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class SalaryGeneratorTest {

    private val spec = GenerationSpec(
        monthsPerUser = 3,
        minSalary = BigDecimal("3000.00"),
        maxSalary = BigDecimal("9000.00"),
        maxBonus = BigDecimal("1500.00"),
    )

    @Test
    fun `salaryDate walks one month back per paycheck, continuing across users`() {
        val today = LocalDate.of(2026, 6, 13)
        assertEquals(today, salaryDate(today, userIndex = 0, monthIndex = 0, monthsPerUser = 3))
        assertEquals(today.minusMonths(2), salaryDate(today, userIndex = 0, monthIndex = 2, monthsPerUser = 3))
        // next user is strictly older than the previous user's last paycheck
        assertEquals(today.minusMonths(3), salaryDate(today, userIndex = 1, monthIndex = 0, monthsPerUser = 3))
        assertEquals(today.minusMonths(6), salaryDate(today, userIndex = 2, monthIndex = 0, monthsPerUser = 3))
    }

    @Test
    fun `buildSalaryMessages yields monthsPerUser messages per user with in-range values`() {
        val today = LocalDate.of(2026, 6, 13)
        val userIds = listOf(1L, 2L, 3L, 4L)

        val messages = buildSalaryMessages(userIds, spec, today, Random(42))

        // count + grouping: one block of monthsPerUser per user, in order
        assertEquals(userIds.size * spec.monthsPerUser, messages.size)
        assertEquals(userIds.flatMap { id -> List(spec.monthsPerUser) { id } }, messages.map { it.userId })

        // the whole run walks strictly backwards in time (older for each successive paycheck)
        val dates = messages.map { it.dateOfSalary }
        assertEquals(dates.sortedDescending(), dates)
        assertEquals(dates.distinct(), dates)

        // pseudo-random amounts stay within bounds
        messages.forEach { m ->
            assertTrue(m.salary >= spec.minSalary && m.salary <= spec.maxSalary, "salary ${m.salary} out of range")
            m.bonus?.let { b -> assertTrue(b >= BigDecimal.ZERO && b <= spec.maxBonus, "bonus $b out of range") }
        }
    }

    @Test
    fun `validate rejects bad specs`() {
        assertFails { GenerationSpec(0, BigDecimal("1"), BigDecimal("2"), BigDecimal.ZERO).validate() }
        assertFails { GenerationSpec(1, BigDecimal("5"), BigDecimal("2"), BigDecimal.ZERO).validate() }
        assertFails { GenerationSpec(1, BigDecimal("1"), BigDecimal("2"), BigDecimal("-1")).validate() }
    }
}
