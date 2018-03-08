package io.rocketico.core

import io.rocketico.core.model.response.CurrencyResponse

object RateHelper {

    fun getRates(): CurrencyResponse? {
        return Utils.api.getRate().execute().body()
    }
}