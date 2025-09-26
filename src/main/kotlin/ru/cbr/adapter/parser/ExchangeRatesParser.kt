package ru.cbr.adapter.parser

import org.springframework.stereotype.Component
import org.w3c.dom.Element
import ru.cbr.adapter.model.CurrencyRate
import ru.cbr.adapter.model.ExchangeRates

/**
 * Парсер для курсов валют от ЦБ РФ
 */
@Component
class ExchangeRatesParser : AbstractXmlParser() {
  /**
   * Парсит XML с курсами валют
   *
   * @param xml XML строка с курсами валют
   * @return объект ExchangeRates с курсами валют
   */
  fun parseExchangeRates(xml: String): ExchangeRates {
    val document = parseXmlDocument(xml)
    val root = document.documentElement

    val date = parseDate(root.getAttribute("Date"))
    val name = root.getAttribute("name")

    val rates = parseCurrencyRates(root)

    return ExchangeRates(date, name, rates)
  }

  private fun parseCurrencyRates(root: Element): List<CurrencyRate> {
    val rates = mutableListOf<CurrencyRate>()
    val valuteNodes = root.getElementsByTagName("Valute")

    for (i in 0 until valuteNodes.length) {
      val valute = valuteNodes.item(i) as Element
      rates.add(parseCurrencyRate(valute))
    }

    return rates
  }

  private fun parseCurrencyRate(valute: Element): CurrencyRate =
    CurrencyRate(
      id = valute.getAttribute("ID"),
      numCode = getElementText(valute, "NumCode"),
      charCode = getElementText(valute, "CharCode"),
      nominal = parseInt(getElementText(valute, "Nominal")),
      name = getElementText(valute, "Name"),
      value = parseDecimal(getElementText(valute, "Value")),
      vunitRate = parseDecimal(getElementText(valute, "VunitRate")),
    )
}
