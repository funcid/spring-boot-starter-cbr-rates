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
import ru.cbr.adapter.parser.BankBicParser
import ru.cbr.adapter.parser.CurrencyListParser
import ru.cbr.adapter.parser.ExchangeRatesParser
import ru.cbr.adapter.parser.InterbankCreditMarketParser
import ru.cbr.adapter.parser.MetalParser
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
  fun bankBicParser() = BankBicParser()

  @Bean
  @ConditionalOnMissingBean
  fun currencyListParser() = CurrencyListParser()

  @Bean
  @ConditionalOnMissingBean
  fun exchangeRatesParser() = ExchangeRatesParser()

  @Bean
  @ConditionalOnMissingBean
  fun interbankCreditMarketParser() = InterbankCreditMarketParser()

  @Bean
  @ConditionalOnMissingBean
  fun metalParser() = MetalParser()

  @Bean
  @ConditionalOnMissingBean
  fun cbrRatesClient(
    cbrRestTemplate: RestTemplate,
    properties: CbrProperties,
    exchangeRatesParser: ExchangeRatesParser,
    currencyListParser: CurrencyListParser,
  ): CbrRatesClient =
    CbrRatesClient(
      cbrRestTemplate,
      properties,
      exchangeRatesParser,
      currencyListParser,
    )

  @Bean("bankBicClient")
  @ConditionalOnMissingBean
  fun cbrBankBicClient(
    cbrRestTemplate: RestTemplate,
    properties: CbrProperties,
    bankBicParser: BankBicParser,
  ): CbrBankBicClient =
    CbrBankBicClient(
      cbrRestTemplate,
      properties,
      bankBicParser,
    )

  @Bean("metalsCbrClient")
  @ConditionalOnMissingBean
  fun cbrMetalClient(
    cbrRestTemplate: RestTemplate,
    properties: CbrProperties,
    metalParser: MetalParser,
  ): CbrMetalClient =
    CbrMetalClient(
      cbrRestTemplate,
      properties,
      metalParser,
    )

  @Bean
  @ConditionalOnMissingBean
  fun cbrInterbankCreditMarketClient(
    cbrRestTemplate: RestTemplate,
    properties: CbrProperties,
    interbankCreditMarketParser: InterbankCreditMarketParser,
  ): CbrInterbankCreditMarketClient =
    CbrInterbankCreditMarketClient(
      cbrRestTemplate,
      properties,
      interbankCreditMarketParser,
    )
}
