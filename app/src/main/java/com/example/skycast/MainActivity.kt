package com.example.skycast

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.ui.theme.SkyCastTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModelWeather: ViewModelWeather

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModelWeather.fetchCurrentLocationWeather(this)
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelWeather = ViewModelProvider(this)[ViewModelWeather::class.java]

        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        setContent {
            SkyCastTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherPage(viewModelWeather)
                }
            }
        }
    }
}