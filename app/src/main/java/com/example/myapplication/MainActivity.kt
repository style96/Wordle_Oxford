package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity(){
    private val TAG = "MainActivity"
    private val viewModel by viewModels<WordleViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var gradapter: GridRVAdapter
    private var indexHolder : Int = 0
    private val wordList = listOf("refer","poker","bride", "kovan", "kazan", "mappa")
    private var wordOfDay = wordList[Random(System.nanoTime()).nextInt(wordList.size)]
    private var wordCount : Int = 0
    private var word : String = ""
    private lateinit var keyboardDict: Map<String, Button>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
    }


}