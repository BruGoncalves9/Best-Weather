package com.example.bestweather.restapi

import com.example.bestweather.response.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
    A public interface that exposes the [getWeather] method
**/
interface Interface
{
    /**
        Returns a [List] of [Response].
        The @GET annotation indicates that the "weather" endpoint will be requested with the GET HTTP method.
        The @Query annotation indicates that there are multiples arguments being added to the URI base.
        This way, it specifies the remaining parameters to answer the correct call to the public API.
    **/
    @GET("weather")
    fun getWeather(@Query("q") city:String,
                   @Query("appid") appid: String,
                   @Query("units") unit: String
    ): Call<Response>
}