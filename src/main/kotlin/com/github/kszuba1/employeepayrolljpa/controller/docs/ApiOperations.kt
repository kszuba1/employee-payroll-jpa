package com.github.kszuba1.employeepayrolljpa.controller.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ProblemDetail

@Operation(
    summary = "List users",
    description = "Returns all users as a JSON array, each with the names of the departments it " +
            "belongs to. Optionally sorted via `sortBy` (a column from GET /api/users/sortable-columns) " +
            "and `direction`.",
    responses = [
        ApiResponse(responseCode = "200", description = "The (optionally sorted) list of users"),
        ApiResponse(
            responseCode = "400",
            description = "Unknown sort column or invalid direction",
            content = [Content(schema = Schema(implementation = ProblemDetail::class))],
        ),
    ],
)
annotation class ListUsersOperation

@Operation(
    summary = "Get a user by id",
    responses = [
        ApiResponse(responseCode = "200", description = "The user"),
        ApiResponse(
            responseCode = "404",
            description = "No user with the given id",
            content = [Content(schema = Schema(implementation = ProblemDetail::class))],
        ),
    ],
)
annotation class GetUserOperation

@Operation(
    summary = "Create a user",
    description = "Creates a user and optionally assigns it to departments via `departmentIds`.",
    responses = [
        ApiResponse(responseCode = "201", description = "The created user"),
        ApiResponse(
            responseCode = "400",
            description = "Malformed body or a referenced department does not exist",
            content = [Content(schema = Schema(implementation = ProblemDetail::class))],
        ),
    ],
)
annotation class CreateUserOperation

@Operation(
    summary = "Annual salary total for a person",
    description = "Sum of all salaries paid to the person (matched by first and last name) within the given calendar year.",
    responses = [
        ApiResponse(responseCode = "200", description = "The total (0 when nothing matches)"),
        ApiResponse(
            responseCode = "400",
            description = "Missing or invalid query parameter",
            content = [Content(schema = Schema(implementation = ProblemDetail::class))],
        ),
    ],
)
annotation class UserAnnualTotalOperation


@Operation(
    summary = "List departments",
    description = "Returns all departments (with member counts). Optionally sorted via `sortBy` " +
            "(a column from GET /api/departments/sortable-columns) and `direction`.",
    responses = [
        ApiResponse(responseCode = "200", description = "The (optionally sorted) list of departments"),
        ApiResponse(
            responseCode = "400",
            description = "Unknown sort column or invalid direction",
            content = [Content(schema = Schema(implementation = ProblemDetail::class))],
        ),
    ],
)
annotation class ListDepartmentsOperation

@Operation(
    summary = "Create a department",
    responses = [
        ApiResponse(responseCode = "201", description = "The created department"),
        ApiResponse(
            responseCode = "400",
            description = "Malformed body",
            content = [Content(schema = Schema(implementation = ProblemDetail::class))],
        ),
    ],
)
annotation class CreateDepartmentOperation

@Operation(
    summary = "Annual salary total for a department",
    description = "Sum of all salaries paid in the given calendar year to users belonging to the " +
            "named department. Note: with the many-to-many model a user in several departments is " +
            "counted in each of them.",
    responses = [
        ApiResponse(responseCode = "200", description = "The total (0 when nothing matches)"),
        ApiResponse(
            responseCode = "400",
            description = "Missing or invalid query parameter",
            content = [Content(schema = Schema(implementation = ProblemDetail::class))],
        ),
    ],
)
annotation class DepartmentAnnualTotalOperation

@Operation(
    summary = "Add a user to a department",
    description = "Adds the user to the department's membership. Idempotent: repeating the call leaves a single membership.",
    responses = [
        ApiResponse(responseCode = "204", description = "Membership ensured"),
        ApiResponse(
            responseCode = "404",
            description = "The department or the user does not exist",
            content = [Content(schema = Schema(implementation = ProblemDetail::class))],
        ),
    ],
)
annotation class AddUserToDepartmentOperation

@Operation(
    summary = "Remove a user from a department",
    description = "Removes the user from the department's membership. Idempotent: a no-op if the user is not a member.",
    responses = [
        ApiResponse(responseCode = "204", description = "Membership removed (or already absent)"),
        ApiResponse(
            responseCode = "404",
            description = "The department or the user does not exist",
            content = [Content(schema = Schema(implementation = ProblemDetail::class))],
        ),
    ],
)
annotation class RemoveUserFromDepartmentOperation


@Operation(
    summary = "Monthly company tax",
    description = "Total tax owed by the company for the given month and year, computed at the supplied percentage rate.",
    responses = [
        ApiResponse(responseCode = "200", description = "The computed tax (0 when nothing matches)"),
        ApiResponse(
            responseCode = "400",
            description = "Missing or invalid query parameter",
            content = [Content(schema = Schema(implementation = ProblemDetail::class))],
        ),
    ],
)
annotation class MonthlyCompanyTaxOperation
