package com.example.bestweather

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.example.bestweather.constants.API_Key
import com.example.bestweather.constants.BASE_URL
import com.example.bestweather.databinding.ActivityMainBinding
import com.example.bestweather.restapi.Interface
import com.example.bestweather.constants.UNIT
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt

/**
    The class Main Activity is responsible for handling basically everything in this app
**/
class MainActivity : AppCompatActivity()
{
    // Defines a binding variable later initialized in "onCreate()".
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Creation of a binding object that can be used to access the views in the layout. It inflates the layout file for the activity.
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Sets the content view of the activity to the root view of the inflated layout file
        setContentView(binding.root)

        /**
            When the location (in the EditText) is written by the user and it is pressed the Enter key,
            then it will fetch the Weather information regarding the desired location.
            It sets an Action Listener regarding the Edit Text.
            The lambda function ignore two parameters and uses the actionID.
        **/
        binding.Location.setOnEditorActionListener { _, actionID,_ ->
            // Checks if the action performed by the user is "Done", by pressing the "Enter" key on the keyboard.
            if(actionID==EditorInfo.IME_ACTION_DONE)
            {
                fetchWeather(binding.Location.text.toString())
                true
            }
            // Otherwise, it will interpret that the User is still typing, so it returns false.
            else
            {
                false
            }
        }
    }

    /**
        Function responsible for attending the Retrofit Service and attend the API request
    **/
    private fun fetchWeather(city: String)
    {
        /**
            Add a Retrofit Builder to build and create a Retrofit Object
            . Retrofit needs the base URI for the web service, and a converter factory to build a web services API.
            . Add the base URI for the web service.
            . Build the Retrofit object
            . Create the Retrofit object.
        **/
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(Interface::class.java)

        /**
            Gets the Weather Response information from the Weather API Retrofit service
        **/
        val response = retrofit.getWeather(city, API_Key, UNIT)

        /**
            Use of enqueue() method of the retrofit2.Call interface to make an asynchronous HTTP request.
            A Callback object specifying the expected Weather Response of the Open Weather Public Api endpoint.
        **/
        response.enqueue(object : Callback<com.example.bestweather.response.Response>
        {
            // The onResponse() method receives a successful response from the API endpoint
            override fun onResponse(call: Call<com.example.bestweather.response.Response>, response: Response<com.example.bestweather.response.Response>)
            {
                // Creation of a local variable to access the response's body
                val responseBody = response.body()!!
                // Call of a function that will update the UI, sending as an argument "responseBody"
                updateUI(responseBody)
            }

            // In case of an unsuccessful response, it calls the onFailure() method that will do a simple log message
            override fun onFailure(call: Call<com.example.bestweather.response.Response>, t: Throwable)
            {
                Log.d("DATA", t.toString())
            }
        })
    }

    private fun updateUI(responseBody: com.example.bestweather.response.Response)
    {
        // Commands to obtain the current day and hour
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("EEEE, HH:mm")

        // Will store locally the returned correct sunrise and sunset time
        val sunrise=convertUnixToDateFormat(responseBody.sys.sunrise)
        val sunset=convertUnixToDateFormat(responseBody.sys.sunset)

        // Update all the UI
        binding.CityName.text = responseBody.name
        binding.Country.text = responseBody.sys.country
        binding.Date.text = format.format(calendar.time)
        binding.Weather.setImageResource(selectImage(responseBody.weather[0].description))
        binding.Temperature.text = "${responseBody.main.temp.roundToInt()}ºC"
        binding.Status.text = responseBody.weather[0].main + ", " + responseBody.weather[0].description
        binding.Status.text = responseBody.weather[0].main + ", " + responseBody.weather[0].description
        binding.TempFeelsLike.text = "${responseBody.main.feels_like.roundToInt()}ºC"
        binding.Humidity.text = "${responseBody.main.humidity}%"
        binding.WindSpeed.text =" ${responseBody.wind.speed.roundToInt()} Km/h"
        binding.Pressure.text = " ${responseBody.main.pressure} mbar"
        binding.Sunrise.text = "$sunrise"
        binding.Sunset.text = "$sunset"
        binding.Location.setText("")
    }

    /**
        Function responsible for selecting the right image source from a few possibilities of descriptions
    **/
    private fun selectImage(description: String): Int
    {
        if(description=="clear sky")
        {
            return R.drawable.clear_sky
        }

        if(description=="few clouds")
        {
            return R.drawable.few_clouds
        }

        if(description=="overcast clouds")
        {
            return R.drawable.overcast_clouds
        }

        if(description=="scattered clouds")
        {
            return R.drawable.scattered_clouds
        }

        if(description=="light rain")
        {
            return R.drawable.light_rain
        }

        if(description=="moderate rain")
        {
            return R.drawable.heavy_rain
        }

        if(description=="shower rain")
        {
            return R.drawable.heavy_rain
        }

        if(description=="mist")
        {
            return R.drawable.mist
        }

        if(description=="snow")
        {
            return R.drawable.snow
        }

        return R.drawable.few_clouds
    }

    /**
        Function responsible for converting the Unix date into a readable one in UTC timezone (small compensation of 1 hour to be the same timezone as Portugal's)
    **/
    private fun convertUnixToDateFormat(time: Int): String
    {
        /**
            Creation of a new Date object.
            It converts the Unix time from seconds to milliseconds.
            It adds an additional hour to account for timezone compensation.
            Note: this number will be a Long data type.
        **/
        val date = Date((time+3600) * 1000L)

        // The SimpleDateFormat() method formats the date as the desired string
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        // The format() method returns a string representation of the date object.
        val formattedDate = format.format(date)

        // Return a substring with only containing the "HH:mm" of the sunrise and/or sunset
        return formattedDate.substring(11,16)
    }

}