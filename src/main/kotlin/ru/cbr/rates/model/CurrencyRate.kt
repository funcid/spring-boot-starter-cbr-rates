package ru.cbr.rates.model

import java.math.BigDecimal
import java.time.LocalDate

data class CurrencyRate(
    val id: String,
    val numCode: String,
    val charCode: String,
    val nominal: Int,
    val name: String,
    val value: BigDecimal,
    val vunitRate: BigDecimal
)

data class ExchangeRates(
    val date: LocalDate,
    val name: String,
    val rates: List<CurrencyRate>
)

data class CurrencyInfo(
    val id: String,
    val name: String,
    val engName: String,
    val nominal: Int,
    val parentCode: String,
    val isoNumCode: String,
    val isoCharCode: String
)

data class CurrencyList(
    val name: String,
    val currencies: List<CurrencyInfo>
)