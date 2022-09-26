package com.example.stockmarket.data.remote.dto

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(
        @Query("apikey") apiKey: String
    ): ResponseBody

    companion object{
        const val API_KEY = "UKMO5FE10ZYKTVA5"
        const val BASE_URL = "https://alphavantage.co"
    }
}