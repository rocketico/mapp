package io.rocketico.core.model.response

class TokenInfoFromMarket(val marketName: String,
                          val currency: String,
                          val marketCapitalization : Float,
                          val lowestRate24h: Float,
                          val highestRate24h: Float,
                          val tradingVolume24h: Float)
