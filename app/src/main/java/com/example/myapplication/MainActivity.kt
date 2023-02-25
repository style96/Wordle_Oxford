package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity(){
    private val viewModel by viewModels<WordleViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var gradapter: GridRVAdapter
    private var indexHolder : Int = 0
    private val stringArray = Array(30){ GridViewModal("",0) }
    private val wordList = listOf("refer","poker","bride", "kovan", "kazan", "mappa")
    private var wordOfDay = wordList[Random(System.nanoTime()).nextInt(wordList.size)]
    private var wordCount : Int = 0
    private var word : String = ""
    private lateinit var keyboardDict: Map<String, Button>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        gradapter = GridRVAdapter(viewModel.stringArray,this)
        binding.gridView.adapter = gradapter
        bindKeyboardDict()
        setClickListenersToKeyboard()
        lifecycleScope.launch{
            viewModel.signal.collect{
                when(it){
                    Signal.NOTAWORD -> showToast("Kelime listesinde kelime bulunmuyor.")
                    Signal.NEEDLETTER -> showToast("eksik kelime girdiniz")
                    Signal.NEXTTRY -> showToast("kelime sözlükte var fakat yanlıs cevap tekrar dene")
                    Signal.GAMEOVER -> TODO()
                    Signal.WIN -> showToast("günün kelimesi doğru cevap")
                }
            }
        }
    }

    private fun writeLetter(v: View){
        if(stringArray.size > indexHolder){
            if(wordCount*5 + 5 > indexHolder) {
                val b:Button= v as Button
                stringArray[indexHolder].letter = b.text.toString()
                gradapter.notifyDataSetChanged()
                indexHolder++
            }
        }
    }
    private fun deleteLetter(){
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
            gradapter.notifyDataSetChanged()
        }
    }
    private fun enterButtonHandler(){
        if(indexHolder == 0){
            showToast("Kelime Giriniz")
            Signal.NEEDLETTER
        }
        else if(indexHolder%5 != 0){
            showToast("eksik kelime girdiniz")
            Signal.NEEDLETTER
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
            Log.d("Wordle", word)
            if(word == wordOfDay) {
                showToast("günün kelimesi doğru cevap")
                Signal.WIN
            } else if(wordList.contains(word)) {
                showToast("kelime sözlükte var fakat yanlıs cevap tekrar dene")
                Signal.NEXTTRY
                wordCount++
            }
        }
    }
    private fun setKeyboardDefaultLetterColor(button: Button) {
        button.setBackgroundColor(Color.GRAY)
    }
    private fun setKeyboardIncorrectLetterColor(button: Button) {
        button.setBackgroundColor(Color.DKGRAY)
    }
    private fun setKeyboardCorrectLetterColor(button: Button) {
        button.setBackgroundColor(Color.GREEN)
    }
    private fun setKeyboardCorrectLetterInWrongPositionColor(button: Button) {
        button.setBackgroundColor(Color.YELLOW)
    }
    private fun checkWordCorrectness(word : String) {
        if(!wordList.contains(word)) {
            showToast("Kelime listesinde kelime bulunmuyor.")
            Signal.NOTAWORD
            return
        }
        Log.d("Wordle", "Word : $word")
        for (i in word.indices) {
            if(wordOfDay.contains(word[i])) {
                if(word[i] == wordOfDay[i]){
                    Log.d("Wordle",word[i]+" yesile boya")
                    stringArray[wordCount*5+i].status = 1
                } else {
                    Log.d("Wordle",word[i]+" sariya boya")
                    stringArray[wordCount*5+i].status = 2
                }
            } else {
                Log.d("Wordle",word[i]+" dark griye boya")
                stringArray[wordCount*5+i].status = 3
            }
        }
        gradapter.notifyDataSetChanged()
    }
    private fun showToast(text: String){
        Log.d("Wordle", text)
        Toast.makeText(
            this,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun bindKeyboardDict() {
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
    }
    private fun setClickListenersToKeyboard(){
        keyboardDict.forEach {
            it.value.setOnClickListener { p0 ->
                lifecycleScope.launch {
                    viewModel.writeLetter(p0)
                    gradapter.notifyDataSetChanged()
                }
            }
        }
        binding.apply {
            buttonEnter.setOnClickListener{
                lifecycleScope.launch {
                    viewModel.enterButtonHandler()
                    gradapter.notifyDataSetChanged()
                }

            }
            buttonDelete.setOnClickListener{
                lifecycleScope.launch {
                    viewModel.deleteLetter()
                    gradapter.notifyDataSetChanged()
                }
            }
        }
    }
}