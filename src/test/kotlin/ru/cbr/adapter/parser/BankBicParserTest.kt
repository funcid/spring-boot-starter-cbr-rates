package ru.cbr.adapter.parser

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class BankBicParserTest {
  private val parser = BankBicParser()

  @Test
  fun `parses bank bic records`() {
    val banks = parser.parseBankBicList(CbrTestResources.read("cbr/bank-bic.xml"))

    assertEquals(1, banks.size)
    val bank = banks.first()
    assertEquals("123", bank.id)
    assertEquals(LocalDate.of(2024, 6, 15), bank.du)
    assertEquals("Сбербанк", bank.shortName)
    assertEquals(44525225, bank.bic)
  }
}
