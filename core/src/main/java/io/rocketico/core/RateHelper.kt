package io.rocketico.core

import io.rocketico.core.model.Rate

object RateHelper {

    fun getRate(): Rate? {
        return Utils.api.getRate().execute().body()
    }
}