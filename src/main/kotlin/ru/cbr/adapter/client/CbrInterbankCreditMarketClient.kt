package ru.cbr.adapter.client

import org.springframework.web.client.RestTemplate
import ru.cbr.adapter.config.CbrProperties
import ru.cbr.adapter.model.InterbankCreditMarket
import ru.cbr.adapter.parser.InterbankCreditMarketParser
import ru.cbr.adapter.util.fetchXmlWithCorrectEncoding
import ru.cbr.adapter.util.formatDate
import java.time.LocalDate

class CbrInterbankCreditMarketClient(
  private val restTemplate: RestTemplate,
  private val properties: CbrProperties,
  private val interbankCreditMarketParser: InterbankCreditMarketParser,
) {
  /**
   * Получить данные кредитных рынков за месяц
   */
  fun getInterbankCreditMarketList(
    year: Int,
    month: Int,
  ): Collection<InterbankCreditMarket> {
    val startDate = LocalDate.of(year, month, 1)
    val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())

    val startDateFormatted = formatDate(startDate)
    val endDateFormatted = formatDate(endDate)

    return interbankCreditMarketParser.parseInterbankCreditMarket(
      restTemplate.fetchXmlWithCorrectEncoding(
        url = "${properties.baseUrl}/xml_mkr.asp?date_req1=$startDateFormatted&date_req2=$endDateFormatted",
      ),
    )
  }
}
