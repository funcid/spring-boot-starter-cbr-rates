package ru.cbr.adapter.parser

import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.SAXException
import ru.cbr.adapter.model.CharsetConstants.WINDOWS_1251
import java.io.IOException
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Базовый абстрактный класс для парсинга XML документов от ЦБ РФ
 */
abstract class AbstractXmlParser {
  protected val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
  protected val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

  /**
   * Парсит XML строку в DOM документ
   *
   * @param xml XML строка для парсинга
   * @return DOM документ
   * @throws XmlParsingException если не удалось распарсить XML
   */
  protected fun parseXmlDocument(xml: String): Document =
    try {
      documentBuilder.parse(xml.byteInputStream(WINDOWS_1251))
    } catch (e: SAXException) {
      throw XmlParsingException("Ошибка парсинга XML: ${e.message}", e)
    } catch (e: IOException) {
      throw XmlParsingException("Ошибка чтения XML: ${e.message}", e)
    } catch (e: DOMException) {
      throw XmlParsingException("Ошибка DOM: ${e.message}", e)
    }

  /**
   * Извлекает текст из элемента по имени тега
   *
   * @param parent родительский элемент
   * @param tagName имя тега
   * @return текст элемента или пустую строку, если элемент не найден
   */
  protected fun getElementText(
    parent: Element,
    tagName: String,
  ): String {
    val nodeList = parent.getElementsByTagName(tagName)
    return if (nodeList.length > 0) {
      nodeList.item(0).textContent?.trim() ?: ""
    } else {
      ""
    }
  }

  /**
   * Парсит строку в BigDecimal, заменяя запятую на точку
   *
   * @param value строка для парсинга
   * @return BigDecimal значение
   * @throws XmlParsingException если не удалось распарсить число
   */
  protected fun parseDecimal(value: String): BigDecimal =
    try {
      BigDecimal(value.replace(",", "."))
    } catch (e: NumberFormatException) {
      throw XmlParsingException("Не удалось распарсить число: '$value'", e)
    }

  /**
   * Парсит строку в BigDecimal или возвращает null для пустых значений
   *
   * @param value строка для парсинга
   * @return BigDecimal значение или null для пустых значений
   * @throws XmlParsingException если значение не пустое, но не является числом
   */
  protected fun parseDecimalOrNull(value: String): BigDecimal? =
    when {
      value == "-" || value.isEmpty() -> null
      else ->
        try {
          BigDecimal(value.replace(",", "."))
        } catch (e: NumberFormatException) {
          throw XmlParsingException("Не удалось распарсить число: '$value'", e)
        }
    }

  /**
   * Парсит дату из строки в формате dd.MM.yyyy
   *
   * @param dateString строка с датой
   * @return LocalDate объект
   * @throws XmlParsingException если не удалось распарсить дату
   */
  protected fun parseDate(dateString: String): LocalDate =
    try {
      LocalDate.parse(dateString, dateFormatter)
    } catch (e: DateTimeParseException) {
      throw XmlParsingException("Не удалось распарсить дату: '$dateString'", e)
    }

  /**
   * Парсит строку в Int
   *
   * @param value строка для парсинга
   * @return Int значение
   * @throws XmlParsingException если не удалось распарсить число
   */
  protected fun parseInt(value: String): Int =
    try {
      value.toInt()
    } catch (e: NumberFormatException) {
      throw XmlParsingException("Не удалось распарсить целое число: '$value'", e)
    }
}

/**
 * Исключение для ошибок парсинга XML
 */
class XmlParsingException(
  message: String,
  cause: Throwable? = null,
) : RuntimeException(message, cause)
