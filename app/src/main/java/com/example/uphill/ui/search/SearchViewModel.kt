package com.example.uphill.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

//    TODO search view 다듬기
    private val _text = MutableLiveData<String>().apply {
        value = "This is record Fragment"
    }
    val text: LiveData<String> = _text
}