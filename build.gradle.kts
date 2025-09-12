plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.spring)
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependency.management)
  alias(libs.plugins.ktlint)
  `maven-publish`
}

group = "me.func"
version = "0.0.1-SNAPSHOT"
description = "cbr"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  // Spring
  implementation(libs.spring.boot.starter)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.boot.starter.configuration.processor)
  implementation(libs.spring.boot.starter.logging)
  implementation(libs.spring.boot.starter.actuator)

  // Kotlin
  implementation(libs.jackson.module.kotlin)
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlin.logging.jvm)

  // Tests
  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.kotlin.test.junit5)
  testImplementation(libs.mockito.kotlin)
  testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
  jvmToolchain(libs.versions.java.get().toInt())
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.bootJar {
  enabled = false
}

tasks.jar {
  enabled = true
  archiveClassifier = ""
}

ktlint {
  android.set(false)
  outputToConsole.set(true)
  ignoreFailures.set(false)
  filter {
    exclude("**/generated/**", "**/*.kts")
    include("**/kotlin/**")
  }
}


publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      pom {
        name.set("Spring Boot Starter CBR Rates")
        description.set("Spring Boot starter for Central Bank of Russia exchange rates integration")

        developers {
          developer {
            name.set("CBR Team")
            organization.set("Central Bank of Russia")
          }
        }
      }
    }
  }
}