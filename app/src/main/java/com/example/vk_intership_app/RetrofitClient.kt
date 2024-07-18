package com.example.vk_intership_app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "https://api.apilayer.com/exchangerates_data/"
    private const val API_KEY = "RH95wxyF0pxy334jLopFxMNAbtJJb87M"


    val api: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }
    fun getApiKey() = API_KEY
}
