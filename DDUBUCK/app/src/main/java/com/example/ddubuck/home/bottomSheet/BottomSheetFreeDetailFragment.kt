package com.example.ddubuck.home.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.ddubuck.R

class BottomSheetFreeDetailFragment : Fragment() {
    lateinit var callback: OnFreeStartClickedListener

    fun setOnFreeStartClickedListener(callback: OnFreeStartClickedListener) {
        this.callback = callback
    }

    interface OnFreeStartClickedListener {
        fun onFreeStartClicked()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView  = inflater.inflate(R.layout.bottom_sheet_free_detail,container, false)
        val startButton : Button = rootView.findViewById(R.id.start_button_free)
        startButton.setOnClickListener{
            callback.onFreeStartClicked()
        }
        return rootView
    }

}