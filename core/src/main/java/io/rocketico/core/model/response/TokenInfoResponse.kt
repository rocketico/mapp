package io.rocketico.core.model.response

class TokenInfoResponse(val currency: String,
                        val marketsInfo: List<TokenInfoFromMarket>) {

    class TokenInfoFromMarket(val marketName: String = "Not available",
                              val exchange: Float? = null,
                              val marketCapitalization: Float? = null,
                              val lowestRate24h: Float? = null,
                              val highestRate24h: Float? = null,
                              val traidingVolume24h: Float? = null)
}
