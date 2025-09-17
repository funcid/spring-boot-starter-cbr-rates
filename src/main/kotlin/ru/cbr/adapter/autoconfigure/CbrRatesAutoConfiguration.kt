package ru.cbr.adapter.autoconfigure

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import ru.cbr.adapter.client.CbrRatesClient
import ru.cbr.adapter.config.CbrProperties
import java.time.Duration
import ru.cbr.adapter.client.CbrBankBicClient
import ru.cbr.adapter.parser.XmlParser

@AutoConfiguration
@ConditionalOnClass(RestTemplate::class)
@ConditionalOnProperty(prefix = "cbr.adapter", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CbrProperties::class)
class CbrRatesAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  fun cbrRestTemplate(properties: CbrProperties): RestTemplate =
    RestTemplate().apply {
      requestFactory =
        org.springframework.http.client.SimpleClientHttpRequestFactory().apply {
          setConnectTimeout(Duration.ofMillis(properties.connectTimeout))
          setReadTimeout(Duration.ofMillis(properties.readTimeout))
        }
    }

  @Bean
  @ConditionalOnMissingBean
  fun ratesXmlParser() = XmlParser()

  @Bean
  @ConditionalOnMissingBean
  fun cbrRatesClient(
    cbrRestTemplate: RestTemplate,
    properties: CbrProperties,
    xmlParser: XmlParser
  ): CbrRatesClient = CbrRatesClient(cbrRestTemplate, properties, xmlParser)

  @Bean
  @ConditionalOnMissingBean
  fun cbrBankBicClient(
    cbrRestTemplate: RestTemplate,
    properties: CbrProperties,
    xmlParser: XmlParser
  ): CbrBankBicClient = CbrBankBicClient(cbrRestTemplate, properties, xmlParser)
}
