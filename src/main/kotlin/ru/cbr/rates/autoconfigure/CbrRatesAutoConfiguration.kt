package ru.cbr.rates.autoconfigure

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import ru.cbr.rates.client.CbrRatesClient
import ru.cbr.rates.config.CbrRatesProperties
import java.time.Duration

@AutoConfiguration
@ConditionalOnClass(RestTemplate::class)
@ConditionalOnProperty(prefix = "cbr.rates", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CbrRatesProperties::class)
class CbrRatesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun cbrRatesRestTemplate(properties: CbrRatesProperties): RestTemplate {
        return RestTemplate().apply {
            requestFactory = org.springframework.http.client.SimpleClientHttpRequestFactory().apply {
                setConnectTimeout(Duration.ofMillis(properties.connectTimeout))
                setReadTimeout(Duration.ofMillis(properties.readTimeout))
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun cbrRatesClient(
        cbrRatesRestTemplate: RestTemplate,
        properties: CbrRatesProperties
    ): CbrRatesClient {
        return CbrRatesClient(cbrRatesRestTemplate, properties)
    }
}