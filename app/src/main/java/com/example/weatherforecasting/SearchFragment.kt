package com.example.weatherforecasting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.example.weatherforecasting.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    //create binding obj
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Initialize binding obj
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    //destroy binding obj
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}