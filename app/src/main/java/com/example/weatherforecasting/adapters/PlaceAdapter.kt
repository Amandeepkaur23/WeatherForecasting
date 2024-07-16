package com.example.weatherforecasting.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecasting.databinding.PlaceItemListBinding
import com.example.weatherforecasting.models.Place

class PlaceAdapter(private val clickListener: (Place) -> Unit): RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    private var placesList: List<Place> = listOf()

    fun setPlacesList(places: List<Place>) {
        placesList = places
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = PlaceItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return placesList.size
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(placesList[position])
    }

     inner class PlaceViewHolder(private val binding: PlaceItemListBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(place: Place) {
            binding.txtPlaces.text = place.display_name
            binding.root.setOnClickListener {
                clickListener(place)
            }
        }
    }
}