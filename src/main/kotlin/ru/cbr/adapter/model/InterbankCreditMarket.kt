package ru.cbr.adapter.model

import java.math.BigDecimal
import java.time.LocalDate

data class InterbankCreditMarket(
  val date: LocalDate,
  val code: String,
  val c1: BigDecimal,
  val c7: BigDecimal?,
  val c30: BigDecimal?,
  val c90: BigDecimal?,
  val c180: BigDecimal?,
  val c360: BigDecimal?,
)
