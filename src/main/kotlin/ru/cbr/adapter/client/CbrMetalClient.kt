package ru.cbr.adapter.client

import org.springframework.web.client.RestTemplate
import ru.cbr.adapter.configuration.CbrProperties
import ru.cbr.adapter.model.Metal
import ru.cbr.adapter.parser.MetalParser
import ru.cbr.adapter.util.fetchXmlWithCorrectEncoding
import ru.cbr.adapter.util.formatDate
import java.time.LocalDate

class CbrMetalClient(
  private val restTemplate: RestTemplate,
  private val properties: CbrProperties,
  private val metalParser: MetalParser,
) {
  /**
   * Получить полный список металлов
   */
  fun getMetalList(): Collection<Metal> {
    // CBR skips weekends/holidays; a short window can be empty around long breaks.
    val from = formatDate(LocalDate.now().minusDays(14))
    val to = formatDate(LocalDate.now())
    return metalParser.parseMetalList(
      restTemplate.fetchXmlWithCorrectEncoding(
        url = "${properties.baseUrl}/xml_metall.asp?date_req1=$from&date_req2=$to",
      ),
    )
  }
}
