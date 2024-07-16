package com.example.weatherforecasting

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.weatherforecasting.adapters.DailyWeatherAdapter
import com.example.weatherforecasting.adapters.HourlyWeatherAdapter
import com.example.weatherforecasting.databinding.FragmentHomeBinding
import com.example.weatherforecasting.models.DWeatherData
import com.example.weatherforecasting.models.Daily
import com.example.weatherforecasting.models.DailyUnits
import com.example.weatherforecasting.models.HWeatherData
import com.example.weatherforecasting.models.Hourly
import com.example.weatherforecasting.models.HourlyUnits
import com.example.weatherforecasting.models.Place
import com.example.weatherforecasting.utils.Constants.TAG
import com.example.weatherforecasting.utils.NetworkResults
import com.example.weatherforecasting.viewmodel.CurrentViewModel
import com.example.weatherforecasting.viewmodel.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


const val CHANNEL_ID = "coder"
@AndroidEntryPoint
class HomeFragment : Fragment() {

    //create binding object
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    //viewModel variable
    private val currentViewModel by viewModels<CurrentViewModel>()
    //Adapter
    private lateinit var hourlyAdapter: HourlyWeatherAdapter
    private lateinit var dailyAdapter: DailyWeatherAdapter

    //to get the current location
    private var LATITUDE: Double? = null
    private var LONGITUDE: Double? = null

