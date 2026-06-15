package com.github.kszuba1.employeepayrolljpa.consumer

import com.github.kszuba1.employeepayrolljpa.entity.Salary
import com.github.kszuba1.employeepayrolljpa.messaging.SALARY_QUEUE
import com.github.kszuba1.employeepayrolljpa.messaging.SalaryMessage
import com.github.kszuba1.employeepayrolljpa.repository.SalaryRepository
import com.github.kszuba1.employeepayrolljpa.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper

@Service
class SalaryConsumerService(
    private val salaryRepository: SalaryRepository,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @JmsListener(destination = SALARY_QUEUE)
    fun receive(payload: String) {
        val message = objectMapper.readValue(payload, SalaryMessage::class.java)
        val user = userRepository.findById(message.userId).orElseThrow {
            IllegalStateException("Cannot persist salary: user ${message.userId} not found")
        }
        salaryRepository.save(
            Salary(
                dateOfSalary = message.dateOfSalary,
                salary = message.salary,
                bonus = message.bonus,
                user = user,
            ),
        )
        log.info("Persisted salary for user {} dated {}", message.userId, message.dateOfSalary)
    }
}
