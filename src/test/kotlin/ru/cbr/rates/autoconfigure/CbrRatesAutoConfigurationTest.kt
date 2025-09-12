package ru.cbr.rates.autoconfigure

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.web.client.RestTemplate
import ru.cbr.rates.client.CbrRatesClient
import ru.cbr.rates.config.CbrRatesProperties
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CbrRatesAutoConfigurationTest {
  private val contextRunner =
    ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(CbrRatesAutoConfiguration::class.java))

  @Test
  fun `should create CBR rates client bean when auto-configuration is enabled`() {
    contextRunner.run { context ->
      assertNotNull(context.getBean(CbrRatesClient::class.java))
      assertNotNull(context.getBean("cbrRatesRestTemplate", RestTemplate::class.java))
      assertNotNull(context.getBean(CbrRatesProperties::class.java))
    }
  }

  @Test
  fun `should not create CBR rates client when disabled`() {
    contextRunner
      .withPropertyValues("cbr.rates.enabled=false")
      .run { context ->
        assert(!context.containsBean("cbrRatesClient"))
      }
  }

  @Test
  fun `should configure properties with default values`() {
    contextRunner.run { context ->
      val properties = context.getBean(CbrRatesProperties::class.java)
      assertEquals("https://www.cbr.ru/scripts", properties.baseUrl)
      assertEquals(5000, properties.connectTimeout)
      assertEquals(10000, properties.readTimeout)
      assertEquals(true, properties.cacheEnabled)
      assertEquals(60, properties.cacheTtlMinutes)
    }
  }

  @Test
  fun `should configure properties with custom values`() {
    contextRunner
      .withPropertyValues(
        "cbr.rates.baseUrl=https://custom.cbr.ru",
        "cbr.rates.connectTimeout=3000",
        "cbr.rates.readTimeout=15000",
        "cbr.rates.cacheEnabled=false",
        "cbr.rates.cacheTtlMinutes=30",
      ).run { context ->
        val properties = context.getBean(CbrRatesProperties::class.java)
        assertEquals("https://custom.cbr.ru", properties.baseUrl)
        assertEquals(3000, properties.connectTimeout)
        assertEquals(15000, properties.readTimeout)
        assertEquals(false, properties.cacheEnabled)
        assertEquals(30, properties.cacheTtlMinutes)
      }
  }
}
