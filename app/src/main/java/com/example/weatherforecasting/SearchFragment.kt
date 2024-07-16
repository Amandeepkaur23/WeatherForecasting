package com.example.weatherforecasting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherforecasting.adapters.PlaceAdapter
import com.example.weatherforecasting.databinding.FragmentSearchBinding
import com.example.weatherforecasting.models.Place
import com.example.weatherforecasting.utils.Constants.TAG
import com.example.weatherforecasting.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@AndroidEntryPoint
class SearchFragment : Fragment() {

    //create binding obj
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!

    private lateinit var placeAdapter: PlaceAdapter

    //activityViewModels delegate to share the ViewModel across multiple fragments
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Initialize binding obj
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeAdapter = PlaceAdapter { place ->
            Toast.makeText(requireContext(), "${place.display_name} is clicked", Toast.LENGTH_SHORT).show()
            sharedViewModel.setSelectedPlace(place)
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }

        binding.recyclerViewPlaces.adapter = placeAdapter

        binding.searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchPlaces(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun searchPlaces(query: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val nominatimService = retrofit.create(NominatimService::class.java)

        lifecycleScope.launch {
            binding.isLoading.isVisible = true
            try {
                val place = nominatimService.searchPlace(query)
                if (place.isNotEmpty()) {
                    binding.isLoading.isVisible = false
                    placeAdapter.setPlacesList(place)
                } else {
                    binding.isLoading.isVisible = false
                    Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
                binding.isLoading.isVisible = false
            }
        }
    }
    interface NominatimService {
        @GET("search")
        suspend fun searchPlace(
            @Query("q") query: String,
            @Query("format") format: String = "json"
        ): List<Place>
    }

    //destroy binding obj
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}