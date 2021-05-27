package com.mapo.ddubuck.mypage

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide


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

        imageView.setOnClickListener {
            val intent =  Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_GALLERY)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            context.startActivity(intent);
        }

        return imageView
    }

}