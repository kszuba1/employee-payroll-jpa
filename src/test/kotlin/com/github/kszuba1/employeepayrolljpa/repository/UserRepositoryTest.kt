package com.github.kszuba1.employeepayrolljpa.repository

import com.github.kszuba1.employeepayrolljpa.entity.Department
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
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase
class UserRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val entityManager: TestEntityManager,
) {

    @Test
    fun `creates user and assigns id`() {
        // given
        val user = newUser()

        // when
        val saved = userRepository.save(user)

        // then
        val savedId = assertNotNull(saved.id)
        val reloaded = entityManager.find(User::class.java, savedId)!!
        assertEquals("Anna", reloaded.firstName)
        assertEquals("Kowalska", reloaded.lastName)
    }

    @Test
    fun `updates user fields and persists changes`() {
        // given
        val saved = userRepository.save(newUser())
        entityManager.flush()
        entityManager.clear()

        // when
        val toUpdate = userRepository.findById(saved.id!!).get()
        toUpdate.firstName = "Hanna"
        toUpdate.description = "Updated description"
        userRepository.save(toUpdate)
        entityManager.flush()
        entityManager.clear()

        // then
        val reloaded = userRepository.findById(saved.id!!).get()
        assertEquals("Hanna", reloaded.firstName)
        assertEquals("Updated description", reloaded.description)
    }

    @Test
    fun `cascades persist of salaries when saving user`() {
        // given
        val user = newUser()
        user.addSalary(Salary(salary = BigDecimal("5000.00"), dateOfSalary = LocalDate.of(2025, 1, 31)))
        user.addSalary(Salary(salary = BigDecimal("5500.00"), dateOfSalary = LocalDate.of(2025, 2, 28)))

        // when
        val saved = userRepository.save(user)
        entityManager.flush()
        entityManager.clear()

        // then
        val reloaded = userRepository.findById(saved.id!!).get()
        assertEquals(2, reloaded.salaries.size)
        assertTrue(reloaded.salaries.all { it.id != null })
        assertTrue(reloaded.salaries.all { it.user?.id == reloaded.id })
    }

    @Test
    fun `orphan removal deletes salary detached from user`() {
        // given
        val user = newUser()
        val keep = Salary(salary = BigDecimal("5000.00"), dateOfSalary = LocalDate.of(2025, 1, 31))
        val drop = Salary(salary = BigDecimal("4000.00"), dateOfSalary = LocalDate.of(2025, 2, 28))
        user.addSalary(keep)
        user.addSalary(drop)
        val saved = userRepository.save(user)
        entityManager.flush()
        entityManager.clear()

        // when
        val reloaded = userRepository.findById(saved.id!!).get()
        val toDrop = reloaded.salaries.first { it.salary == BigDecimal("4000.00") }
        reloaded.removeSalary(toDrop)
        userRepository.save(reloaded)
        entityManager.flush()
        entityManager.clear()

        // then
        val again = userRepository.findById(saved.id!!).get()
        assertEquals(1, again.salaries.size)
        assertEquals(BigDecimal("5000.00"), again.salaries.single().salary)
    }

    @Test
    fun `links user to departments via many-to-many`() {
        // given
        val engineering = entityManager.persist(newDepartment("Engineering"))
        val sales = entityManager.persist(newDepartment("Sales"))
        val user = newUser()
        user.addDepartment(engineering)
        user.addDepartment(sales)

        // when
        val saved = userRepository.save(user)
        entityManager.flush()
        entityManager.clear()

        // then
        val reloaded = userRepository.findById(saved.id!!).get()
        val deptNames = reloaded.departments.map { it.departmentName }.toSet()
        assertEquals(setOf("Engineering", "Sales"), deptNames)
    }

    @Test
    fun `sums annual salaries by year and full name`() {
        // given
        val user = newUser()
        user.addSalary(Salary(salary = BigDecimal("5000.00"), dateOfSalary = LocalDate.of(2025, 1, 31)))
        user.addSalary(Salary(salary = BigDecimal("5500.00"), dateOfSalary = LocalDate.of(2025, 12, 31)))
        user.addSalary(Salary(salary = BigDecimal("9999.00"), dateOfSalary = LocalDate.of(2024, 6, 30)))
        userRepository.save(user)
        entityManager.flush()

        // when
        val sum = userRepository.sumAnnualSalariesByYearAndFullName(2025, "Anna", "Kowalska")

        // then
        assertEquals(0, BigDecimal("10500.00").compareTo(sum))
    }

    @Test
    fun `returns zero when no salaries match the criteria`() {
        // given
        userRepository.save(newUser())
        entityManager.flush()

        // when
        val sum = userRepository.sumAnnualSalariesByYearAndFullName(2099, "Ghost", "Person")

        // then
        assertEquals(0, BigDecimal.ZERO.compareTo(sum))
    }

    private fun newUser(): User = User(
        firstName = "Anna",
        lastName = "Kowalska",
        userName = "annak",
        password = "secret",
        description = "Senior engineer",
    )

    private fun newDepartment(name: String): Department = Department(
        address = "Main St",
        departmentName = name,
        mail = "$name@company.com",
        phone = "111-111",
    )
}
