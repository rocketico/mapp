package io.rocketico.core.model.response

class TokenInfoFromMarket(val marketName: String,
                          val marketCapitalization : Long,
                          val lowestRate24h: Long,
                          val highestRate24h: Long,
                          val tradingVolume24h: Long) {
}
