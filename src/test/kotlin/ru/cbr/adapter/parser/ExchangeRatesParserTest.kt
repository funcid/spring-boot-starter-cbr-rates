package ru.cbr.adapter.parser

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

class ExchangeRatesParserTest {
  private val parser = ExchangeRatesParser()

  @Test
  fun `parses exchange rates from val curs xml`() {
    val exchangeRates = parser.parseExchangeRates(CbrTestResources.read("cbr/exchange-rates.xml"))

    assertEquals(LocalDate.of(2002, 3, 2), exchangeRates.date)
    assertEquals("Foreign Currency Market", exchangeRates.name)
    assertEquals(1, exchangeRates.rates.size)

    val usd = exchangeRates.rates.first()
    assertEquals("R01235", usd.id)
    assertEquals("840", usd.numCode)
    assertEquals("USD", usd.charCode)
    assertEquals(1, usd.nominal)
    assertEquals("Доллар США", usd.name)
    assertEquals(BigDecimal("30.9436"), usd.value)
    assertEquals(BigDecimal("30.9436"), usd.vunitRate)
  }
}
