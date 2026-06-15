package com.github.kszuba1.employeepayrolljpa.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal
import java.time.LocalDate

@Entity
class Salary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var bonus: BigDecimal? = null,
    var dateOfSalary: LocalDate,
    var salary: BigDecimal,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Salary) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String =
        "Salary(id=$id, dateOfSalary=$dateOfSalary, salary=$salary, bonus=$bonus)"
}