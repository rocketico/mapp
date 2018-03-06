package io.rocketico.core

import io.rocketico.core.api.Api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Utils {
    private const val BASE_URL = "https://gist.githubusercontent.com/KirillZholnerovich/c8f50d91479599e2ce79cd773e7c16e1/raw/46a7f74ce10f3faec61c63dfaf7549d39d033611/"
    private lateinit var retrofit: Retrofit
    val api: Api

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(Api::class.java)
    }
}