package ru.cbr.adapter.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cbr.adapter")
data class CbrProperties(
  val baseUrl: String = "https://www.cbr.ru/scripts",
  val connectTimeout: Long = 5000,
  val readTimeout: Long = 10000,
  val cacheEnabled: Boolean = true,
  val cacheTtlMinutes: Long = 60,
)
