package ru.cbr.adapter.parser

import org.w3c.dom.Element
import ru.cbr.adapter.model.CharsetConstants.WINDOWS_1251
import ru.cbr.adapter.model.CurrencyInfo
import ru.cbr.adapter.model.CurrencyList
import ru.cbr.adapter.model.CurrencyRate
import ru.cbr.adapter.model.ExchangeRates
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory
import org.springframework.stereotype.Service
import ru.cbr.adapter.model.BankBic

@Service
class XmlParser {
  private val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
  private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

  fun parseExchangeRates(xml: String): ExchangeRates {
    val document = documentBuilder.parse(xml.byteInputStream(WINDOWS_1251))
    val root = document.documentElement

    val date = LocalDate.parse(root.getAttribute("Date"), dateFormatter)
    val name = root.getAttribute("name")

    val rates = mutableListOf<CurrencyRate>()
    val valuteNodes = root.getElementsByTagName("Valute")

    for (i in 0 until valuteNodes.length) {
      val valute = valuteNodes.item(i) as Element
      rates.add(parseValute(valute))
    }

    return ExchangeRates(date, name, rates)
  }

  private fun parseValute(valute: Element): CurrencyRate =
    CurrencyRate(
      id = valute.getAttribute("ID"),
      numCode = getElementText(valute, "NumCode"),
      charCode = getElementText(valute, "CharCode"),
      nominal = getElementText(valute, "Nominal").toInt(),
      name = getElementText(valute, "Name"),
      value = parseDecimal(getElementText(valute, "Value")),
      vunitRate = parseDecimal(getElementText(valute, "VunitRate")),
    )

  fun parseCurrencyList(xml: String): CurrencyList {
    val document = documentBuilder.parse(xml.byteInputStream(WINDOWS_1251))
    val root = document.documentElement

    val name = root.getAttribute("name")
    val currencies = mutableListOf<CurrencyInfo>()
    val itemNodes = root.getElementsByTagName("Item")

    for (i in 0 until itemNodes.length) {
      val item = itemNodes.item(i) as Element
      currencies.add(parseCurrencyItem(item))
    }

    return CurrencyList(name, currencies)
  }

  fun parseBankBicList(xml: String): Collection<BankBic> {
    val document = documentBuilder.parse(xml.byteInputStream(WINDOWS_1251))
    val root = document.documentElement

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
      du = LocalDate.parse(item.getAttribute("DU"), dateFormatter),
      shortName = getElementText(item, "ShortName"),
      bic = getElementText(item, "Bic").toInt(),
    )

  private fun parseCurrencyItem(item: Element): CurrencyInfo =
    CurrencyInfo(
      id = item.getAttribute("ID"),
      name = getElementText(item, "Name"),
      engName = getElementText(item, "EngName"),
      nominal = getElementText(item, "Nominal").toInt(),
      parentCode = getElementText(item, "ParentCode"),
      isoNumCode = getElementText(item, "ISO_Num_Code"),
      isoCharCode = getElementText(item, "ISO_Char_Code"),
    )

  private fun getElementText(
    parent: Element,
    tagName: String,
  ): String {
    val nodeList = parent.getElementsByTagName(tagName)
    return if (nodeList.length > 0) {
      nodeList.item(0).textContent.trim()
    } else {
      ""
    }
  }

  private fun parseDecimal(value: String): BigDecimal = BigDecimal(value.replace(",", "."))
}
