package com.github.kszuba1.employeepayrolljpa.repository

import com.github.kszuba1.employeepayrolljpa.entity.Department
import com.github.kszuba1.employeepayrolljpa.entity.Salary
import com.github.kszuba1.employeepayrolljpa.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DataJpaTest
class DepartmentRepositoryTest @Autowired constructor(
    private val departmentRepository: DepartmentRepository,
    private val entityManager: TestEntityManager,
) {

    @Test
    fun `creates department and assigns id`() {
        // given
        val department = newDepartment("Engineering")

        // when
        val saved = departmentRepository.save(department)

        // then
        val savedId = assertNotNull(saved.id)
        val reloaded = entityManager.find(Department::class.java, savedId)!!
        assertEquals("Engineering", reloaded.departmentName)
        assertEquals("engineering@company.com", reloaded.mail)
    }

    @Test
    fun `updates department fields`() {
        // given
        val saved = departmentRepository.save(newDepartment("Engineering"))
        entityManager.flush()
        entityManager.clear()

        // when
        val toUpdate = departmentRepository.findById(saved.id!!).get()
        toUpdate.departmentName = "Engineering & Research"
        toUpdate.phone = "999-999"
        departmentRepository.save(toUpdate)
        entityManager.flush()
        entityManager.clear()

        // then
        val reloaded = departmentRepository.findById(saved.id!!).get()
        assertEquals("Engineering & Research", reloaded.departmentName)
        assertEquals("999-999", reloaded.phone)
    }

    @Test
    fun `sums annual salaries by year and department name`() {
        // given
        val engineering = entityManager.persist(newDepartment("Engineering"))
        val sales = entityManager.persist(newDepartment("Sales"))

        val anna = newUser("Anna", "Kowalska")
        anna.addDepartment(engineering)
        anna.addSalary(Salary(salary = BigDecimal("5000.00"), dateOfSalary = LocalDate.of(2025, 1, 31)))
        anna.addSalary(Salary(salary = BigDecimal("5500.00"), dateOfSalary = LocalDate.of(2025, 6, 30)))
        anna.addSalary(Salary(salary = BigDecimal("9999.00"), dateOfSalary = LocalDate.of(2024, 1, 31)))
        entityManager.persist(anna)

        val bob = newUser("Bob", "Smith")
        bob.addDepartment(sales)
        bob.addSalary(Salary(salary = BigDecimal("4800.00"), dateOfSalary = LocalDate.of(2025, 3, 31)))
        entityManager.persist(bob)

        entityManager.flush()

        // when
        val sum = departmentRepository.sumAnnualSalariesByYearAndDepartmentName(2025, "Engineering")

        // then
        assertEquals(0, BigDecimal("10500.00").compareTo(sum))
    }

    @Test
    fun `returns zero for unknown department`() {
        // when
        val sum = departmentRepository.sumAnnualSalariesByYearAndDepartmentName(2025, "NoSuchDept")

        // then
        assertEquals(0, BigDecimal.ZERO.compareTo(sum))
    }

    private fun newDepartment(name: String): Department = Department(
        address = "Main St",
        departmentName = name,
        mail = "${name.lowercase().replace(" ", "")}@company.com",
        phone = "111-111",
    )

    private fun newUser(firstName: String, lastName: String): User = User(
        firstName = firstName,
        lastName = lastName,
        userName = firstName.lowercase(),
        password = "secret",
        description = "n/a",
    )
}
