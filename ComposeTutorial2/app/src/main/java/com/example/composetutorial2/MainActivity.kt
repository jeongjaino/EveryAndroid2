package com.example.composetutorial2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetutorial2.ui.theme.ComposeTutorial2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutorial2Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {

    var expanded = remember { mutableStateOf(false)}//재구성으로부터 변수 초기화 방지
    val extraPadding = if(expanded.value) 48.dp else 0.dp

    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ){
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(extraPadding)) {
                Text(text = "Hello, ")
                Text(text = name)
            }
            OutlinedButton(onClick = {expanded.value = !expanded.value}) {
                Text(if(expanded.value) "Show less" else "Show more")
            }
        }
    }
}

@Composable
fun MyApp(names: List<String> = listOf("World" , "Compose")){
    Column(){
        for(name in names){
            Greeting(name = name)
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    ComposeTutorial2Theme {
        MyApp()
    }
}