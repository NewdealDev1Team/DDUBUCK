package com.example.ddubuck

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CourseSelectSheetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sheet_test_page)

        var fooArray = arrayListOf<String>()
        for(i in 1..100) {
            fooArray.add(String.format("자유산책"))
        }
        var sheetRecycler : RecyclerView = findViewById(R.id.sheet_recycler)

        sheetRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sheetRecycler.adapter = MainRvAdapter(this, fooArray)

        println("HELLO")
    }
}

class MainRvAdapter(val context: Context, val itemList : ArrayList<String>):
    RecyclerView.Adapter<MainRvAdapter.Holder>() {
    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val title = itemView?.findViewById<TextView>(R.id.sheet_item_title)
        private val body = itemView?.findViewById<TextView>(R.id.sheet_item_body)
        private val picture = itemView?.findViewById<ImageView>(R.id.sheet_item_picture)

        //https://blog.yena.io/studynote/2017/12/06/Android-Kotlin-RecyclerView1.html
        fun bind (i: String, context: Context) {
            /* dogPhoto의 setImageResource에 들어갈 이미지의 id를 파일명(String)으로 찾고,
            이미지가 없는 경우 안드로이드 기본 아이콘을 표시한다.*/

            /* 나머지 TextView와 String 데이터를 연결한다. */
            /*
            if (dog.photo != "") {
                val resourceId = context.resources.getIdentifier(dog.photo, "drawable", context.packageName)
                dogPhoto?.setImageResource(resourceId)
            } else {
                dogPhoto?.setImageResource(R.mipmap.ic_launcher)
            }
             */
            title?.text = i
            body?.text = "나만의 자유로운 산책,\n즐길 준비 되었나요?"
            picture?.setImageResource(R.mipmap.ic_launcher)
        }

    }

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