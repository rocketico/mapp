package io.rocketico.core

import io.rocketico.core.api.Api
import io.rocketico.core.model.TokenType
import org.web3j.crypto.WalletUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

object Utils {
    private const val BASE_URL = "http://134.17.25.175:8080/" //todo move path ".../api/" here
    private val retrofit: Retrofit
    val api: Api

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(Api::class.java)
    }

    fun bigIntegerToFloat(value: BigInteger, decimals: Int = 18, scale: Int = 5): Float {
        val tmp = BigDecimal(value)
        val resultBigDecimal = tmp.divide(BigDecimal.valueOf(10).pow(decimals))
        return resultBigDecimal.setScale(scale, RoundingMode.DOWN).toFloat()
    }

    fun floatToBigInteger(value: Float, decimals: Int = 18): BigInteger {
        val tmp = BigDecimal(value.toString()).multiply(BigDecimal.TEN.pow(decimals))
        return tmp.toBigInteger()
    }

    fun txFeeFromGwei(gwei: Int, ethRate: Float?, tokenType: TokenType): Float {
        val gasLimit = if (tokenType == TokenType.ETH) EthereumHelper.GAS_LIMIT
                        else EthereumHelper.ERC_20_GAS_LIMIT

        val tmp1 = BigInteger.valueOf(gwei.toLong()) * gasLimit
        val tmp2 = tmp1.toBigDecimal() * BigDecimal(ethRate.toString())
        val tmp3 = tmp2.divide(BigDecimal.TEN.pow(9), 5, RoundingMode.DOWN)
        return tmp3.toFloat()
    }

    fun scaleFloat(fiatValue: Float, scale: Int = 5): String {
        val formatString = "%.${scale}f"
        return String.format(formatString, fiatValue).replace(',', '.')
    }

    fun round(value: Float, scale: Int): Float {
        return Math.round(value * Math.pow(10.0, scale.toFloat().toDouble())) / Math.pow(10.0, scale.toDouble()).toFloat()
    }
}