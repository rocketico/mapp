package io.rocketico.core

import io.rocketico.core.api.Api
import org.web3j.crypto.WalletUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

object Utils {
    private const val BASE_URL = "http://server.com/api/v1/"
    private val retrofit: Retrofit
    val api: Api

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(Api::class.java)
    }

    fun bigIntegerToFloat(value: BigInteger, round: Boolean = false, decimals: Int = 18, scale: Int = 5): Float {
        val tmp = BigDecimal(value)
        val resultBigDecimal = tmp.divide(BigDecimal.valueOf(10).pow(decimals))
        val result = resultBigDecimal.setScale(scale, RoundingMode.FLOOR).toFloat()
        return result;
    }

    fun isPrivateKeyValid(privateKey: String): Boolean {
        return WalletUtils.isValidPrivateKey(privateKey)
    }

    fun round(value: Float, scale: Int): Float {
        return Math.round(value * Math.pow(10.0, scale.toFloat().toDouble())) / Math.pow(10.0, scale.toDouble()).toFloat()
    }
}