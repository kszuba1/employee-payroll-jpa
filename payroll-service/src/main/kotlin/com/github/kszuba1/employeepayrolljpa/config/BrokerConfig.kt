package com.github.kszuba1.employeepayrolljpa.config

import org.apache.activemq.broker.BrokerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Hosts the "Queue Server (MOM)": an embedded ActiveMQ "Classic" broker exposed over TCP so the
 * standalone :salary-generator process can publish to the queue this service consumes from.
 * Disabled under the `test` profile, where the suite uses an in-VM broker instead of binding a port.
 */
@Configuration
@Profile("!test")
class BrokerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun brokerService(
        @Value("\${app.broker.bind-url:tcp://localhost:61616}") bindUrl: String,
    ): BrokerService = BrokerService().apply {
        brokerName = "payroll"
        isPersistent = false
        isUseJmx = false
        isUseShutdownHook = false
        addConnector(bindUrl)
    }
}
