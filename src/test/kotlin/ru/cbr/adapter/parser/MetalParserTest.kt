package ru.cbr.adapter.parser

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

class MetalParserTest {
  private val parser = MetalParser()

  @Test
  fun `parses metal prices with comma decimal separator`() {
    val metals = parser.parseMetalList(CbrTestResources.read("cbr/metals.xml"))

    assertEquals(1, metals.size)
    val gold = metals.first()
    assertEquals(LocalDate.of(2024, 6, 15), gold.date)
    assertEquals("1", gold.code)
    assertEquals(BigDecimal("7500.50"), gold.buy)
    assertEquals(BigDecimal("7600.00"), gold.sell)
  }
}
