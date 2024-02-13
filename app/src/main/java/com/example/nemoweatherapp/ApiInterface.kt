package com.example.nemoweatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
//    fun getWeather(@Query("city") city: String): Call<WeatherResponse>
    fun getWeatherData(
        @Query("q") city:String,
        @Query("appid") appid:String,
        @Query("units") units:String

    ) : Call<WeatherApp>

}