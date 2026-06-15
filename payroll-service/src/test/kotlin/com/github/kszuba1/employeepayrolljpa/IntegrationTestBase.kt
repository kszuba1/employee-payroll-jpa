package com.github.kszuba1.employeepayrolljpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(
    properties = [
        "spring.sql.init.mode=always",
        "spring.jpa.defer-datasource-initialization=true",
    ],
)
abstract class IntegrationTestBase {

    @Autowired
    protected lateinit var mockMvc: MockMvc
}
