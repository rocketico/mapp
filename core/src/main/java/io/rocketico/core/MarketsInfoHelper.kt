package io.rocketico.core

import io.rocketico.core.model.response.TokenInfoFromMarket
import java.util.*

object MarketsInfoHelper {

    fun getTokenInfoFromMarkets(tokenType: String, currency: String): List<TokenInfoFromMarket>? {
        return Utils.api.getTokenInfo(tokenType, currency).execute().body()
    }

}