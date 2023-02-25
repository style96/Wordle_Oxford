package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WordleViewModelFactory(private val startingToplam : Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordleViewModel::class.java)){
            return WordleViewModel() as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}