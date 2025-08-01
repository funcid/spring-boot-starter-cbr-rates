# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot starter for CBR (Central Bank of Russia) exchange rates integration. Provides auto-configuration and client
for fetching exchange rates from CBR API.

## Technology Stack

- **Language**: Kotlin
- **Build Tool**: Gradle with Kotlin DSL
- **Framework**: Spring Boot 3.3.2
- **Target JDK**: 17

## Development Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Clean build
./gradlew clean build

# Publish to local repository
./gradlew publishToMavenLocal
```

## Project Structure

```
src/main/kotlin/ru/cbr/rates/
├── autoconfigure/          # Auto-configuration classes
│   └── CbrRatesAutoConfiguration.kt
├── client/                 # CBR API client
│   └── CbrRatesClient.kt
└── config/                 # Configuration properties
    └── CbrRatesProperties.kt

src/main/resources/META-INF/spring/
└── org.springframework.boot.autoconfigure.AutoConfiguration.imports

src/test/kotlin/ru/cbr/rates/
└── autoconfigure/          # Auto-configuration tests
    └── CbrRatesAutoConfigurationTest.kt
```

## Configuration Properties

The starter uses `cbr.rates` prefix for configuration. All properties have autocomplete support in `application.properties`:

- `cbr.rates.enabled` - Enable/disable the starter (default: true)
- `cbr.rates.base-url` - CBR API base URL (default: https://www.cbr.ru/scripts)
- `cbr.rates.connect-timeout` - Connection timeout in milliseconds (default: 5000)
- `cbr.rates.read-timeout` - Read timeout in milliseconds (default: 10000)
- `cbr.rates.cache-enabled` - Enable caching (default: true)
- `cbr.rates.cache-ttl-minutes` - Cache TTL in minutes (default: 60)

**Configuration metadata**: `src/main/resources/META-INF/spring-configuration-metadata.json` provides IDE autocomplete support.

## Usage

When included as dependency, the starter automatically configures:

- `CbrRatesClient` bean for fetching exchange rates
- `RestTemplate` bean with configured timeouts
- Configuration properties binding