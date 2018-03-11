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

    @GET("https://gist.githubusercontent.com/uxname/6180e4558f743eb4ee68e8358aa82c09/raw/3e08cf111c31c794062195123bec76214958e6cf/gistfile1.txt")
    fun getRatesByDateRange(
            @Query("fromDate") fromDate: Date,
            @Query("toDate") toDate: Date
    ): Call<TokenRatesRangeResponse>
}