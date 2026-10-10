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

    val allMetals = parseMetalItems(root)

    return allMetals
      .groupBy { it.code }
      .mapValues { (_, metals) -> metals.maxByOrNull { it.date } }
      .values
      .filterNotNull()
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
      // Current CBR xml_metall.asp puts Code on the Record; older dumps used a child <Code>.
      code = item.getAttribute("Code").ifBlank { getElementText(item, "Code") },
      buy = parseDecimal(getElementText(item, "Buy")),
      sell = parseDecimal(getElementText(item, "Sell")),
    )
}
