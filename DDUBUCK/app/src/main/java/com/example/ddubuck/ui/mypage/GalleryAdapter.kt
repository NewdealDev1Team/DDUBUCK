package com.example.ddubuck.ui.mypage

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.example.ddubuck.R


class GalleryAdapter(val context: Context, uriArr: ArrayList<String>) : BaseAdapter() {
    private var items = ArrayList<String>()

    init {
        this.items = uriArr
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(p: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = ImageView(context)
        val display = context.resources.displayMetrics

        Glide.with(context).load(items[p]).into(imageView)
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        imageView.layoutParams =
            LinearLayout.LayoutParams(display.widthPixels / 5, display.widthPixels / 5)
//        if (p == 3) {
//            Glide.with(context).clear(imageView)
//            imageView.setImageResource(R.drawable.ic_icon_plus)
//            imageView.adjustViewBounds = true
//            imageView.layoutParams =
//                LinearLayout.LayoutParams(display.widthPixels / 5, display.widthPixels / 5)
//            imageView.scaleType = ImageView.ScaleType.CENTER
//
//
//
//        } else {
//
//
//
//
//        }

        imageView.setOnClickListener {
            val intent =  Intent()
            intent.action = Intent.ACTION_VIEW;
            intent.type = "image/*";
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            context.startActivity(intent);
        }

        return imageView
    }

}