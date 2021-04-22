package com.example.ddubuck.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.ddubuck.R

class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        initToolBar()
    }

    //TODO Photo 사용 창 푸ㅏㅅ=서

    private fun initToolBar() {
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.share_toolbar)
        setSupportActionBar(tb)
        val tbm = supportActionBar
        if(tbm != null) {
            tbm.setDisplayHomeAsUpEnabled(true)
            tbm.show()
        }

    }
    override fun onOptionsItemSelected(item:MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}