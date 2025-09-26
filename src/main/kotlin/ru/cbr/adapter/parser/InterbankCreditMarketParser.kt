package ru.cbr.adapter.parser

import org.springframework.stereotype.Component
import org.w3c.dom.Element
import ru.cbr.adapter.model.InterbankCreditMarket

/**
 * Парсер для данных межбанковского кредитного рынка от ЦБ РФ
 */
@Component
class InterbankCreditMarketParser : AbstractXmlParser() {
  /**
   * Парсит XML с данными межбанковского кредитного рынка
   *
   * @param xml XML строка с данными межбанковского кредитного рынка
   * @return коллекция объектов InterbankCreditMarket
   */
  fun parseInterbankCreditMarket(xml: String): List<InterbankCreditMarket> {
    val document = parseXmlDocument(xml)
    val root = document.documentElement

    return parseInterbankCreditMarketItems(root)
  }

  private fun parseInterbankCreditMarketItems(root: Element): List<InterbankCreditMarket> {
    val interbankCreditMarkets = mutableListOf<InterbankCreditMarket>()
    val itemNodes = root.getElementsByTagName("Record")

    for (i in 0 until itemNodes.length) {
      val item = itemNodes.item(i) as Element
      val code = item.getAttribute("Code")

      // Фильтруем только записи с кодом "3" (межбанковский кредитный рынок)
      if (code == "3") {
        interbankCreditMarkets.add(parseInterbankCreditMarket(item))
      }
    }

    return interbankCreditMarkets
  }

  private fun parseInterbankCreditMarket(item: Element): InterbankCreditMarket =
    InterbankCreditMarket(
      date = parseDate(item.getAttribute("Date")),
      code = item.getAttribute("Code"),
      c1 = parseDecimal(getElementText(item, "C1")),
      c7 = parseDecimalOrNull(getElementText(item, "C7")),
      c30 = parseDecimalOrNull(getElementText(item, "C30")),
      c90 = parseDecimalOrNull(getElementText(item, "C90")),
      c180 = parseDecimalOrNull(getElementText(item, "C180")),
      c360 = parseDecimalOrNull(getElementText(item, "C360")),
    )
}
