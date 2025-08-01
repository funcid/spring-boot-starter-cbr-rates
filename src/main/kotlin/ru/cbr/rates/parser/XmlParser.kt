package ru.cbr.rates.parser

import org.w3c.dom.Element
import ru.cbr.rates.model.CharsetConstants.WINDOWS_1251
import ru.cbr.rates.model.CurrencyInfo
import ru.cbr.rates.model.CurrencyList
import ru.cbr.rates.model.CurrencyRate
import ru.cbr.rates.model.ExchangeRates
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

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

    private fun parseValute(valute: Element): CurrencyRate {
        return CurrencyRate(
            id = valute.getAttribute("ID"),
            numCode = getElementText(valute, "NumCode"),
            charCode = getElementText(valute, "CharCode"),
            nominal = getElementText(valute, "Nominal").toInt(),
            name = getElementText(valute, "Name"),
            value = parseDecimal(getElementText(valute, "Value")),
            vunitRate = parseDecimal(getElementText(valute, "VunitRate"))
        )
    }

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

    private fun parseCurrencyItem(item: Element): CurrencyInfo {
        return CurrencyInfo(
            id = item.getAttribute("ID"),
            name = getElementText(item, "Name"),
            engName = getElementText(item, "EngName"),
            nominal = getElementText(item, "Nominal").toInt(),
            parentCode = getElementText(item, "ParentCode"),
            isoNumCode = getElementText(item, "ISO_Num_Code"),
            isoCharCode = getElementText(item, "ISO_Char_Code")
        )
    }

    private fun getElementText(parent: Element, tagName: String): String {
        val nodeList = parent.getElementsByTagName(tagName)
        return if (nodeList.length > 0) {
            nodeList.item(0).textContent.trim()
        } else {
            ""
        }
    }

    private fun parseDecimal(value: String): BigDecimal {
        return BigDecimal(value.replace(",", "."))
    }
}