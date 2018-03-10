package io.rocketico.core

import io.rocketico.core.model.response.TokensRatesResponse
import io.rocketico.core.model.response.TokenRatesRangeResponse
import java.util.*

object RateHelper {
    fun getTokenRateByDate(date: Date = Date()): TokensRatesResponse? {
        return Utils.api.getRatesByDate(date).execute().body()
    }

    fun getTokenRatesByRange(from: Date, to: Date = Date()): TokenRatesRangeResponse? {
        return Utils.api.getRatesByDateRange(from, to).execute().body()
    }
}