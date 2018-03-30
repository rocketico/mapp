package io.rocketico.core.model.response

class TokenInfoResponse(val currency: String,
                        val marketsInfo: List<TokenInfoFromMarket>) {

    class TokenInfoFromMarket(val marketName: String,
                              val exchange: Float,
                              val marketCapitalization : Float,
                              val lowestRate24h: Float,
                              val highestRate24h: Float,
                              val tradingVolume24h: Float)
}
