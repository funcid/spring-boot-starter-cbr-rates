package ru.cbr.rates.client

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import ru.cbr.rates.config.CbrRatesProperties
import ru.cbr.rates.model.CharsetConstants.WINDOWS_1251
import ru.cbr.rates.model.CurrencyList
import ru.cbr.rates.model.CurrencyRate
import ru.cbr.rates.model.ExchangeRates
import ru.cbr.rates.parser.XmlParser
import java.time.LocalDate
import java.util.Currency

class CbrRatesClient(
  private val restTemplate: RestTemplate,
  private val properties: CbrRatesProperties,
) {
  private val xmlParser = XmlParser()

  /**
   * Получить все текущие курсы валют
   */
  fun getCurrentRates(): ExchangeRates = getRatesForDate(LocalDate.now())

  /**
   * Получить курсы валют на определенную дату
   */
  fun getRatesForDate(date: LocalDate): ExchangeRates {
    val url = "${properties.baseUrl}/XML_daily.asp?date_req=${formatDate(date)}"
    val xml = fetchXmlWithCorrectEncoding(url)
    return xmlParser.parseExchangeRates(xml)
  }

  /**
   * Получить курс конкретной валюты по java.util.Currency
   */
  fun getCurrencyRate(currencyCode: String): CurrencyRate? {
    val rates = getCurrentRates()
    return rates.rates.find { it.charCode == currencyCode }
  }

  /**
   * Получить курс конкретной валюты по java.util.Currency
   */
  fun getCurrencyRate(currency: Currency): CurrencyRate? {
    val rates = getCurrentRates()
    return rates.rates.find { it.charCode == currency.currencyCode }
  }

  /**
   * Получить полный список доступных валют
   */
  fun getCurrencyList(): CurrencyList {
    val url = "${properties.baseUrl}/XML_valFull.asp"
    val xml = fetchXmlWithCorrectEncoding(url)
    return xmlParser.parseCurrencyList(xml)
  }

  /**
   * Получить коды валют для текущих курсов
   */
  fun getCurrencies(): List<String> = getCurrencyList().currencies.map { it.isoCharCode }

  private fun fetchXmlWithCorrectEncoding(url: String): String {
    val headers = HttpHeaders()
    headers.accept = listOf(MediaType.APPLICATION_XML)
    val entity = HttpEntity<String>(headers)

    val response = restTemplate.exchange(url, HttpMethod.GET, entity, ByteArray::class.java)
    val bytes = response.body ?: byteArrayOf()

    return String(bytes, WINDOWS_1251)
  }

  private fun formatDate(date: LocalDate): String =
    "${date.dayOfMonth.toString().padStart(2, '0')}/" +
      "${date.monthValue.toString().padStart(2, '0')}/" +
      "${date.year}"
}
