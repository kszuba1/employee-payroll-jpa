package com.github.kszuba1.employeepayrolljpa.controller

import com.github.kszuba1.employeepayrolljpa.IntegrationTestBase
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SalaryControllerIntegrationTest : IntegrationTestBase() {

    @Test
    fun `lists the seeded salaries`() {
        mockMvc.perform(get("/api/salaries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(16))
    }

    @Test
    fun `computes the monthly company tax`() {
        mockMvc.perform(
            get("/api/salaries/monthly-tax")
                .param("year", "2025")
                .param("month", "1")
                .param("taxPercentage", "19"),
        )
            .andExpect(status().isOk())
            .andExpect(content().json("""{"total":3382.00}"""))
    }
}
