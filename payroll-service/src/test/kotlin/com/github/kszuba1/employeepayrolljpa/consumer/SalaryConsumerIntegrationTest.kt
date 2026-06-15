package com.github.kszuba1.employeepayrolljpa.consumer

import com.github.kszuba1.employeepayrolljpa.messaging.SALARY_QUEUE
import com.github.kszuba1.employeepayrolljpa.messaging.SalaryMessage
import com.github.kszuba1.employeepayrolljpa.repository.SalaryRepository
import com.github.kszuba1.employeepayrolljpa.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

/**
 * Verifies SalaryConsumerService persists a salary it consumes off the queue. Uses its own in-VM
 * broker and database so the message can't be stolen by another cached context's listener and the
 * extra row can't disturb the count-sensitive controller tests.
 */
@SpringBootTest
@TestPropertySource(
    properties = [
        "spring.sql.init.mode=always",
        "spring.jpa.defer-datasource-initialization=true",
        "spring.datasource.url=jdbc:h2:mem:consumertest;DB_CLOSE_DELAY=-1",
        "spring.activemq.broker-url=vm://consumertest?broker.persistent=false&broker.useJmx=false",
    ],
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SalaryConsumerIntegrationTest {

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var salaryRepository: SalaryRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `consumes a salary message off the queue and persists it`() {
        val initial = salaryRepository.count()
        val userId = userRepository.findAll().first().id!!

        val message = SalaryMessage(
            userId = userId,
            dateOfSalary = LocalDate.of(2026, 6, 13),
            salary = BigDecimal("5000.00"),
            bonus = BigDecimal("250.00"),
        )
        jmsTemplate.convertAndSend(SALARY_QUEUE, objectMapper.writeValueAsString(message))

        // Consumed asynchronously on the listener thread — await it (up to 10s).
        val target = initial + 1
        val deadline = System.currentTimeMillis() + 10_000
        while (salaryRepository.count() < target && System.currentTimeMillis() < deadline) {
            Thread.sleep(100)
        }
        assertEquals(target, salaryRepository.count())
    }
}
