package ru.cbr.adapter.client

import org.springframework.web.client.RestTemplate
import ru.cbr.adapter.config.CbrProperties
import ru.cbr.adapter.parser.BankBicParser
import ru.cbr.adapter.util.fetchXmlWithCorrectEncoding

class CbrBankBicClient(
  private val restTemplate: RestTemplate,
  private val properties: CbrProperties,
  private val bankBicParser: BankBicParser,
) {
  /**
   * Получить полный список банков
   */
  fun getBankBicList() =
    bankBicParser.parseBankBicList(
      restTemplate.fetchXmlWithCorrectEncoding(
        url = "${properties.baseUrl}/XML_bic.asp",
      ),
    )
}
