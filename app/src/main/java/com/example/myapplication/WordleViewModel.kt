package com.example.myapplication

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.util.*


val DEFAULT_LETTER = Letter(" ", R.drawable.textview_border, R.color.black)
val DEFAULT_KEY = Key(backgroundColor = Color.DKGRAY, textColor = R.color.black)

class WordleViewModel() : ViewModel() {
    private val TAG = "WordleViewModel"
    val signal = MutableSharedFlow<Signal>()
    private var indexHolder : Int = 0
    val stringArray = Array(30){ GridViewModal("",0) }
    private var wordCount : Int = 0
    private var word : String = ""
    private val wordList = listOf("refer","poker","bride", "kovan", "kazan", "mappa")
    private var wordOfDay = wordList[Random(System.nanoTime()).nextInt(wordList.size)]


    fun writeLetter(v: View){
        if(stringArray.size > indexHolder){
            if(wordCount*5 + 5 > indexHolder) {
                val b: Button = v as Button
                stringArray[indexHolder].letter = b.text.toString()
                indexHolder++
            }
        }
    }
    fun deleteLetter(){
        if(word == wordOfDay){
            return
        }
        if (indexHolder >= 0){
            if(wordCount*5 < indexHolder){
                if(indexHolder != 0){
                    indexHolder--
                }
            }
            stringArray[indexHolder].letter = ""
        }
    }
    suspend fun enterButtonHandler(){
        if(indexHolder == 0){
            signal.emit(Signal.NEEDLETTER)
        }
        else if(indexHolder%5 != 0){
            signal.emit(Signal.NEEDLETTER)
        }
        else if(indexHolder%5 == 0){
            val startIndex = indexHolder - 5
            val endIndex = indexHolder
            word= ""
            for (i in startIndex..endIndex){
                word += stringArray[i].letter
            }
            word = word.lowercase()
            checkWordCorrectness(word)
            Log.d(TAG, word)
            if(word == wordOfDay) {
                signal.emit(Signal.WIN)
            } else if(wordList.contains(word)) {
                signal.emit(Signal.NEXTTRY)
                wordCount++
            }
        }
    }
    private suspend fun checkWordCorrectness(word : String) {
        if(!wordList.contains(word)) {
            signal.emit(Signal.NOTAWORD)
            return
        }
        Log.d(TAG, "Word : $word")
        for (i in word.indices) {
            if(wordOfDay.contains(word[i])) {
                if(word[i] == wordOfDay[i]){
                    Log.d(TAG,word[i]+" yesile boya")
                    stringArray[wordCount*5+i].status = 1
                } else {
                    Log.d(TAG,word[i]+" sariya boya")
                    stringArray[wordCount*5+i].status = 2
                }
            } else {
                Log.d(TAG,word[i]+" dark griye boya")
                stringArray[wordCount*5+i].status = 3
            }
        }
    }
}

data class Letter(
    val letter: String,
    val backgroundColor: Int = R.drawable.textview_border,
    val textColor: Int = R.color.black
)

data class Key(val backgroundColor: Int, val textColor: Int)