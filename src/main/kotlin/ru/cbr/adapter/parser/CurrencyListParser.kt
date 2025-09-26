package ru.cbr.adapter.parser

import org.springframework.stereotype.Component
import org.w3c.dom.Element
import ru.cbr.adapter.model.CurrencyInfo
import ru.cbr.adapter.model.CurrencyList

/**
 * Парсер для списка валют от ЦБ РФ
 */
@Component
class CurrencyListParser : AbstractXmlParser() {
  /**
   * Парсит XML со списком валют
   *
   * @param xml XML строка со списком валют
   * @return объект CurrencyList с информацией о валютах
   */
  fun parseCurrencyList(xml: String): CurrencyList {
    val document = parseXmlDocument(xml)
    val root = document.documentElement

    val name = root.getAttribute("name")
    val currencies = parseCurrencyItems(root)

    return CurrencyList(name, currencies)
  }

  private fun parseCurrencyItems(root: Element): List<CurrencyInfo> {
    val currencies = mutableListOf<CurrencyInfo>()
    val itemNodes = root.getElementsByTagName("Item")

    for (i in 0 until itemNodes.length) {
      val item = itemNodes.item(i) as Element
      currencies.add(parseCurrencyItem(item))
    }

    return currencies
  }

  private fun parseCurrencyItem(item: Element): CurrencyInfo =
    CurrencyInfo(
      id = item.getAttribute("ID"),
      name = getElementText(item, "Name"),
      engName = getElementText(item, "EngName"),
      nominal = parseInt(getElementText(item, "Nominal")),
      parentCode = getElementText(item, "ParentCode"),
      isoNumCode = getElementText(item, "ISO_Num_Code"),
      isoCharCode = getElementText(item, "ISO_Char_Code"),
    )
}
