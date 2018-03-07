package io.rocketico.core

import io.rocketico.core.CurrencyHelper.Response

object RateHelper {

    fun getRate(): Response? {
        return Utils.api.getRate().execute().body()
    }
}