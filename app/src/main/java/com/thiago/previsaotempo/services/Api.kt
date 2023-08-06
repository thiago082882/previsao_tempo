package com.thiago.previsaotempo.services

import com.thiago.previsaotempo.model.Main
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


//https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
//f797e8a2b419b7c72db86f0a58e22775

interface Api {
    @GET("weather")
    fun weatherMap(
        @Query("q") cityName: String,
        @Query("appid") api_key: String
    ):Call<Main>
}