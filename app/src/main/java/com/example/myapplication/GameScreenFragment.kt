package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentGameScreenBinding
import kotlinx.coroutines.launch

class GameScreenFragment : Fragment() {
    private val TAG = "GameScreenFragment"
    private var _binding: FragmentGameScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var gradapter: GridRVAdapter
    private lateinit var keyboardDict: Map<String, Button>
    private val viewModel by viewModels<WordleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGameScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        gradapter = GridRVAdapter(viewModel.stringArray,activity!!.applicationContext)
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

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
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

    private fun showToast(text: String){
        Log.d(TAG, text)
        Toast.makeText(
            activity!!.applicationContext,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }
}