package ru.cbr.adapter.model

import java.time.LocalDate

data class BankBic(
  val id: String,
  val du: LocalDate,
  val shortName: String,
  val bic: Int,
)
