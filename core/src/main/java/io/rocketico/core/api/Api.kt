package io.rocketico.core.api

import io.rocketico.core.model.response.TokensRatesResponse
import io.rocketico.core.model.response.TokenRatesRangeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface Api {
    @GET("https://gist.githubusercontent.com/KirillZholnerovich/c8f50d91479599e2ce79cd773e7c16e1/raw/0b9c708ecc61edf0e225200ce2fdaa6ceecae0da/gistfile1.txt")
    fun getRatesByDate(
            @Query("date") date: Date
    ): Call<TokensRatesResponse>

    @GET("https://gist.githubusercontent.com/uxname/8aa54537ea27bead85e14bd160a06b78/raw/70edf54b0a3352ee80ed31ecaa8787deadbb97eb/gistfile1.txt")
    fun getRatesByDateRange(
            @Query("fromDate") fromDate: Date,
            @Query("toDate") toDate: Date
    ): Call<TokenRatesRangeResponse>
}