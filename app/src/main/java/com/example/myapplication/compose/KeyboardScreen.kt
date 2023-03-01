package com.example.myapplication.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.GridViewModal
import com.example.myapplication.Status
import com.example.myapplication.WordleViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class KeyboardScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val viewModel = viewModel<WordleViewModel>()

                val stringList by viewModel.flowStringArray.collectAsState()
                val gameLevel by viewModel.flowGameLevel.collectAsState()

                Column(modifier = Modifier.fillMaxWidth()) {
                    LetterGrid(stringList, gameLevel) { viewModel.flowWriteLetter(GridViewModal("Z",Status.DEFAULT)) }
                    //KeyboardButton(onButtonClick = { changeFirstLetter() })
                }
            }
        }
    }
}

@Composable
fun LetterGrid(stringList: List<GridViewModal>, gameLevel: Int, onButtonClick: () -> Unit) {


    Log.d("KeyboardScreen","stringList size" +stringList.size)

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min=300.dp,max=420.dp)){
        LazyVerticalGrid(columns = GridCells.Fixed(gameLevel),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = if (gameLevel == 4) 65.dp else 35.dp, vertical = 25.dp)
                .fillMaxSize(1f)
        ) {
            itemsIndexed(
                items = stringList,
                key = { index, item -> index.toString() }) { index, it ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .aspectRatio(1f, true)
                        .border(BorderStroke(1.dp, Color.Black)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = it.letter)
                }
            }
        }

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for(y in 0..2){
                Row (
                    horizontalArrangement = Arrangement.spacedBy(
                    space = 2.dp,
                    alignment = Alignment.CenterHorizontally
                )) {
                    for (i in 0..9){
                        Button(onClick = { onButtonClick() },
                            modifier = Modifier
                                .weight(1f)
                                .height(IntrinsicSize.Max),
                            contentPadding = PaddingValues(0.dp)
                                ) {
                            Text(text = "Q")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
@Composable
fun KeyboardButton(
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for(y in 0..2){
            Row {
                for (i in 0..9){
                    Button(onClick =onButtonClick,
                        modifier = Modifier.weight(1.toFloat())) {
                        Text(text = "Q")
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun KeyboardButtonPreview() {

    MyApplicationTheme {
        val viewModel = viewModel<WordleViewModel>()

        val stringList by viewModel.flowStringArray.collectAsState()
        val gameLevel by viewModel.flowGameLevel.collectAsState()

        Column(modifier = Modifier.fillMaxWidth()) {
            LetterGrid(stringList, gameLevel) { viewModel.flowWriteLetter(GridViewModal("Z",Status.DEFAULT)) }
            //KeyboardButton(onButtonClick = { changeFirstLetter() })
        }
    }

}

