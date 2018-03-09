package io.rocketico.core.model.response

data class TokenRatesRangeResponse(
        val rates: List<RatesItem?>? = null,
        val currency: String? = null,
        val timestamp: Long? = null
) {
    data class RatesItem(
            val date: Long? = null,
            val values: List<ValuesItem?>? = null
    ) {
        data class ValuesItem(
                val tokenSymbol: String? = null,
                val rate: Double? = null
        )
    }
}