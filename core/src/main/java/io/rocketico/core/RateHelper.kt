package io.rocketico.core

import io.rocketico.core.CurrencyHelper.CurrencyResponse

object RateHelper {

    fun getRates(): CurrencyResponse? {
        return Utils.api.getRate().execute().body()
    }
}