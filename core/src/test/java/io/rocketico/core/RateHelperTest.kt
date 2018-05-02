package io.rocketico.core

import io.rocketico.core.api.Api
import io.rocketico.core.model.TokenType
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class RateHelperTest {
    val TEST_WALLET = "0x89292cf683fc405680333f2c8e57ae8cd366a2da"
    val BASE_URL = "http://134.17.25.175:8080/api/"

    lateinit var api: Api

    @Before
    fun setUp() {
        api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Api::class.java)
    }

    @Test
    fun getTokenRateByDate() {
        val tmpRates = api.getRatesByDate(listOf(TokenType.ETH.codeName), Date(1525255200000L))
        val rates = tmpRates.execute().body()
        assertEquals(1, rates?.rates?.size)
    }

    @Test
    fun getTokenRatesByRange() {
    }

    @Test
    fun getYesterdayTokenRate() {
    }
}