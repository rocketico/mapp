package io.rocketico.core.model.response

import java.util.*

data class TokenRatesRangeResponse(
        val rates: List<RatesItem?>? = null,
        val currency: String? = null
) {
    data class RatesItem(
            val date: Date? = null,
            var values: List<ValuesItem?>? = null
    ) {
        data class ValuesItem(
                val tokenSymbol: String? = null,
                val volume: Float? = null,
                val rate: Float? = null
        )
    }
}