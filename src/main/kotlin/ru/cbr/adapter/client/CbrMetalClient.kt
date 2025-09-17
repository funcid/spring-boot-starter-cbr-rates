package ru.cbr.adapter.client

import org.springframework.web.client.RestTemplate
import ru.cbr.adapter.config.CbrProperties
import ru.cbr.adapter.model.Metal
import ru.cbr.adapter.parser.XmlParser
import ru.cbr.adapter.util.fetchXmlWithCorrectEncoding
import ru.cbr.adapter.util.formatDate
import java.time.LocalDate

class CbrMetalClient(
  private val restTemplate: RestTemplate,
  private val properties: CbrProperties,
  private val xmlParser: XmlParser
) {

  /**
   * Получить полный список металлов
   */
  fun getMetalList(): Collection<Metal> {
    val formatDate = formatDate(LocalDate.now())
    return xmlParser.parseMetalList(
      restTemplate.fetchXmlWithCorrectEncoding(
        url = "${properties.baseUrl}/xml_metall.asp?date_req1=$formatDate&date_req2=$formatDate"
      )
    )
  }
}
