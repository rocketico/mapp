package io.rocketico.core.model.response

class CurrencyResponse(val currency: String,
                       val rates: List<Rate>,
                       val timestamp: Long) {

    class Rate(val tokenSymbol: String,
               val rate: Float)
}