package com.google.samples.motionlayoutcodelab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.motionlayoutcodelab.databinding.FlightItemBinding

class FlightSearchResultAdapter : RecyclerView.Adapter<FlightSearchResultAdapter.BindViewHolder>() {
    var itemList: List<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: FlightItemBinding = DataBindingUtil.inflate(inflater, R.layout.flight_item, parent, false)
        return BindViewHolder(binding.root)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: BindViewHolder, position: Int) {
        val binding = holder.binding as FlightItemBinding
        val item = itemList.getOrNull(position)

        binding.flight.text = item
    }


    class BindViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding: ViewDataBinding?
            get() = DataBindingUtil.getBinding(itemView)
    }

}