package com.github.kszuba1.employeepayrolljpa.repository

import com.github.kszuba1.employeepayrolljpa.entity.Department
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

interface DepartmentRepository : JpaRepository<Department, Long> {

    @Query(
        """
        SELECT COALESCE(SUM(s.salary), 0)
        FROM Salary s
        JOIN s.user u
        JOIN u.departments d
        WHERE EXTRACT(YEAR FROM s.dateOfSalary) = :year
          AND d.departmentName = :departmentName
        """
    )
    fun sumAnnualSalariesByYearAndDepartmentName(
        @Param("year") year: Int,
        @Param("departmentName") departmentName: String,
    ): BigDecimal
}