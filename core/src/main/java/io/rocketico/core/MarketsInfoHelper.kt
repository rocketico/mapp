package io.rocketico.core

import io.rocketico.core.model.response.TokenInfoResponse

object MarketsInfoHelper {

    fun getTokenInfoFromMarkets(tokenType: String, currency: String): TokenInfoResponse? {
        return Utils.api.getTokenInfo(tokenType, currency).execute().body()
    }

}