package com.github.kszuba1.employeepayrolljpa.web

import com.github.kszuba1.employeepayrolljpa.IntegrationTestBase
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Cross-cutting error handling — exercises [GlobalExceptionHandler] / the inherited
 * [org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler]
 * behaviour that isn't tied to a single controller.
 */
class GlobalExceptionHandlerIntegrationTest : IntegrationTestBase() {

    @Test
    fun `a non-numeric path variable returns 400`() {
        mockMvc.perform(get("/api/users/abc"))
            .andExpect(status().isBadRequest())
    }

    @Test
    fun `a missing required query parameter returns 400`() {
        mockMvc.perform(
            get("/api/users/annual-total")
                .param("firstName", "Anna")
                .param("lastName", "Kowalska"),
        )
            .andExpect(status().isBadRequest())
    }

    @Test
    fun `a malformed JSON body returns 400`() {
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("this is not json"),
        )
            .andExpect(status().isBadRequest())
    }

    @Test
    fun `an unknown route returns 404`() {
        mockMvc.perform(get("/api/does-not-exist"))
            .andExpect(status().isNotFound())
    }
}
