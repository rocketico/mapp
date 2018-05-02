package io.rocketico.core.api

import io.rocketico.core.model.response.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface Api {
    @GET("rates-by-date")
    fun getRatesByDate(
            @Query("tokenList") tokenTypeList: List<String>,
            @Query("date") date: Date
    ): Call<TokensRatesResponse>

    @GET("rates-by-range")
    fun getRatesByDateRange(
            @Query("tokenList") tokenTypeList: List<String>,
            @Query("fromDate") fromDate: Date,
            @Query("toDate") toDate: Date
    ): Call<TokenRatesRangeResponse>

    @GET("token/info")
    fun getTokenInfo(@Query("tokenType") tokenType: String,
                     @Query("currency") currency: String
    ): Call<TokenInfoResponse>

    @GET("transactions")
    fun getTokensHistory(
            @Query("address") walletAddress: String,
            @Query("tokenTypeList") tokenTypeList: List<String>,
            @Query("dateFrom") dateFrom: Date,
            @Query("dateTo") dateTo: Date
    ): Call<List<TokenHistoryResponse>>

    @GET("tokens/{address}")
    fun getWalletTokens(@Path(value = "address", encoded = true) address: String): Call<List<WalletTokensResponse>>
}