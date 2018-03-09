package io.rocketico.core.api

import io.rocketico.core.model.response.TokenRatesResponse
import retrofit2.Call
import retrofit2.http.GET

interface Api {
    @GET("https://gist.githubusercontent.com/KirillZholnerovich/c8f50d91479599e2ce79cd773e7c16e1/raw/462c2715eab236db5aceeff2094077bed91a0061/")
    fun getRate(): Call<TokenRatesResponse>
}