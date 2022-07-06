package com.example.composetutorial3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.composetutorial3.ui.theme.ComposeTutorial3Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutorial3Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    PhotographerCard()
                }
            }
        }
    }
}

@Composable
fun PhotographerCard(modifier : Modifier = Modifier){
    Row(modifier = modifier
        .clickable(onClick = {})
        .padding(16.dp))
    {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {

        }
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically) //align은 row에서 정렬
        ) {
            Text("베르나르 베르베르", fontWeight = FontWeight.Bold)
            // LocalContentAlpha 는 자식들의 투명도를 정의한다.
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text("3 분전", style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Composable
fun LayoutsCodelab(){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "LayoutsCodelab")
                },
                actions = {
                    IconButton(
                        onClick = {

                        }
                    ){
                        Icon(Icons.Default.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding -> //scaffold의 람다는 padding을 매개변수로 가진다.
        bodyContent(Modifier.padding(innerPadding))
    }
}
@Composable
fun bodyContent(modifier : Modifier = Modifier){
    Column(modifier = modifier){
        Text(text = "안녕하새우")
        Text(text = "대하 타이거새우 흰다리새우 가재 랍스터")
    }
}

@Composable
fun ScrollingList(){

    val listSize = 100

    val scrollState = rememberLazyListState()
    // 스크롤 포지션을 state로 저장

    val coroutineScope = rememberCoroutineScope()

    Column(){
        Row{
            Button(onClick = {
                coroutineScope.launch{
                    scrollState.animateScrollToItem(0)
                }
            }){
                Text("Scroll to the top")
            }
            Button(onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(listSize - 1)
                }
            }){
                Text("Scroll to the end")
            }
        }
        LazyColumn(state = scrollState){
            items(listSize){
                ImageListItem(index = it)
            }
        }
    }
}

@Composable
fun ImageListItem(index : Int){
    Row(verticalAlignment = Alignment.CenterVertically){
        Image(
            painter = rememberImagePainter(
                data = "https://developer.android.com/images/brand/Android_Robot.png"
            ),
            contentDescription = null,
            modifier= Modifier.size(50.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(text = "Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}
@Preview
@Composable
fun PhotographerCardPreview() {
    ComposeTutorial3Theme {
        ScrollingList()
    }
}