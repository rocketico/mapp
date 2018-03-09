package io.rocketico.core.api

import io.rocketico.core.model.response.TokenLastRatesResponse
import io.rocketico.core.model.response.TokenRatesRangeResponse
import retrofit2.Call
import retrofit2.http.GET
import java.util.*

interface Api {
    @GET("https://gist.githubusercontent.com/KirillZholnerovich/c8f50d91479599e2ce79cd773e7c16e1/raw/462c2715eab236db5aceeff2094077bed91a0061/")
    fun getRate(): Call<TokenLastRatesResponse>

    @GET("https://gist.githubusercontent.com/uxname/a79dac421b7ffcb14a18c7bb6464f396/raw/0f4ae51a444535aece940e65696cc44e7c630bc9/gistfile1.txt")
    fun getRate(from: Date, to: Date): Call<TokenRatesRangeResponse>
}