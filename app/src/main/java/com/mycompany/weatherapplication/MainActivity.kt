package com.mycompany.weatherapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil3.compose.AsyncImage
import com.mycompany.weatherapplication.model.Main
import com.mycompany.weatherapplication.model.Weather
import com.mycompany.weatherapplication.model.WeatherData
import com.mycompany.weatherapplication.network.NetworkExecutor.TAG
import com.mycompany.weatherapplication.ui.theme.WeatherApplicationNewTheme
import com.mycompany.weatherapplication.viewmodel.WeatherDataViewModel
import com.mycompany.weatherapplication.viewmodel.WeatherViewModelFactory
import java.text.DateFormat
import java.util.Date

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val weatherDataViewmodel: WeatherDataViewModel by viewModels {
            WeatherViewModelFactory(this.getString(R.string.open_weather_api_key))
        }
        enableEdgeToEdge()
        setContent {
            WeatherApplicationNewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    displayWeatherInfo(
                        modifier = Modifier.padding(innerPadding),
                        weatherDataViewmodel
                    )
                }
            }
        }
    }
}

@Composable
fun displayWeatherInfo(modifier: Modifier = Modifier, weatherDataViewmodel: WeatherDataViewModel) {
    Column {
        var searchQuery by remember { mutableStateOf("") }

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Enter City Name") },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(Modifier.height(10.dp))

        Log.d(TAG, "Search query : $searchQuery")
        //call the open weather API and update the UI
        LaunchedEffect(key1 = searchQuery) {
            weatherDataViewmodel.fetchWeatherData(searchQuery)
        }

        val weatherData: State<WeatherData?> = weatherDataViewmodel.weatherLiveData.observeAsState()
        val errorMessage: State<String?> = weatherDataViewmodel.weatherDataError.observeAsState()
        val openAlertDialog = remember { mutableStateOf(false) }

        errorMessage.value?.let {  error->
            displayErrorDialog(
                error,
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {openAlertDialog.value = false},
                icon = Icons.Default.Info
            )
        }

        //city field
        var cityFieldValue by remember { mutableStateOf("") }

        Text(
            text = cityFieldValue,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(4.dp)
        )

        cityFieldValue = weatherData.value?.name + ", " + (weatherData.value?.sys?.country)

        Spacer(Modifier.height(10.dp))

        // updated field
        var weatherUpdateTime by remember { mutableStateOf("") }
        Text(
            text = weatherUpdateTime,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(4.dp)
        )
        val df = DateFormat.getDateTimeInstance()
        val updatedOn = weatherData.value?.dt?.times(1000)?.let { Date(it) }?.let { df.format(it) }
        weatherUpdateTime = "Last update:  $updatedOn"

        Spacer(Modifier.height(10.dp))

        var weatherIcon by remember { mutableStateOf("") }

        val weather: Weather? = weatherData.value?.weather?.get(0)
        weatherIcon = "${weather?.icon}.png"

        // weather_icon
        AsyncImage(
            model = BuildConfig.WEATHER_API_IMAGE_URL.plus(weatherIcon),
            contentDescription = null
        )


        Spacer(Modifier.height(10.dp))

        // current_temperature_field
        var currentTemperature by remember { mutableStateOf("") }

        Text(
            text = currentTemperature,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(4.dp)
        )

        currentTemperature = weatherData.value?.main?.temp.toString()

        Spacer(Modifier.height(10.dp))

        var mainWeather: String? = null
        var description: String? = null
        if (weather != null) {
            mainWeather = weather.main
            description = weather.description
        }

        val main: Main? = weatherData.value?.main
        var pressure: String? = null
        var humidity: String? = null
        if (main != null) {
            if (main.pressure != null) pressure = java.lang.String.valueOf(main.pressure)

            if (main.humidity != null) humidity = java.lang.String.valueOf(main.humidity)
        }

        var weatherDetails by remember { mutableStateOf("") }

        weatherDetails = formatWeatherDetails(
            mainWeather, description, pressure,
            humidity, main?.tempMax,
            main?.tempMin, weatherData.value?.wind?.speed
        )

        Log.d(TAG, weatherDetails)

        // weather details field
        Text(
            text = weatherDetails,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(4.dp)
        )
    }

}

private fun formatWeatherDetails(
    main: String?, description: String?, pressure: String?, humidity: String?,
    tempMax: Double?, tempMin: Double?, speed: Double?
): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append(main)
    stringBuilder.append("\n")
    stringBuilder.append(description)
    stringBuilder.append("\n")
    stringBuilder.append("$tempMax/$tempMin")
    stringBuilder.append("\n")
    /*stringBuilder.append("Sunrise         "+sunrise);
        stringBuilder.append("\n");
        stringBuilder.append("Sunset          "+sunset);
        stringBuilder.append("\n");*/
    stringBuilder.append("Wind            $speed MPH")
    stringBuilder.append("\n")
    stringBuilder.append("Humidity        $humidity %")
    stringBuilder.append("\n")
    stringBuilder.append("Pressure        " + pressure + "hPa")
    return stringBuilder.toString()
}

@Composable
private fun displayErrorDialog(
    errorMessage: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    icon: ImageVector) {
    AlertDialog(
        icon = { Icon(icon, contentDescription = "Example Icon") },
        title = {
            Text(text = "Network Error")
        },
        text = {
            Text(text = errorMessage)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}