package com.github.kszuba1.employeepayrolljpa.repository

import com.github.kszuba1.employeepayrolljpa.entity.Salary
import com.github.kszuba1.employeepayrolljpa.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DataJpaTest
class SalaryRepositoryTest @Autowired constructor(
    private val salaryRepository: SalaryRepository,
    private val entityManager: TestEntityManager,
) {

    @Test
    fun `creates salary and assigns id`() {
        // given
        val user = persistUser()
        val salary = Salary(
            salary = BigDecimal("5000.00"),
            dateOfSalary = LocalDate.of(2025, 1, 31),
            user = user,
        )

        // when
        val saved = salaryRepository.save(salary)

        // then
        val savedId = assertNotNull(saved.id)
        val reloaded = entityManager.find(Salary::class.java, savedId)!!
        assertEquals(0, BigDecimal("5000.00").compareTo(reloaded.salary))
        assertEquals(LocalDate.of(2025, 1, 31), reloaded.dateOfSalary)
        assertEquals(user.id, reloaded.user?.id)
    }

    @Test
    fun `updates salary amount and bonus`() {
        // given
        val user = persistUser()
        val saved = salaryRepository.save(
            Salary(
                salary = BigDecimal("5000.00"),
                dateOfSalary = LocalDate.of(2025, 1, 31),
                user = user,
            )
        )
        entityManager.flush()
        entityManager.clear()

        // when
        val toUpdate = salaryRepository.findById(saved.id!!).get()
        toUpdate.salary = BigDecimal("5500.00")
        toUpdate.bonus = BigDecimal("1000.00")
        salaryRepository.save(toUpdate)
        entityManager.flush()
        entityManager.clear()

        // then
        val reloaded = salaryRepository.findById(saved.id!!).get()
        assertEquals(0, BigDecimal("5500.00").compareTo(reloaded.salary))
        assertEquals(0, BigDecimal("1000.00").compareTo(reloaded.bonus!!))
    }

    @Test
    fun `calculates monthly tax for company`() {
        // given
        val anna = persistUser("Anna", "Kowalska")
        val bob = persistUser("Bob", "Smith")

        entityManager.persist(Salary(salary = BigDecimal("5000.00"), dateOfSalary = LocalDate.of(2025, 1, 31), user = anna))
        entityManager.persist(Salary(salary = BigDecimal("4000.00"), dateOfSalary = LocalDate.of(2025, 1, 31), user = bob))
        entityManager.persist(Salary(salary = BigDecimal("9999.00"), dateOfSalary = LocalDate.of(2025, 2, 28), user = anna))
        entityManager.persist(Salary(salary = BigDecimal("9999.00"), dateOfSalary = LocalDate.of(2024, 1, 31), user = anna))
        entityManager.flush()

        // when
        val tax = salaryRepository.calculateMonthlyTax(
            year = 2025,
            month = 1,
            taxPercentage = BigDecimal("19"),
        )

        // then
        assertEquals(0, BigDecimal("1710.00").compareTo(tax))
    }

    @Test
    fun `returns zero tax when no salaries match the period`() {
        // when
        val tax = salaryRepository.calculateMonthlyTax(
            year = 2099,
            month = 1,
            taxPercentage = BigDecimal("19"),
        )

        // then
        assertEquals(0, BigDecimal.ZERO.compareTo(tax))
    }

    private fun persistUser(firstName: String = "Anna", lastName: String = "Kowalska"): User {
        val user = User(
            firstName = firstName,
            lastName = lastName,
            userName = firstName.lowercase(),
            password = "secret",
            description = "n/a",
        )
        return entityManager.persist(user)
    }
}
