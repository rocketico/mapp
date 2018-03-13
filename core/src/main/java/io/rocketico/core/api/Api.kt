package io.rocketico.core.api

import io.rocketico.core.model.response.TokenHistoryResponse
import io.rocketico.core.model.response.TokenInfoFromMarket
import io.rocketico.core.model.response.TokenRatesRangeResponse
import io.rocketico.core.model.response.TokensRatesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface Api {
    @GET("https://gist.githubusercontent.com/KirillZholnerovich/c8f50d91479599e2ce79cd773e7c16e1/raw/0b9c708ecc61edf0e225200ce2fdaa6ceecae0da/gistfile1.txt")
    fun getRatesByDate(
            @Query("date") date: Date
    ): Call<TokensRatesResponse>

    @GET("https://gist.githubusercontent.com/uxname/bd779159538c914ab8563241d5c37858/raw/acd09e0b05f025444578af807370001e79b99d09/gistfile1.txt")
    fun getRatesByDateRange(
            @Query("fromDate") fromDate: Date,
            @Query("toDate") toDate: Date
    ): Call<TokenRatesRangeResponse>

    @GET("https://gist.githubusercontent.com/KirillZholnerovich/2223525229c2de361bb37f347b5c33d9/raw/28c5fa1c14618e63aea250d03a3a02860c276823/gistfile1.txt")
    fun getTokenInfo(@Query("tokenType") tokenType: String,
                     @Query("currency") currency: String
    ): Call<List<TokenInfoFromMarket>>

    @GET("https://gist.githubusercontent.com/uxname/1c6689cac6b8bb69e39ed4aeeb8280de/raw/435216688cbe9430959bc91cafffe374cf63feb9/gistfile1.txt")
    fun getTokensHistory(
            @Query("dateFrom") dateFrom: Date,
            @Query("dateTo") dateTo: Date,
            @Query("tokenTypeList") tokenTypeList: List<String>
    ): Call<List<TokenHistoryResponse>>
}