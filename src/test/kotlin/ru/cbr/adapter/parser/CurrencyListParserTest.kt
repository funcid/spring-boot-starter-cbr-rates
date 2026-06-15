package ru.cbr.adapter.parser

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CurrencyListParserTest {
  private val parser = CurrencyListParser()

  @Test
  fun `parses currency list items`() {
    val currencyList = parser.parseCurrencyList(CbrTestResources.read("cbr/currency-list.xml"))

    assertEquals("Foreign Currency List", currencyList.name)
    assertEquals(1, currencyList.currencies.size)

    val gbp = currencyList.currencies.first()
    assertEquals("R01035", gbp.id)
    assertEquals("Фунт стерлингов", gbp.name)
    assertEquals("Pound Sterling", gbp.engName)
    assertEquals(1, gbp.nominal)
    assertEquals("0", gbp.parentCode)
    assertEquals("826", gbp.isoNumCode)
    assertEquals("GBP", gbp.isoCharCode)
  }
}
