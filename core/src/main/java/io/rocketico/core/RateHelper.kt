package io.rocketico.core

import io.rocketico.core.model.response.TokenLastRatesResponse
import io.rocketico.core.model.response.TokenRatesRangeResponse
import java.util.*

object RateHelper {

    fun getLastTokenRates(): TokenLastRatesResponse? {
        return Utils.api.getRate().execute().body()
    }

    fun getTokenRatesRange(from: Date, to: Date = Date()): TokenRatesRangeResponse? {
        return Utils.api.getRate(from, to).execute().body()
    }

}