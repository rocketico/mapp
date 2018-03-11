package io.rocketico.core.model.response

class TokenInfoFromMarket(val marketName: String,
                          val marketCapitalization : List<ValueItem>,
                          val lowestRate24h: List<ValueItem>,
                          val highestRate24h: List<ValueItem>,
                          val tradingVolume24h: List<ValueItem>) {

    class ValueItem(val currency: String,
                    val value: Long)
}
