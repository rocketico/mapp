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

    @GET("https://gist.githubusercontent.com/uxname/9d964387dbc911826b880e5628c1a64a/raw/1707bdde659451afd1a6332fc3b10dd12cadd9ce/gistfile1.txt")
    fun getRatesByDateRange(
            @Query("fromDate") fromDate: Date,
            @Query("toDate") toDate: Date
    ): Call<TokenRatesRangeResponse>
}