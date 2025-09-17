package ru.cbr.adapter.autoconfigure

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import ru.cbr.adapter.client.CbrBankBicClient
import ru.cbr.adapter.client.CbrInterbankCreditMarketClient
import ru.cbr.adapter.client.CbrMetalClient
import ru.cbr.adapter.client.CbrRatesClient
import ru.cbr.adapter.config.CbrProperties
import ru.cbr.adapter.parser.XmlParser
import java.time.Duration

@AutoConfiguration
@ConditionalOnClass(RestTemplate::class)
@ConditionalOnProperty(prefix = "cbr.adapter", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CbrProperties::class)
class CbrAutoConfiguration {
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

  @Bean("metalsCbrClient")
  @ConditionalOnMissingBean
  fun cbrMetalClient(
    cbrRestTemplate: RestTemplate,
    properties: CbrProperties,
    xmlParser: XmlParser
  ): CbrMetalClient = CbrMetalClient(cbrRestTemplate, properties, xmlParser)

  @Bean
  @ConditionalOnMissingBean
  fun cbrInterbankCreditMarketClient(
    cbrRestTemplate: RestTemplate,
    properties: CbrProperties,
    xmlParser: XmlParser
  ): CbrInterbankCreditMarketClient =
    CbrInterbankCreditMarketClient(cbrRestTemplate, properties, xmlParser)
}
