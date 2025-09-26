package ru.cbr.adapter.parser

import org.springframework.stereotype.Component
import org.w3c.dom.Element
import ru.cbr.adapter.model.Metal

/**
 * Парсер для данных о драгоценных металлах от ЦБ РФ
 */
@Component
class MetalParser : AbstractXmlParser() {
  /**
   * Парсит XML с данными о драгоценных металлах
   *
   * @param xml XML строка с данными о металлах
   * @return коллекция объектов Metal
   */
  fun parseMetalList(xml: String): List<Metal> {
    val document = parseXmlDocument(xml)
    val root = document.documentElement

    return parseMetalItems(root)
  }

  private fun parseMetalItems(root: Element): List<Metal> {
    val metals = mutableListOf<Metal>()
    val itemNodes = root.getElementsByTagName("Record")

    for (i in 0 until itemNodes.length) {
      val item = itemNodes.item(i) as Element
      metals.add(parseMetal(item))
    }

    return metals
  }

  private fun parseMetal(item: Element): Metal =
    Metal(
      date = parseDate(item.getAttribute("Date")),
      code = getElementText(item, "Code"),
      buy = parseDecimal(getElementText(item, "Buy")),
      sell = parseDecimal(getElementText(item, "Sell")),
    )
}
