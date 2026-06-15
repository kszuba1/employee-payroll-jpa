package com.github.kszuba1.employeepayrolljpa.repository

import com.github.kszuba1.employeepayrolljpa.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

interface UserRepository : JpaRepository<User, Long> {

    @Query(
        """
        SELECT COALESCE(SUM(s.salary), 0)
        FROM Salary s
        JOIN s.user u
        WHERE EXTRACT(YEAR FROM s.dateOfSalary) = :year
          AND u.firstName = :firstName
          AND u.lastName = :lastName
        """
    )
    fun sumAnnualSalariesByYearAndFullName(
        @Param("year") year: Int,
        @Param("firstName") firstName: String,
        @Param("lastName") lastName: String,
    ): BigDecimal
}