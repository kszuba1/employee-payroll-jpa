package com.github.kszuba1.employeepayrolljpa.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var firstName: String,
    var lastName: String,
    var userName: String,
    var password: String,
    var description: String,
    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val salaries: MutableSet<Salary> = mutableSetOf(),
    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "user_department",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "department_id")],
    )
    val departments: MutableSet<Department> = mutableSetOf(),
) {
    fun addSalary(salary: Salary) {
        salaries.add(salary)
        salary.user = this
    }

    fun removeSalary(salary: Salary) {
        salaries.remove(salary)
        salary.user = null
    }

    fun addDepartment(department: Department) {
        departments.add(department)
        department.users.add(this)
    }

    fun removeDepartment(department: Department) {
        departments.remove(department)
        department.users.remove(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String =
        "User(id=$id, userName=$userName, firstName=$firstName, lastName=$lastName)"
}
