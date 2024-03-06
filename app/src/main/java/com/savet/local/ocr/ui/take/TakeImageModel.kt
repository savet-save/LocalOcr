package com.savet.local.ocr.ui.take

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TakeImageModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is TakeImage Fragment"
    }
    val text: LiveData<String> = _text
}