package com.github.kszuba1.employeepayrolljpa.repository

import com.github.kszuba1.employeepayrolljpa.entity.Salary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

interface SalaryRepository : JpaRepository<Salary, Long> {

    @Query(
        """
        SELECT COALESCE(SUM(s.salary), 0) * :taxPercentage / 100
        FROM Salary s
        WHERE EXTRACT(YEAR FROM s.dateOfSalary) = :year
          AND EXTRACT(MONTH FROM s.dateOfSalary) = :month
        """
    )
    fun calculateMonthlyTax(
        @Param("year") year: Int,
        @Param("month") month: Int,
        @Param("taxPercentage") taxPercentage: BigDecimal,
    ): BigDecimal
}
