package io.rocketico.core

import io.rocketico.core.model.response.TokenRatesResponse

object RateHelper {

    fun getLastTokenRates(): TokenRatesResponse? {
        return Utils.api.getRate().execute().body()
    }
}