package com.github.kszuba1.employeepayrolljpa.generator

import com.github.kszuba1.employeepayrolljpa.messaging.SALARY_QUEUE
import com.github.kszuba1.employeepayrolljpa.messaging.SalaryMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import kotlin.random.Random

@Component
class SalaryGenerator(
    private val jmsTemplate: JmsTemplate,
    private val objectMapper: ObjectMapper,
    @param:Value("\${app.rest.base-url:http://localhost:8080}") private val restBaseUrl: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun generate(spec: GenerationSpec): GenerationSummary {
        spec.validate()
        val userIds = fetchUserIds()
        val messages = buildSalaryMessages(userIds, spec, LocalDate.now())
        messages.forEach { jmsTemplate.convertAndSend(SALARY_QUEUE, objectMapper.writeValueAsString(it)) }
        return GenerationSummary(
            users = userIds.size,
            monthsPerUser = spec.monthsPerUser,
            messagesPublished = messages.size,
            queue = SALARY_QUEUE,
        )
    }

    private fun fetchUserIds(): List<Long> {
        log.info("Fetching users from {}/api/users", restBaseUrl)
        val users = RestClient.create(restBaseUrl)
            .get()
            .uri("/api/users")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<RestUser>>() {})
            ?: emptyList()
        return users.map { it.id }
    }

    data class RestUser(
        val id: Long,
        val firstName: String? = null,
        val lastName: String? = null,
    )
}

data class GenerationSpec(
    val monthsPerUser: Int,
    val minSalary: BigDecimal,
    val maxSalary: BigDecimal,
    val maxBonus: BigDecimal,
) {
    fun validate() {
        require(monthsPerUser >= 1) { "monthsPerUser must be >= 1" }
        require(minSalary >= BigDecimal.ZERO) { "minSalary must be >= 0" }
        require(minSalary <= maxSalary) { "minSalary must be <= maxSalary" }
        require(maxBonus >= BigDecimal.ZERO) { "maxBonus must be >= 0" }
    }
}

data class GenerationSummary(
    val users: Int,
    val monthsPerUser: Int,
    val messagesPublished: Int,
    val queue: String,
)

internal fun buildSalaryMessages(
    userIds: List<Long>,
    spec: GenerationSpec,
    today: LocalDate,
    random: Random = Random.Default,
): List<SalaryMessage> =
    userIds.flatMapIndexed { userIndex, userId ->
        (0 until spec.monthsPerUser).map { monthIndex ->
            SalaryMessage(
                userId = userId,
                dateOfSalary = salaryDate(today, userIndex, monthIndex, spec.monthsPerUser),
                salary = randomAmount(spec.minSalary, spec.maxSalary, random),
                bonus = randomBonus(spec.maxBonus, random),
            )
        }
    }

internal fun salaryDate(today: LocalDate, userIndex: Int, monthIndex: Int, monthsPerUser: Int): LocalDate =
    today.minusMonths(userIndex.toLong() * monthsPerUser + monthIndex)

private fun randomAmount(min: BigDecimal, max: BigDecimal, random: Random): BigDecimal =
    (min + (max - min) * BigDecimal(random.nextDouble())).setScale(2, RoundingMode.HALF_UP)

private fun randomBonus(maxBonus: BigDecimal, random: Random): BigDecimal? {
    if (maxBonus <= BigDecimal.ZERO || random.nextInt(BONUS_FREQUENCY) != 0) return null
    return (maxBonus * BigDecimal(random.nextDouble())).setScale(2, RoundingMode.HALF_UP)
}

private const val BONUS_FREQUENCY = 3
