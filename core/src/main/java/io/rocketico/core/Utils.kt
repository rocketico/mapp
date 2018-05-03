package io.rocketico.core

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import io.rocketico.core.api.Api
import io.rocketico.core.model.TokenType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode


object Utils {
    private val retrofit: Retrofit
    val api: Api

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(Cc.SERVER_URL)
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

    fun txFeeFromGwei(gwei: Int, ethRate: Float?, tokenType: TokenType): Float? {
        return if (ethRate == null) {
            null
        } else {
            val gasLimit =
                    if (tokenType == TokenType.ETH)
                        EthereumHelper.GAS_LIMIT
                    else
                        EthereumHelper.ERC_20_GAS_LIMIT

            val txFeeBI = BigInteger.valueOf(gwei.toLong()) * gasLimit
            val txFee = txFeeBI.toBigDecimal() * BigDecimal(ethRate.toString())
            txFee.divide(BigDecimal.TEN.pow(9), 5, RoundingMode.DOWN).toFloat()
        }
    }

    fun ethFromGwei(gwei: Int): Float {
        val tmpBI = BigDecimal.valueOf(gwei.toLong()).divide(BigDecimal.TEN.pow(9))
        return tmpBI.toFloat()

//        val gasLimit =
//                if (tokenType == TokenType.ETH)
//                    EthereumHelper.GAS_LIMIT
//                else
//                    EthereumHelper.ERC_20_GAS_LIMIT
//
//        val txFeeBI = BigInteger.valueOf(gwei.toLong()) * gasLimit
//        val txFee = txFeeBI.toBigDecimal() * BigDecimal(ethRate.toString())
//        txFee.divide(BigDecimal.TEN.pow(9), 5, RoundingMode.DOWN).toFloat()
    }

    fun scaleFloat(fiatValue: Float, scale: Int = 5): String {
        val formatString = "%.${scale}f"
        return String.format(formatString, fiatValue).replace(',', '.')
    }

    fun round(value: Float, scale: Int): Float {
        return Math.round(value * Math.pow(10.0, scale.toFloat().toDouble())) / Math.pow(10.0, scale.toDouble()).toFloat()
    }

    fun logDebug(message: Any) {
        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(AndroidLogAdapter())
            try {
                Logger.d(message)
            } catch (e: Exception) {
                println(message)
            }
        }
    }
}