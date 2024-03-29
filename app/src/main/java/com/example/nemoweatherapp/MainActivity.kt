package com.example.nemoweatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.example.nemoweatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// 81f47d2366411e34e8ccee18462d7d10
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("London")
        SearchCity()


    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(city:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(city, "81f47d2366411e34e8ccee18462d7d10", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxiT = responseBody.main.temp_max
                    val miniT = responseBody.main.temp_min
//                    val temperature = responseBody.main.temp.toString()
//                    val temperature = responseBody.main.temp.toString()

                    binding.temp.text = "$temperature ℃"
                    binding.weather.text = condition
                    binding.conditions.text = condition
                    binding.maxi.text = "Max Temp: $maxiT ℃"
                    binding.mini.text = "Min Temp: $miniT ℃"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.date.text = date()
                    binding.day.text = day(System.currentTimeMillis())
                    binding.location.text = "$city"




//                    Log.d("TAG", "onResponse: $temperature")
                    changeImage(condition)

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImage(conditions:String) {
        when (conditions){
            "Clear Sky", "Sunny", "Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animation.setAnimation(R.raw.sun)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.animation.setAnimation(R.raw.rain)
            }
            "Haze", "Partly Clouds", "Clouds", "Overcast", "Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.animation.setAnimation(R.raw.cloud)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.animation.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animation.setAnimation(R.raw.sun)
            }
        }
        binding.animation.playAnimation()
    }

    fun day(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
    fun date(): String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun time(timestamp: Long): String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
}