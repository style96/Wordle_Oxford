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
import androidx.room.Room
import com.example.myapplication.databinding.FragmentGameScreenBinding
import com.example.myapplication.room.AppDatabase
import com.example.myapplication.room.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
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

        gradapter = GridRVAdapter(viewModel.stringArray,requireActivity().applicationContext)
        binding.gridView.adapter = gradapter
        keyboardDict = viewModel.bindKeyboardDict(binding)
        viewModel.bindLetterDict()
        setClickListenersToKeyboard()
        lifecycleScope.launch{
            viewModel.signal.collect{
                when(it){
                    Signal.NOTAWORD -> showToast("Kelime listesinde kelime bulunmuyor.")
                    Signal.NEEDLETTER -> showToast("eksik kelime girdiniz")
                    Signal.NEXTTRY -> showToast("kelime sözlükte var fakat yanlıs cevap tekrar dene")
                    Signal.GAMEOVER -> TODO()
                    Signal.WIN -> showToast("Kazandın.")
                }
            }
        }
        lifecycleScope.launch{
            viewModel.status.collect{
                when(it){
                    is SStatus.CORRECT -> it.button.setBackgroundColor(resources.getColor(R.color.green))
                    is SStatus.DEFAULT -> it.button.setBackgroundColor(resources.getColor(R.color.gray))
                    is SStatus.INCORRECT -> it.button.setBackgroundColor(resources.getColor(R.color.dark_gray))
                    is SStatus.WRONGPOSITION -> it.button.setBackgroundColor(resources.getColor(R.color.yellow))
                }
            }
        }
        val db = Room.databaseBuilder(
            requireActivity().applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
        val userDao = db.userDao()
        //val users: List<User> = userDao.getAll()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


    private fun setClickListenersToKeyboard(){
        keyboardDict.forEach {
            lifecycleScope.launch {
                viewModel.stringArray
            }
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
            requireActivity().applicationContext,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }
}