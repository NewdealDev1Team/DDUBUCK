package com.example.ddubuck.ui.share

import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R

class ShareSelectRvAdapter(private val itemList: Array<String>,
                            private val onClick : (i:String) -> Unit):
        RecyclerView.Adapter<ShareSelectRvAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.share_select_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(itemList[position], onClick)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)  {
        private val body = itemView?.findViewById<TextView>(R.id.share_select_item_bodyTv)
        fun bind(i: String, onClick: (v:String) -> Unit) {
            body?.text = i
            itemView.setOnClickListener{
                onClick(i)
            }
        }
    }
}

