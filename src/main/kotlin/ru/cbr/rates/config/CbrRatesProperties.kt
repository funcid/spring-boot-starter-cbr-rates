package ru.cbr.rates.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cbr.rates")
data class CbrRatesProperties(
    val baseUrl: String = "https://www.cbr.ru/scripts",
    val connectTimeout: Long = 5000,
    val readTimeout: Long = 10000,
    val cacheEnabled: Boolean = true,
    val cacheTtlMinutes: Long = 60
)