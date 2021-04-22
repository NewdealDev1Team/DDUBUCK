package com.example.ddubuck.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.ddubuck.R
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView


class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        initToolBar()
        initPhotoEditor()
    }

    private fun initToolBar() {
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.share_toolbar)
        setSupportActionBar(tb)
        val tbm = supportActionBar
        if(tbm != null) {
            tbm.setDisplayHomeAsUpEnabled(true)
            tbm.show()
        }

    }

    private fun initPhotoEditor() {
        val mPhotoEditorView = findViewById<PhotoEditorView>(R.id.photoEditorView)

        mPhotoEditorView.source.setImageResource(R.drawable.weather_high)

        val mTextRobotoTf = ResourcesCompat.getFont(this, R.font.mapohongdaefreedom)

        //val mEmojiTypeFace = Typeface.createFromAsset(assets, "emojione-android.ttf")

        val mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .build()
        mPhotoEditorView.setBackgroundColor(Color.BLACK)
        val bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.weather_low)
        mPhotoEditor.addText("aaaaaa", Color.BLACK)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}