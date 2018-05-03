package io.rocketico.core

import io.rocketico.core.model.response.TokenInfoResponse

object MarketsInfoHelper {

    fun getTokenInfoFromMarkets(tokenType: String, currency: String): TokenInfoResponse? {
        //todo fix me
        val call = Utils.api.getTokenInfo(tokenType, currency)
        println(call.request().url())
        return call.execute().body()
    }

}