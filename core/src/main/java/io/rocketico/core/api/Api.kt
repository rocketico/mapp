package io.rocketico.core.api

import io.rocketico.core.model.Rate
import retrofit2.Call
import retrofit2.http.GET

interface Api {
    @GET("gistfile1.txt")
    fun getRate(): Call<Rate>
}