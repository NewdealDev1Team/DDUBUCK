package com.example.ddubuck.ui.badge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ddubuck.R

class BadgeFragment : Fragment() {

    private lateinit var badgeViewModel: BadgeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        badgeViewModel =
                ViewModelProvider(this).get(BadgeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_badge, container, false)
        val textView: TextView = root.findViewById(R.id.text_badge)
        badgeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}