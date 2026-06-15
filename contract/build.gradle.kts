// Shared JMS message contract (SalaryMessage + queue name). Plain Kotlin library so both the
// producer (:salary-generator) and the consumer (:payroll-service) depend on the same types.
plugins {
	kotlin("jvm")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}
