package com.github.kszuba1.employeepayrolljpa.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun employeePayrollOpenApi(): OpenAPI =
        OpenAPI().info(
            Info()
                .title("Employee Payroll REST API")
                .description(
                    "SOA/RESTful service over a JPA data layer: browse and create users and " +
                        "departments (with column sorting), manage department membership, and run " +
                        "accounting queries (annual department/person totals, monthly company tax).",
                )
                .version("v1"),
        )
}
