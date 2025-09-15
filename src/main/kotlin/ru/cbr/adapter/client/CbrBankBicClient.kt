package ru.cbr.adapter.client

import org.springframework.web.client.RestTemplate
import ru.cbr.adapter.config.CbrProperties
import ru.cbr.adapter.parser.XmlParser
import ru.cbr.adapter.util.fetchXmlWithCorrectEncoding

class CbrBankBicClient(
  private val restTemplate: RestTemplate,
  private val properties: CbrProperties,
  private val xmlParser: XmlParser
) {

  /**
   * Получить полный список банков
   */
  fun getBankBicList() = xmlParser.parseBankBicList(
    restTemplate.fetchXmlWithCorrectEncoding(
      url = "${properties.baseUrl}/XML_bic.asp"
    )
  )
}


