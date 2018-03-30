package io.rocketico.core.model.response

class TokenInfoResponse(val currency: String,
                        val marketsInfo: List<TokenInfoFromMarket>) {

    class TokenInfoFromMarket(val marketName: String = "Not available",
                              val exchange: Float = 0f,
                              val marketCapitalization : Float = 0f,
                              val lowestRate24h: Float = 0f,
                              val highestRate24h: Float = 0f,
                              val traidingVolume24h: Float = 0f)
}
