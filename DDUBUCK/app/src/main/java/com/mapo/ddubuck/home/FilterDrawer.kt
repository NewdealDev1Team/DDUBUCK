package com.mapo.ddubuck.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mapo.ddubuck.MainActivityViewModel
import com.mapo.ddubuck.R
import com.mapo.ddubuck.databinding.FragmentHomeDrawerBinding
import kotlinx.android.synthetic.main.fragment_home_drawer.view.*

class FilterDrawer :Fragment() {
    private val mainViewModel : MainActivityViewModel by activityViewModels()
    private lateinit var binding : FragmentHomeDrawerBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeDrawerBinding.inflate(inflater)
        val root = inflater.inflate(R.layout.fragment_home_drawer, container, false)




        binding.homeDrawerConfirmButton.setOnClickListener {
            mainViewModel.showDrawer.value = false
        }
        return root
    }
}

