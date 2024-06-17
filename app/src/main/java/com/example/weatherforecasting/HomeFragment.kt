package com.example.weatherforecasting

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.weatherforecasting.adapters.DailyWeatherAdapter
import com.example.weatherforecasting.adapters.HourlyWeatherAdapter
import com.example.weatherforecasting.databinding.FragmentHomeBinding
import com.example.weatherforecasting.models.DailyWeatherData
import com.example.weatherforecasting.models.HourlyWeatherData
import com.example.weatherforecasting.utils.Constants.TAG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeFragment : Fragment() {

    //create binding object
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /* LocationRequest - Requirements for the location updates, i.e.,
    how often you should receive updates, the priority, etc. */
    private lateinit var locationRequest: LocationRequest

    /* LocationCallback - Called when FusedLocationProviderClient
    has a new Location */
    private lateinit var locationCallback: LocationCallback

    // This will store current location info
    private var currentLocation: Location? = null

    private lateinit var adapter: HourlyWeatherAdapter
    private lateinit var hourlyWeatherData: List<HourlyWeatherData>

    private lateinit var dailyAdapter: DailyWeatherAdapter
    private lateinit var dailyWeatherData: List<DailyWeatherData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //initialize binding object
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //initialize the fusedLocationProviderClient class
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        isLocationPermissionGranted()

        binding.search.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadHourlyWeatherData()
        adapter = HourlyWeatherAdapter(hourlyWeatherData)
        binding.hourlyRv.adapter = adapter

        loadDailyWeatherData()
        dailyAdapter = DailyWeatherAdapter(dailyWeatherData)
        binding.dailyRv.adapter = dailyAdapter

        //used to fetch current date and time
        val sdf = SimpleDateFormat("EEE, MMM d, hh:mm aaa")
        val currentDate = sdf.format(Date())
        binding.date.text = currentDate
    }

    private fun loadDailyWeatherData() {
        dailyWeatherData = listOf(
            DailyWeatherData("Today",74, 37),
            DailyWeatherData("Sunday",74, 37),
            DailyWeatherData("Tuesday",74, 37),
            DailyWeatherData("Wednesday",74, 37),
            DailyWeatherData("Thursday",74, 37),
            DailyWeatherData("Friday",74, 37),
            DailyWeatherData("Saturday",74, 37),
        )
    }

    private fun loadHourlyWeatherData() {
        hourlyWeatherData = listOf(
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 ),
            HourlyWeatherData(9.30, 34, 54 )
        )
    }

    //Check if required permissions is granted or not if not then request those permissions
    private fun isLocationPermissionGranted() {
         if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
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
            if(it != null){
                val latitude = it.latitude
                val longitude = it.longitude
                Log.d(TAG, "latitude is : $latitude")
                Log.d(TAG, "longitude is : $longitude")
                getAddressFromLocation(latitude, longitude)
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses.get(0)
                    val addressText = address?.locality
                    Log.d(TAG, "Address is : $addressText")
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


