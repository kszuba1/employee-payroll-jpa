// Root build for the multi-module Lab VII-VIII project. Plugins are declared here (versions in
// one place) but applied in the subprojects that need them. See:
//   :contract          — shared JMS message contract (plain Kotlin library)
//   :payroll-service   — RestAPI + JPA DataStore + SalaryConsumerService + embedded queue broker
//   :salary-generator  — standalone app: fetches users over REST and publishes salaries to the queue
plugins {
	kotlin("jvm") version "2.2.21" apply false
	kotlin("plugin.spring") version "2.2.21" apply false
	kotlin("plugin.jpa") version "2.2.21" apply false
	id("org.springframework.boot") version "4.0.6" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
	group = "com.github.kszuba1"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}
