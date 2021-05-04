package com.example.ddubuck.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ddubuck.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImageProviderSelectDialog : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.bottom_sheet_image_provider, container, false)
        val cameraButton = root.findViewById<android.widget.Button>(R.id.share_sheet_cameraButton)
        val galleryButton = root.findViewById<android.widget.Button>(R.id.share_sheet_galleryButton)

        val cancelButton = root.findViewById<android.widget.Button>(R.id.share_sheet_cancelButton)
        cancelButton.setOnClickListener {

        }
        return root
    }
}