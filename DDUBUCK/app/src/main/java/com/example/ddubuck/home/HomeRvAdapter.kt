package com.example.ddubuck.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R

class HomeRvAdapter(val context: Context, val itemList: ArrayList<String>):
        RecyclerView.Adapter<Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.sheet_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(itemList[position], context)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}

class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)  {
    private val title = itemView?.findViewById<TextView>(R.id.sheet_item_title)
    private val body = itemView?.findViewById<TextView>(R.id.sheet_item_body)
    private val picture = itemView?.findViewById<ImageView>(R.id.sheet_item_picture)

    //https://blog.yena.io/studynote/2017/12/06/Android-Kotlin-RecyclerView1.html
    fun bind(i: String, context: Context) {
        itemView.setOnClickListener{println(i)}
        title?.text = "자유산책"
        body?.text = "나만의 자유로운 산책,\n즐길 준비 되었나요?"
        picture?.setImageResource(R.mipmap.ic_launcher)
    }
}