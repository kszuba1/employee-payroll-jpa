package com.github.kszuba1.employeepayrolljpa.controller

import com.github.kszuba1.employeepayrolljpa.IntegrationTestBase
import org.hamcrest.Matchers.hasItems
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerIntegrationTest : IntegrationTestBase() {

    @Test
    fun `lists the seeded users as JSON`() {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(4))
    }

    @Test
    fun `returns a user with its department names`() {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userName").value("annak"))
            .andExpect(jsonPath("$.departmentNames", hasItems("Engineering", "Sales")))
    }

    @Test
    fun `returns 404 for an unknown user`() {
        mockMvc.perform(get("/api/users/9999"))
            .andExpect(status().isNotFound())
    }

    @Test
    fun `sorts users by a column ascending then descending`() {
        mockMvc.perform(get("/api/users").param("sortBy", "lastName"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Kowalska"))
            .andExpect(jsonPath("$[3].lastName").value("Smith"))

        mockMvc.perform(get("/api/users").param("sortBy", "lastName").param("direction", "desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Smith"))
    }

    @Test
    fun `rejects an unknown sort column with 400`() {
        mockMvc.perform(get("/api/users").param("sortBy", "password"))
            .andExpect(status().isBadRequest())
    }

    @Test
    fun `accepts a case-insensitive direction and rejects an invalid one`() {
        mockMvc.perform(get("/api/users").param("sortBy", "lastName").param("direction", "DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Smith"))

        mockMvc.perform(get("/api/users").param("sortBy", "lastName").param("direction", "sideways"))
            .andExpect(status().isBadRequest())
    }

    @Test
    fun `lists the sortable columns`() {
        mockMvc.perform(get("/api/users/sortable-columns"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasItems("id", "firstName", "lastName", "userName", "description")))
    }

    @Test
    fun `sums a person's annual salaries`() {
        mockMvc.perform(
            get("/api/users/annual-total")
                .param("year", "2025")
                .param("firstName", "Anna")
                .param("lastName", "Kowalska"),
        )
            .andExpect(status().isOk())
            .andExpect(content().json("""{"total":16500.00}"""))
    }

    @Test
    fun `creates a user assigned to departments`() {
        val body = """{"firstName":"Eve","lastName":"Adams","userName":"evea","password":"pw","description":"New hire","departmentIds":[10,20]}"""

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.userName").value("evea"))
            .andExpect(jsonPath("$.departmentNames", hasItems("Engineering", "Sales")))

        mockMvc.perform(get("/api/users"))
            .andExpect(jsonPath("$.length()").value(5))
    }

    @Test
    fun `rejects creating a user that references a missing department`() {
        val body = """{"firstName":"X","lastName":"Y","userName":"xy","password":"pw","description":"d","departmentIds":[999]}"""
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest())
    }
}
