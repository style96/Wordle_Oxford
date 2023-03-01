package com.example.myapplication

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.databinding.FragmentGameScreenBinding
import kotlinx.coroutines.flow.asStateFlow
import java.util.*


val DEFAULT_LETTER = Letter(" ", R.drawable.textview_border, R.color.black)
val DEFAULT_KEY = Key(backgroundColor = R.color.gray, textColor = R.color.black)

class WordleViewModel() : ViewModel() {
    private val TAG = "WordleViewModel"
    val signal = MutableSharedFlow<Signal>()
    val status = MutableSharedFlow<SStatus>()

    private var indexHolder : Int = 0
    val stringArray = Array(30){ GridViewModal("",Status.DEFAULT) }
    private var wordCount : Int = 0
    private var word : String = ""
    private val wordList = listOf("refer","poker","bride", "kovan", "kazan", "mappa")
    private val _wordOfDay = MutableStateFlow(wordList[Random(System.nanoTime()).nextInt(wordList.size)])
    val wordOfDay = _wordOfDay.asStateFlow()
    private lateinit var keyboardDict: Map<String, Button>
    private lateinit var letterDict: MutableMap<String, Key>
    /**
     * Compose Experiments
     */

    private val _flowIndexHolder = MutableStateFlow(0)
    val flowIndexHolder = _flowIndexHolder.asStateFlow()

    private val _flowWordCount = MutableStateFlow(0)
    val flowWordCount = _flowWordCount.asStateFlow()

    private val _flowGameLevel = MutableStateFlow(5)
    val flowGameLevel = _flowGameLevel.asStateFlow()

    private val _flowStringArray = MutableStateFlow(List(flowGameLevel.value*6){ GridViewModal("",Status.DEFAULT) })
    val flowStringArray = _flowStringArray.asStateFlow()
    fun flowWriteLetter(gridViewModal: GridViewModal){

        if(flowStringArray.value.size > flowIndexHolder.value){
            if(flowWordCount.value*5 + 5 > flowIndexHolder.value) {
                _flowStringArray.value = _flowStringArray.value.mapIndexed {index, item ->
                    if(index == flowIndexHolder.value) gridViewModal
                    else item
                }
                _flowIndexHolder.value++
            }
        }
    }

