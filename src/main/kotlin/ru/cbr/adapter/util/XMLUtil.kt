package ru.cbr.adapter.util

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import ru.cbr.adapter.model.CharsetConstants.WINDOWS_1251
import java.time.LocalDate

fun RestTemplate.fetchXmlWithCorrectEncoding(url: String): String {
  val headers = HttpHeaders()
  headers.accept = listOf(MediaType.APPLICATION_XML)
  val entity = HttpEntity<String>(headers)

  val response = exchange(url, HttpMethod.GET, entity, ByteArray::class.java)
  val bytes = response.body ?: byteArrayOf()

  return String(bytes, WINDOWS_1251)
}

fun formatDate(date: LocalDate): String =
  "${date.dayOfMonth.toString().padStart(2, '0')}/" +
    "${date.monthValue.toString().padStart(2, '0')}/" +
    "${date.year}"
