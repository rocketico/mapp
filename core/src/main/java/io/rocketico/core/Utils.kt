package io.rocketico.core

import io.rocketico.core.api.Api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger

object Utils {
    private const val BASE_URL = "https://gist.githubusercontent.com/KirillZholnerovich/c8f50d91479599e2ce79cd773e7c16e1/raw/462c2715eab236db5aceeff2094077bed91a0061/"
    private lateinit var retrofit: Retrofit
    val api: Api

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(Api::class.java)
    }

    fun bigIntegerToFloat(value: BigInteger, round: Boolean = false, scale: Int = 5): Float {
        var result = value.toFloat().div(1_000_000_000_000_000_000)
        if (round) {
            result = Utils.round(result, scale);
        }
        return result;
    }

    fun round(value: Float, scale: Int): Float {
        return Math.round(value * Math.pow(10.0, scale.toFloat().toDouble())) / Math.pow(10.0, scale.toDouble()).toFloat()
    }
}