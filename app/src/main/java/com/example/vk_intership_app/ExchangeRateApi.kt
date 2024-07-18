package com.example.vk_intership_app

import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {
    @GET("latest")
    suspend fun getLatestRates(@Query("base") base: String): ExchangeRatesResponse
}