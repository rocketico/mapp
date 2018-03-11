package io.rocketico.core

import io.rocketico.core.model.response.TokenInfoFromMarket

object MarketsInfoHelper {

    fun getTokenInfoFromMarkets(tokenType: String): List<TokenInfoFromMarket>? {
        return Utils.api.getTokenInfo(tokenType).execute().body()
    }

}