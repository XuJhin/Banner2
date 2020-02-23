package com.example.banner2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BannerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val dataList = ArrayList<String>()
    fun setData(param: ArrayList<String>) {
        dataList.addAll(param)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val param = dataList[position]
        holder.itemView.findViewById<TextView>(R.id.iv_banner).setText("Item $param")
    }

}