    //for passing data between fragments
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //initialize binding object
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //initialize the fusedLocationProviderClient class
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        binding.search.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        return binding.root
    }

    private fun setNotification(description: String) {
        createNotificationChannel()
        var builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
        builder.setSmallIcon(R.drawable.notification)
            .setContentTitle("Weather Forecast")
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())){
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, "Weather Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Customized Channel for my Weather Forecasting App"

            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //used to fetch current date and time
        val sdf = SimpleDateFormat("EEE, MMM d, hh:mm aaa")
        val currentDate = sdf.format(Date())
        //binding.date.text = currentDate

        val isCurrentLocationWeather = sharedViewModel.selectedPlace.value

        if (isCurrentLocationWeather == null){
            //to check location permission
            isLocationPermissionGranted()
            setupObservers()
        } else{
            sharedViewModel.selectedPlace.observe(viewLifecycleOwner, Observer {place ->
                if(place != null){
                    fetchSelectedPlaceWeather(place)
                }
                else{
                    Log.d(TAG, "there is no data")
                }
            })
        }

    }

    private fun fetchSelectedPlaceWeather(place: Place) {
        binding.location.text = place.display_name.toString()

        Log.d(TAG, place.display_name.toString())
        LONGITUDE = place.lon?.toDouble()
        LATITUDE = place.lat?.toDouble()

        setupObservers()

        //call viewModel's method
        // Fetch current weather data
        currentViewModel.fetchCurrentData(LATITUDE!!, LONGITUDE!!)
        // Fetch daily weather data
        currentViewModel.fetchDailyData(LATITUDE!!, LONGITUDE!!)
        //fetch hourly weather data
        currentViewModel.fetchHourlyData(LATITUDE!!, LONGITUDE!!)
    }

    private fun setupObservers() {
        currentViewModel.currentLiveData.observe(viewLifecycleOwner, Observer {
            binding.isLoading.isVisible = true
            when (it) {
                is NetworkResults.Success -> {
                    binding.isLoading.isVisible = false
                    binding.temp.text = it.data?.current?.temperature_2m.toString()
                    binding.date.text = it.data?.current?.time
                    val temp = it.data?.current?.temperature_2m
                    Log.d(TAG, "temp is : $temp")
                    if(temp != null) {
                        if (temp in 35.0..39.0) {
                            binding.howsWeather.text = "Hot"
                        } else if (temp in 20.0..36.0) {
                            binding.howsWeather.text = "Sunny"
                        } else if (temp <= 20.0) {
                            binding.howsWeather.text = "Cold"
                            binding.animationView.setAnimation(R.raw.cloud)
                            setNotification("Weather is too cold, avoid to travel outside!!!")
                        } else if(temp >= 40){
                            setNotification("Weather is too hot, avoid to travel outside and keep hydrated  ")
                        }
                    }
                    //1 -> day , 2 -> night
                    val isDay = it.data?.current?.is_day
                    if(isDay == 1){
                        binding.animationView.setAnimation(R.raw.sun)
                        binding.mainLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
                        binding.search.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
                    } else{
                        binding.animationView.setAnimation(R.raw.moon)
                        binding.mainLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
                        binding.search.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    }
                }
                is NetworkResults.Loading -> {
                    binding.isLoading.isVisible = true
                }
                is NetworkResults.Error -> {
                    binding.isLoading.isVisible = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        dailyAdapter = DailyWeatherAdapter(
            DWeatherData(
                Daily(listOf(0.0), listOf(0.0), listOf(0.0), listOf("")),
                DailyUnits("", "", "", ""),
                0.0, 0.0, 0.0, 0.0,
                "", "", 0
            )
        )
        binding.dailyRv.adapter = dailyAdapter

        currentViewModel.dailyLiveData.observe(viewLifecycleOwner, Observer {
            binding.isLoading.isVisible = true
            when (it) {
                is NetworkResults.Success -> {
                    binding.isLoading.isVisible = false
                    it.data?.let { data ->
                        dailyAdapter.updateData(data)
                    }
                    val rain = it.data?.daily?.rain_sum?.get(0)
                    if (rain != null) {
                        if(rain >= 70.0)
                        {
                            binding.howsWeather.text = "Rainy"
                            binding.animationView.setAnimation(R.raw.rain)
                        } else if(rain in 25.0..65.0 ){
                            binding.howsWeather.text = "cloudy"
                            binding.animationView.setAnimation(R.raw.cloud)
                        }
                    }
                }
                is NetworkResults.Loading -> {
                    binding.isLoading.isVisible = true
                }
                is NetworkResults.Error -> {
                    binding.isLoading.isVisible = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        hourlyAdapter = HourlyWeatherAdapter(
            HWeatherData(
                0.0, 0.0,
                Hourly(listOf(0.0), listOf(0.0), listOf("")),
                HourlyUnits("", "", ""),
                0.0, 0.0,
                "", "", 0
            )
        )
        binding.hourlyRv.adapter = hourlyAdapter

        currentViewModel.hourlyLiveData.observe(viewLifecycleOwner, Observer { it ->
            binding.isLoading.isVisible = true
            when (it) {
                is NetworkResults.Success -> {
                    binding.isLoading.isVisible = false
                    it.data?.let { data ->
                        val currentDate = binding.date
                        val compareDate = currentDate.text.toString()
                        Log.d(TAG, compareDate)

                        // Filter the data based on the date comparison
                        val filteredTimes = data.hourly.time.filter { time ->
                            time >= compareDate
                        }
                        val filteredHourlyData = data.hourly.copy(
                            time = filteredTimes,
                            rain = data.hourly.rain.take(filteredTimes.size),
                            temperature_2m = data.hourly.temperature_2m.take(filteredTimes.size)
                        )
                        hourlyAdapter.updateData(
                            data.copy(hourly = filteredHourlyData)
                        )
                    }
                }
                is NetworkResults.Loading -> {
                    binding.isLoading.isVisible = true
                }
                is NetworkResults.Error -> {
                    binding.isLoading.isVisible = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    //Check if required permissions is granted or not if not then request those permissions
    private fun isLocationPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                500
            )
            return
        }
        //get latitude and latitude
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
                LATITUDE = it.latitude
                LONGITUDE = it.longitude
                //call viewModel's method
                // Fetch current weather data
                currentViewModel.fetchCurrentData(LATITUDE!!, LONGITUDE!!)
                // Fetch daily weather data
                currentViewModel.fetchDailyData(LATITUDE!!, LONGITUDE!!)
                //fetch hourly weather data
                currentViewModel.fetchHourlyData(LATITUDE!!, LONGITUDE!!)
                getAddressFromLocation(LATITUDE!!, LONGITUDE!!)

            }
        }
    }

    //convert lat and long to human readable name
    private fun getAddressFromLocation(latitude: Double?, longitude: Double?) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses.get(0)
                    val addressText = address?.locality
                    binding.location.text = addressText
                } else {
                    Log.d(TAG, "No address found for the location.")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d(TAG, "Geocoder service not available", e)
        }
    }

    //destroy binding object
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


