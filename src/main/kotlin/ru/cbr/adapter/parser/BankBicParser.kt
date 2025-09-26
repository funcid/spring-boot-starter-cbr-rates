package ru.cbr.adapter.parser

import org.springframework.stereotype.Component
import org.w3c.dom.Element
import ru.cbr.adapter.model.BankBic

/**
 * Парсер для данных о банках и их БИК от ЦБ РФ
 */
@Component
class BankBicParser : AbstractXmlParser() {
  /**
   * Парсит XML с данными о банках и их БИК
   *
   * @param xml XML строка с данными о банках
   * @return коллекция объектов BankBic
   */
  fun parseBankBicList(xml: String): List<BankBic> {
    val document = parseXmlDocument(xml)
    val root = document.documentElement

    return parseBankBicItems(root)
  }

  private fun parseBankBicItems(root: Element): List<BankBic> {
    val bankBics = mutableListOf<BankBic>()
    val itemNodes = root.getElementsByTagName("Record")

    for (i in 0 until itemNodes.length) {
      val item = itemNodes.item(i) as Element
      bankBics.add(parseBankBic(item))
    }

    return bankBics
  }

  private fun parseBankBic(item: Element): BankBic =
    BankBic(
      id = item.getAttribute("ID"),
      du = parseDate(item.getAttribute("DU")),
      shortName = getElementText(item, "ShortName"),
      bic = parseInt(getElementText(item, "Bic")),
    )
}
