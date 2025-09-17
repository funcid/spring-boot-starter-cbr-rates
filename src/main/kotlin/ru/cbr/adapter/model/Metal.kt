package ru.cbr.adapter.model

import java.math.BigDecimal
import java.time.LocalDate

data class Metal(
  val date: LocalDate,
  val code: String,
  val buy: BigDecimal,
  val sell: BigDecimal,
)
