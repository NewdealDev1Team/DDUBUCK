package com.mapo.ddubuck.ui.share

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageProviderSheetViewModel : ViewModel() {
    val imageUri = MutableLiveData<Uri>()
}