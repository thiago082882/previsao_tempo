package com.thiago.previsaotempo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.thiago.previsaotempo.databinding.ActivityMainBinding
import com.thiago.previsaotempo.model.Main
import com.thiago.previsaotempo.model.Weather
import com.thiago.previsaotempo.services.Api
import com.thiago.previsaotempo.utils.Const.API_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trocarTema.setOnCheckedChangeListener { btnView, isChecked ->
            if (isChecked) {
                binding.containerPrincipal.setBackgroundColor(Color.parseColor("#000000"))
                binding.containerInfo.setBackgroundResource(R.drawable.container_info_tema_escuro)
                binding.txtTituloInfo.setTextColor(Color.parseColor("#000000"))
                binding.txtInformacoes1.setTextColor(Color.parseColor("#000000"))
                binding.txtInformacoes2.setTextColor(Color.parseColor("#000000"))
                window.statusBarColor = Color.parseColor("#000000")
            }else {
                binding.containerPrincipal.setBackgroundColor(Color.parseColor("#396BCB"))
                binding.containerInfo.setBackgroundResource(R.drawable.container_info_tema_claro)
                binding.txtTituloInfo.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtInformacoes1.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtInformacoes2.setTextColor(Color.parseColor("#FFFFFF"))
                window.statusBarColor = Color.parseColor("#396BCB")
            }
        }

        binding.btnBuscar.setOnClickListener {
            val cidade = binding.edtBuscarCidade.text.toString()
            binding.progressbar.visibility = View.VISIBLE

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build()
                .create(Api::class.java)

            retrofit.weatherMap(cidade, API_KEY).enqueue(object : Callback<Main> {
                override fun onResponse(call: Call<Main>, response: Response<Main>) {
                    if (response.isSuccessful) {
                        respostaServidor(response)

                    } else {
                        Toast.makeText(applicationContext, "Cidade inválida", Toast.LENGTH_SHORT)
                            .show()
                        binding.progressbar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<Main>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "Erro fatal de servidor!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    binding.progressbar.visibility = View.GONE
                }

            })
        }
    }

    override fun onResume() {
        super.onResume()

        binding.progressbar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(Api::class.java)

        retrofit.weatherMap("São Paulo", API_KEY).enqueue(object : Callback<Main> {
            override fun onResponse(call: Call<Main>, response: Response<Main>) {
                if (response.isSuccessful) {
                    respostaServidor(response)

                } else {
                    Toast.makeText(applicationContext, "Cidade inválida", Toast.LENGTH_SHORT).show()
                    binding.progressbar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<Main>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro fatal de servidor!", Toast.LENGTH_SHORT)
                    .show()
                binding.progressbar.visibility = View.GONE
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun respostaServidor(response: Response<Main>) {

        val main: JsonObject = response.body()!!.main

        val temp = main.get("temp").toString()
        val tempMin = main.get("temp_min").toString()
        val tempMax = main.get("temp_max").toString()
        val humidity = main.get("humidity").toString()

        val sys: JsonObject = response.body()!!.sys

        val country = sys.get("country").asString
        var pais = ""

        val weather: List<Weather> = response.body()!!.weather

        val main_weather: String = weather[0].main
        val description: String = weather[0].description
        val name = response.body()!!.name


        //Converter kelvin em graus Celsius - F= 298 - 273.15 = 26.55C

        val temp_c = (temp.toDouble() - 273.15)
        val tempMin_c = (tempMin.toDouble() - 273.15)
        val tempMax_c = (tempMax.toDouble() - 273.15)
        val decimalFormat = DecimalFormat("00")

        //Tratamento do Pais
        if (country.equals("BR")) {
            pais = "Brasil"
        } else if (country.equals("US")) {
            pais = "Estados Unidos"
        }


        //Tratamento do idioma e imagem
        if (main_weather.equals("Clouds") && description.equals("few clouds")) {

            binding.imgClima.setBackgroundResource(R.drawable.flewclouds)
        } else if (main_weather.equals("Clouds") && description.equals("scattered clouds")) {

            binding.imgClima.setBackgroundResource(R.drawable.clouds)
        } else if (main_weather.equals("Clouds") && description.equals("broken clouds")) {

            binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
        } else if (main_weather.equals("Clouds") && description.equals("overcast clouds")) {

            binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
        } else if (main_weather.equals("Clear") && description.equals("clear sky")) {

            binding.imgClima.setBackgroundResource(R.drawable.clearsky)
        } else if (main_weather.equals("Snow")) {

            binding.imgClima.setBackgroundResource(R.drawable.snow)
        } else if (main_weather.equals("Rain")) {

            binding.imgClima.setBackgroundResource(R.drawable.rain)
        } else if (main_weather.equals("Drizzle")) {

            binding.imgClima.setBackgroundResource(R.drawable.rain)
        } else if (main_weather.equals("Thunderstorm")) {

            binding.imgClima.setBackgroundResource(R.drawable.trunderstorm)
        }


        val descricaoClima = when (description) {
            "clear sky" -> {
                "Céu Limpo"
            }

            "few clouds" -> {
                "Poucas Nuvens"
            }

            "scattered clouds" -> {
                "nuvens dispersas"
            }

            "broken clouds" -> {
                "nuvens quebradas"
            }

            "shower rain" -> {
                "chuva de banho"
            }

            "rain" -> {
                "chuva"
            }

            "thunderstorm" -> {
                "trovoada"
            }

            "snow" -> {
                "neve"
            }

            "mist" -> {
                "névoa"
            }

            else -> {
                "névoa"
            }
        }
        binding.txtTemperatura.text = "${decimalFormat.format(temp_c)} °C"
        binding.txtPaisCidade.text = "$pais - $name"

        binding.txtInformacoes1.text = "clima \n  $descricaoClima \n\n umidade \n $humidity"
        binding.txtInformacoes2.text =
            "Temp.Min \n ${decimalFormat.format(tempMin_c)} \n\n Temp.Max \n ${
                decimalFormat.format(tempMax_c)
            }"

        binding.progressbar.visibility = View.GONE
    }
}