package io.rocketico.core

import io.rocketico.core.api.Api
import okhttp3.OkHttpClient
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WalletManagerTest {
    private val TEST_WALLET = "0x89292cf683fc405680333f2c8e57ae8cd366a2da"

    private val api: Api

    init {
        val httpOk = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(Cc.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpOk)
                .build()

        api = retrofit.create(Api::class.java)
    }

    @Test
    fun getWalletTokensTest() {
        val response = api.getWalletTokens(TEST_WALLET)
        println(response.request().url())
        val result = response.execute().body()

        assertTrue(result?.size!! >= 1)
    }
}