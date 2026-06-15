package com.github.kszuba1.employeepayrolljpa.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany

@Entity
class Department(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var address: String,
    var departmentName: String,
    var mail: String,
    var phone: String,
    var description: String? = null,
    @ManyToMany(mappedBy = "departments")
    val users: MutableSet<User> = mutableSetOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Department) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String =
        "Department(id=$id, departmentName=$departmentName, mail=$mail)"
}