    /**
     * Compose Experiments ends
     */
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
        if(word == wordOfDay.value){
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
            if(word == wordOfDay.value) {
                signal.emit(Signal.WIN)
                checkLetterStatus()
            } else if(wordList.contains(word)) {
                signal.emit(Signal.NEXTTRY)
                checkLetterStatus()
                wordCount++
            }
        }
    }
    private suspend fun checkLetterStatus(){
        stringArray.map {
            if(it.letter != ""){
                when(it.status){
                    Status.DEFAULT -> setKeyboardDefaultLetterColor(it.letter,keyboardDict[it.letter]!!)
                    Status.CORRECT -> setKeyboardCorrectLetterColor(it.letter,keyboardDict[it.letter]!!)
                    Status.WRONGPOSITION -> setKeyboardWrongPositionColor(it.letter,keyboardDict[it.letter]!!)
                    Status.INCORRECT -> setKeyboardIncorrectLetterColor(it.letter,keyboardDict[it.letter]!!)
                }
            }
        }
    }
    private suspend fun setKeyboardDefaultLetterColor(text: String, button: Button) {
        letterDict[text] = Key(backgroundColor = R.color.gray, textColor = R.color.black)
        status.emit(SStatus.DEFAULT(button))
    }
    private suspend fun setKeyboardIncorrectLetterColor(text: String, button: Button) {
        //button.setBackgroundResource(R.color.dark_gray)
        //button.setBackgroundColor(resources)
        letterDict[text] = Key(backgroundColor = R.color.dark_gray, textColor = R.color.black)
        status.emit(SStatus.INCORRECT(button))
    }
    private suspend fun setKeyboardCorrectLetterColor(text: String, button: Button) {
        //button.setBackgroundResource(R.color.green)
        //button.setBackgroundColor(Color.GREEN)
        letterDict[text] = Key(backgroundColor = R.color.green, textColor = R.color.black)
        status.emit(SStatus.CORRECT(button))
    }
    private suspend fun setKeyboardWrongPositionColor(text: String, button: Button) {
        //button.setBackgroundResource(R.color.yellow)
        //button.setBackgroundColor(Color.YELLOW)
        if(letterDict[text]!!.backgroundColor != R.color.green){
            letterDict[text] = Key(backgroundColor = R.color.dark_gray, textColor = R.color.black)
            status.emit(SStatus.WRONGPOSITION(button))
        }
    }
    private suspend fun checkWordCorrectness(word : String) {
        if(!wordList.contains(word)) {
            signal.emit(Signal.NOTAWORD)
            return
        }
        Log.d(TAG, "Word : $word")
        for (i in word.indices) {
            if(wordOfDay.value.contains(word[i])) {
                if(word[i] == wordOfDay.value[i]){
                    Log.d(TAG,word[i]+" yesile boya")
                    stringArray[wordCount*5+i].status = Status.CORRECT
                } else {
                    Log.d(TAG,word[i]+" sariya boya")
                    stringArray[wordCount*5+i].status = Status.WRONGPOSITION
                }
            } else {
                Log.d(TAG,word[i]+" dark griye boya")
                stringArray[wordCount*5+i].status = Status.INCORRECT
            }
        }
    }
    fun bindKeyboardDict(binding: FragmentGameScreenBinding): Map<String, Button> {
        binding.apply {
            keyboardDict = mapOf(
                "Q" to buttonQ,
                "W" to buttonW,
                "E" to buttonE,
                "R" to buttonR,
                "T" to buttonT,
                "Y" to buttonY,
                "U" to buttonU,
                "I" to buttonI,
                "O" to buttonO,
                "P" to buttonP,
                "A" to buttonA,
                "S" to buttonS,
                "D" to buttonD,
                "F" to buttonF,
                "G" to buttonG,
                "H" to buttonH,
                "J" to buttonJ,
                "K" to buttonK,
                "L" to buttonL,
                "Z" to buttonZ,
                "X" to buttonX,
                "C" to buttonC,
                "V" to buttonV,
                "B" to buttonB,
                "N" to buttonN,
                "M" to buttonM,
            )
        }
        return keyboardDict
    }
    fun bindLetterDict() {
        letterDict = mutableMapOf(
            "Q" to DEFAULT_KEY,
            "W" to DEFAULT_KEY,
            "E" to DEFAULT_KEY,
            "R" to DEFAULT_KEY,
            "T" to DEFAULT_KEY,
            "Y" to DEFAULT_KEY,
            "U" to DEFAULT_KEY,
            "I" to DEFAULT_KEY,
            "O" to DEFAULT_KEY,
            "P" to DEFAULT_KEY,
            "A" to DEFAULT_KEY,
            "S" to DEFAULT_KEY,
            "D" to DEFAULT_KEY,
            "F" to DEFAULT_KEY,
            "G" to DEFAULT_KEY,
            "H" to DEFAULT_KEY,
            "J" to DEFAULT_KEY,
            "K" to DEFAULT_KEY,
            "L" to DEFAULT_KEY,
            "Z" to DEFAULT_KEY,
            "X" to DEFAULT_KEY,
            "C" to DEFAULT_KEY,
            "V" to DEFAULT_KEY,
            "B" to DEFAULT_KEY,
            "N" to DEFAULT_KEY,
            "M" to DEFAULT_KEY,
        )
    }
}

data class Letter(
    val letter: String,
    val backgroundColor: Int = R.drawable.textview_border,
    val textColor: Int = R.color.black
)

data class Key(val backgroundColor: Int, val textColor: Int)