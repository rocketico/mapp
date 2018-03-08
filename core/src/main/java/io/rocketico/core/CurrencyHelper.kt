package io.rocketico.core

class CurrencyHelper {

    class CurrencyResponse(val currency: String,
                           val rates: List<Rate>,
                           val timestamp: Long) {

        class Rate(val tokenSymbol: String,
                   val rate: Float)
    }

    enum class Tokens(private val value: String) {
        ETH("ETH");

        override fun toString(): String {
            return value
        }
    }
}
