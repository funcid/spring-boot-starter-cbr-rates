package ru.cbr.adapter.parser

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InterbankCreditMarketParserTest {
  private val parser = InterbankCreditMarketParser()

  @Test
  fun `parses only records with code 3`() {
    val records = parser.parseInterbankCreditMarket(CbrTestResources.read("cbr/interbank-credit-market.xml"))

    assertEquals(1, records.size)
    val record = records.first()
    assertEquals(LocalDate.of(2024, 6, 15), record.date)
    assertEquals("3", record.code)
    assertEquals(BigDecimal("16.00"), record.c1)
    assertEquals(BigDecimal("16.10"), record.c7)
    assertNull(record.c30)
    assertNull(record.c90)
    assertEquals(BigDecimal("16.50"), record.c180)
    assertNull(record.c360)
  }
}
