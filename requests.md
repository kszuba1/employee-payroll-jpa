# Employee Payroll — Example Requests

Ready-to-run examples for every REST endpoint, with bodies and expected results based on
the seed data in [`src/main/resources/data.sql`](src/main/resources/data.sql).

Base URL: **`http://localhost:8080`**

## Table of Contents

- [Getting started](#getting-started)
- [API docs & tools](#api-docs--tools)
- [Users](#users)
  - [List users](#list-users)
  - [List sortable columns for users](#list-sortable-columns-for-users)
  - [Get user by id](#get-user-by-id)
  - [Create user](#create-user)
  - [Annual salary total for a person](#annual-salary-total-for-a-person)
- [Departments](#departments)
  - [List departments](#list-departments)
  - [List sortable columns for departments](#list-sortable-columns-for-departments)
  - [Create department](#create-department)
  - [Add user to department](#add-user-to-department)
  - [Remove user from department](#remove-user-from-department)
  - [Annual salary total for a department](#annual-salary-total-for-a-department)
- [Salaries](#salaries)
  - [List salaries](#list-salaries)
  - [Monthly company tax](#monthly-company-tax)
- [Seed data reference](#seed-data-reference)

---

## Getting started

Run the app:

```bash
./gradlew bootRun
```

It starts on `http://localhost:8080` and loads the seed data automatically.

- Responses are JSON.
- List endpoints (`/api/users`, `/api/departments`) accept optional `sortBy` and `direction` query params.
- Errors are returned as [RFC 7807](https://www.rfc-editor.org/rfc/rfc9457) `ProblemDetail` JSON:
  `400` for bad input (unknown sort column, invalid/missing param, malformed body),
  `404` for a missing resource.

---

## API docs & tools

| What | URL |
| --- | --- |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| H2 console | `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:payroll`, user `sa`, no password) |

---

## Users

### List users

`GET /api/users` — all users, each with the names of the departments it belongs to.
Optional: `?sortBy=<column>&direction=asc|desc` (see [sortable columns](#list-sortable-columns-for-users)).

```bash
# all users
curl -s "http://localhost:8080/api/users"

# sorted by last name, descending
curl -s "http://localhost:8080/api/users?sortBy=lastName&direction=desc"
```

Returns 4 users (Anna, Bob, Carol, Dawid). Example element:

```json
{
  "id": 1,
  "firstName": "Anna",
  "lastName": "Kowalska",
  "userName": "annak",
  "description": "Senior engineer",
  "departmentNames": ["Engineering", "Sales"],
  "salaries": [ { "id": 100, "dateOfSalary": "2024-01-31", "salary": 5000.00, "bonus": null } ]
}
```

### List sortable columns for users

`GET /api/users/sortable-columns` — the values accepted by `sortBy`.

```bash
curl -s "http://localhost:8080/api/users/sortable-columns"
```

```json
["description","firstName","id","lastName","userName"]
```

### Get user by id

`GET /api/users/{id}` — `200` with the user, or `404` if it does not exist.

```bash
curl -s "http://localhost:8080/api/users/1"     # Anna Kowalska
curl -s -o /dev/null -w "%{http_code}\n" "http://localhost:8080/api/users/9999"   # 404
```

### Create user

`POST /api/users` — creates a user and (optionally) assigns it to departments via `departmentIds`.
Returns `201` with the created user. A non-existent `departmentIds` entry → `400`.

Body:

```json
{
  "firstName": "Eve",
  "lastName": "Adams",
  "userName": "evea",
  "password": "secret",
  "description": "New hire",
  "departmentIds": [10, 20]
}
```

```bash
curl -s -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Eve","lastName":"Adams","userName":"evea","password":"secret","description":"New hire","departmentIds":[10,20]}'
```

Response (generated ids start at 100):

```json
{ "id": 100, "firstName": "Eve", "lastName": "Adams", "userName": "evea",
  "description": "New hire", "departmentNames": ["Engineering", "Sales"], "salaries": [] }
```

`departmentIds` is optional — omit it (or use `[]`) to create a user with no department.

### Annual salary total for a person

`GET /api/users/annual-total?year=&firstName=&lastName=` — sum of the person's salaries in that year.

```bash
curl -s "http://localhost:8080/api/users/annual-total?year=2025&firstName=Anna&lastName=Kowalska"
```

```json
{ "total": 16500.00 }
```

---

## Departments

### List departments

`GET /api/departments` — all departments with a member count. Optional `?sortBy=&direction=`.

```bash
curl -s "http://localhost:8080/api/departments?sortBy=departmentName"
```

Returns 3 departments. Example element:

```json
{ "id": 10, "address": "Main St 1", "departmentName": "Engineering",
  "mail": "eng@company.com", "phone": "111-111", "description": "R&D and development", "userCount": 2 }
```

### List sortable columns for departments

`GET /api/departments/sortable-columns`

```bash
curl -s "http://localhost:8080/api/departments/sortable-columns"
```

```json
["address","departmentName","description","id","mail","phone"]
```

### Create department

`POST /api/departments` — returns `201` with the created department.

Body:

```json
{
  "address": "New St 2",
  "departmentName": "Legal",
  "mail": "legal@company.com",
  "phone": "444-444",
  "description": "Contracts"
}
```

```bash
curl -s -X POST "http://localhost:8080/api/departments" \
  -H "Content-Type: application/json" \
  -d '{"address":"New St 2","departmentName":"Legal","mail":"legal@company.com","phone":"444-444","description":"Contracts"}'
```

`description` is optional.

### Add user to department

`PUT /api/departments/{departmentId}/users/{userId}` — `204`, idempotent.
`404` if the department or user does not exist.

```bash
# add Bob (2) to Engineering (10) — Bob is now in Engineering + Sales
curl -s -o /dev/null -w "%{http_code}\n" -X PUT "http://localhost:8080/api/departments/10/users/2"
```

### Remove user from department

`DELETE /api/departments/{departmentId}/users/{userId}` — `204`, idempotent.
`404` if the department or user does not exist.

```bash
# remove Bob (2) from Sales (20)
curl -s -o /dev/null -w "%{http_code}\n" -X DELETE "http://localhost:8080/api/departments/20/users/2"
```

### Annual salary total for a department

`GET /api/departments/annual-total?year=&departmentName=` — sum of salaries paid in that year to
users in the named department. (A user in several departments is counted in each — many-to-many.)

```bash
curl -s "http://localhost:8080/api/departments/annual-total?year=2025&departmentName=Engineering"
# {"total":23500.00}   (Anna 16500 + Dawid 7000)

curl -s "http://localhost:8080/api/departments/annual-total?year=2025&departmentName=Sales"
# {"total":26100.00}   (Anna 16500 + Bob 9600 — Anna counts here too)
```

---

## Salaries

### List salaries

`GET /api/salaries` — all 16 seeded salaries, each with the owning user and their department names.

```bash
curl -s "http://localhost:8080/api/salaries"
```

Example element:

```json
{ "id": 102, "dateOfSalary": "2024-12-31", "salary": 7000.00, "bonus": 1000.00,
  "user": { "id": 1, "firstName": "Anna", "lastName": "Kowalska" },
  "departmentNames": ["Engineering", "Sales"] }
```

### Monthly company tax

`GET /api/salaries/monthly-tax?year=&month=&taxPercentage=` — total tax across all salaries in that
month, at the given percentage rate.

```bash
curl -s "http://localhost:8080/api/salaries/monthly-tax?year=2025&month=1&taxPercentage=19"
```

```json
{ "total": 3382.00 }
```

(January 2025 salaries: 5500 + 4800 + 4000 + 3500 = 17800; × 19% = 3382.00)

---

## Seed data reference

Values to use in the examples above. Explicit ids are seeded; generated ids (from `POST`)
start at **100** for users/departments and **1000** for salaries.

### Departments

| id | departmentName | address | mail | phone |
| --- | --- | --- | --- | --- |
| 10 | Engineering | Main St 1 | eng@company.com | 111-111 |
| 20 | Sales | Park Ave 5 | sales@company.com | 222-222 |
| 30 | HR | Side Rd 9 | hr@company.com | 333-333 |

### Users

| id | name | userName | departments |
| --- | --- | --- | --- |
| 1 | Anna Kowalska | annak | Engineering, Sales |
| 2 | Bob Smith | bobs | Sales |
| 3 | Carol Nowak | caroln | HR |
| 4 | Dawid Lis | dawidl | Engineering |

### Salaries

| id | user | date | salary | bonus |
| --- | --- | --- | --- | --- |
| 100 | Anna (1) | 2024-01-31 | 5000.00 | — |
| 101 | Anna (1) | 2024-02-29 | 5000.00 | — |
| 102 | Anna (1) | 2024-12-31 | 7000.00 | 1000.00 |
| 103 | Bob (2) | 2024-06-30 | 4500.00 | — |
| 104 | Bob (2) | 2024-12-31 | 4500.00 | — |
| 105 | Carol (3) | 2024-03-31 | 3800.00 | — |
| 106 | Carol (3) | 2024-11-30 | 3800.00 | — |
| 200 | Anna (1) | 2025-01-31 | 5500.00 | — |
| 201 | Anna (1) | 2025-02-28 | 5500.00 | — |
| 202 | Anna (1) | 2025-03-31 | 5500.00 | — |
| 203 | Bob (2) | 2025-01-31 | 4800.00 | — |
| 204 | Bob (2) | 2025-02-28 | 4800.00 | — |
| 205 | Carol (3) | 2025-01-31 | 4000.00 | — |
| 206 | Carol (3) | 2025-02-28 | 4000.00 | — |
| 207 | Dawid (4) | 2025-01-31 | 3500.00 | — |
| 208 | Dawid (4) | 2025-02-28 | 3500.00 | — |

Handy aggregates (2025): Anna = 16500 · Bob = 9600 · Carol = 8000 · Dawid = 7000;
Engineering = 23500 (Anna + Dawid) · Sales = 26100 (Anna + Bob) · HR = 8000 (Carol).
