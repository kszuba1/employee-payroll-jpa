package com.github.kszuba1.employeepayrolljpa.controller

import com.github.kszuba1.employeepayrolljpa.IntegrationTestBase
import org.hamcrest.Matchers.hasItems
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DepartmentControllerIntegrationTest : IntegrationTestBase() {

    @Test
    fun `lists departments sorted by name with member counts`() {
        mockMvc.perform(get("/api/departments").param("sortBy", "departmentName"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].departmentName").value("Engineering"))
            .andExpect(jsonPath("$[0].userCount").value(2))
    }

    @Test
    fun `lists the sortable columns`() {
        mockMvc.perform(get("/api/departments/sortable-columns"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasItems("id", "address", "departmentName", "mail", "phone", "description")))
    }

    @Test
    fun `sums a department's annual salaries`() {
        mockMvc.perform(
            get("/api/departments/annual-total")
                .param("year", "2025")
                .param("departmentName", "Engineering"),
        )
            .andExpect(status().isOk())
            .andExpect(content().json("""{"total":23500.00}"""))
    }

    @Test
    fun `creates a department`() {
        val body = """{"address":"New St 2","departmentName":"Legal","mail":"legal@company.com","phone":"444-444","description":"Contracts"}"""
        mockMvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.departmentName").value("Legal"))
            .andExpect(jsonPath("$.userCount").value(0))
    }

    @Test
    fun `adds a user to a department`() {
        // Bob (id 2) starts in Sales only
        mockMvc.perform(put("/api/departments/10/users/2"))
            .andExpect(status().isNoContent())

        mockMvc.perform(get("/api/users/2"))
            .andExpect(jsonPath("$.departmentNames", hasItems("Engineering", "Sales")))
    }

    @Test
    fun `adding a user is idempotent and does not duplicate`() {
        repeat(2) {
            mockMvc.perform(put("/api/departments/20/users/2"))
                .andExpect(status().isNoContent())
        }

        mockMvc.perform(get("/api/users/2"))
            .andExpect(jsonPath("$.departmentNames.length()").value(1))
            .andExpect(jsonPath("$.departmentNames", hasItems("Sales")))
    }

    @Test
    fun `removes a user from a department`() {
        mockMvc.perform(delete("/api/departments/20/users/2"))
            .andExpect(status().isNoContent())

        mockMvc.perform(get("/api/users/2"))
            .andExpect(jsonPath("$.departmentNames").isEmpty())
    }

    @Test
    fun `returns 404 when the department or user is missing`() {
        mockMvc.perform(put("/api/departments/999/users/2"))
            .andExpect(status().isNotFound())
        mockMvc.perform(put("/api/departments/10/users/999"))
            .andExpect(status().isNotFound())
    }
}
