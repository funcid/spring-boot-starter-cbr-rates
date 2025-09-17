package ru.cbr.adapter.client

import org.springframework.web.client.RestTemplate
import ru.cbr.adapter.config.CbrProperties
import ru.cbr.adapter.model.ExchangeRates
import ru.cbr.adapter.parser.XmlParser
import java.time.LocalDate
import java.util.Currency
import ru.cbr.adapter.util.fetchXmlWithCorrectEncoding
import ru.cbr.adapter.util.formatDate

class CbrRatesClient(
  private val restTemplate: RestTemplate,
  private val properties: CbrProperties,
  private val xmlParser: XmlParser
) {

  /**
   * Получить все текущие курсы валют
   */
  fun getCurrentRates(): ExchangeRates = getRatesForDate(LocalDate.now())

  /**
   * Получить курсы валют на определенную дату
   */
  fun getRatesForDate(date: LocalDate) = xmlParser.parseExchangeRates(
    restTemplate.fetchXmlWithCorrectEncoding(
      url = "${properties.baseUrl}/XML_daily.asp?date_req=${formatDate(date)}"
    )
  )

  /**
   * Получить курс конкретной валюты по java.util.Currency
   */
  fun getCurrencyRate(currencyCode: String) = getCurrentRates().rates.find { it.charCode == currencyCode }

  /**
   * Получить курс конкретной валюты по java.util.Currency
   */
  fun getCurrencyRate(currency: Currency) = getCurrentRates().rates.find { it.charCode == currency.currencyCode }

  /**
   * Получить полный список доступных валют
   */
  fun getCurrencyList() = xmlParser.parseCurrencyList(
    restTemplate.fetchXmlWithCorrectEncoding(
      url = "${properties.baseUrl}/XML_valFull.asp"
    )
  )

  /**
   * Получить коды валют для текущих курсов
   */
  fun getCurrencies(): List<String> = getCurrencyList().currencies.map { it.isoCharCode }
}


