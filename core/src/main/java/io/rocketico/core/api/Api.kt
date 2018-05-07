package io.rocketico.core.api

import io.rocketico.core.model.response.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("rates-by-date")
    fun getRatesByDate(
            @Query("tokenTypeList") tokenTypeList: List<String>,
            @Query("date") date: TimestampDate
    ): Call<TokensRatesResponse>

    @GET("rates-by-range")
    fun getRatesByDateRange(
            @Query("tokenTypeList") tokenTypeList: List<String>,
            @Query("fromDate") fromDate: TimestampDate,
            @Query("toDate") toDate: TimestampDate
    ): Call<TokenRatesRangeResponse>

    @GET("token/info")
    fun getTokenInfo(
            @Query("tokenType") tokenType: String,
            @Query("currency") currency: String
    ): Call<TokenInfoResponse>

    @GET("transactions")
    fun getTokensHistory(
            @Query("address") walletAddress: String,
            @Query("tokenTypeList") tokenTypeList: List<String>,
            @Query("fromDate") dateFrom: TimestampDate,
            @Query("toDate") dateTo: TimestampDate
    ): Call<List<TokenHistoryResponse>>

    @GET("tokens/{address}")
    fun getWalletTokens(
            @Path(value = "address") address: String
    ): Call<List<WalletTokensResponse>>
}