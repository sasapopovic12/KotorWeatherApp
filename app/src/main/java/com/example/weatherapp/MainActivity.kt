package com.example.weatherapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import org.json.JSONObject
import java.math.RoundingMode
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val CITY: String = "Kotor, ME"
    val API: String = "85199fe5d89f912d1cc385dfc3413e1b"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        weatherTask().execute()

    }
    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            binding.loader.visibility = android.view.View.VISIBLE
            binding.mainContainer.visibility = android.view.View.GONE
            binding.errorText.visibility = android.view.View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                val main = jsonObject.getJSONObject("main")
                val sys = jsonObject.getJSONObject("sys")
                val wind = jsonObject.getJSONObject("wind")
                val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
                val updated_at: Long = jsonObject.getLong("dt")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updated_at * 1000)
                    )
                var temp1 = main.getString("temp").toBigDecimal()
                temp1 = temp1.setScale(0, RoundingMode.HALF_EVEN)
                val temp = temp1.toString() + "°C"

                var tempMin1 = main.getString("temp_min").toBigDecimal()
                tempMin1 = tempMin1.setScale(0, RoundingMode.HALF_EVEN)

                val tempMin = "Min Temp: " + tempMin1 + "°C"

                var tempMax1 = main.getString("temp_max").toBigDecimal()
                tempMax1 = tempMax1.setScale(0, RoundingMode.HALF_EVEN)
                val tempMax = "Max Temp: " + tempMax1 + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObject.getString("name") + ", " + sys.getString("country")

                /* Populating extracted data into our views */
                binding.address.text = address
                binding.updatedAt.text = updatedAtText
                binding.status.text = weatherDescription.capitalize()
                binding.temp.text = temp
                binding.tempMin.text = tempMin
                binding.tempMax.text = tempMax
                binding.sunrise.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunrise * 1000)
                )
                binding.sunset.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunset * 1000)
                )
                binding.wind.text = windSpeed
                binding.pressure.text = pressure
                binding.humidity.text = humidity

                /* Views populated, Hiding the loader, Showing the main design */
                binding.loader.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE
            }
            catch (e: Exception)
            {
                binding.loader.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            }
        }
    }
}