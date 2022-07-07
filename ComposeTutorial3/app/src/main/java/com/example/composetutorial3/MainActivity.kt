package com.example.composetutorial3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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
                    LayoutsCodelabs()
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

@Composable
fun StaggeredGrid(
    modifier : Modifier = Modifier,
    rows : Int = 3,
    content : @Composable () -> Unit
){
    Layout(
        modifier = modifier,
        content = content
    ){ measurables, constraints ->

        //행에 대한 너비 측정
        val rowWidths = IntArray(rows) { 0 }

        // 행에 대한 높이 측정
        val rowHeights = IntArray(rows) { 0 }

        // 하위 view들을 제한하지 않고, 주어진 제약조건들과 함께 측정한다.
        val placeables = measurables.mapIndexed{ index, measurable ->

            //하위요소 체크
            val placeable = measurable.measure(constraints)

            val row = index % rows
            rowWidths[row] = placeable.width
            rowHeights[row] = Math.max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid의 너비는 가장 넓은 행이다. coerceIn : 범위를 한정하는 함수
        val width = rowWidths.maxOrNull()
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth

        // Grid의 높이는 높이 제약조건으로 인해 강제로 변한된 각 행의 가장 높은 요소의 합이다.
        val height = rowHeights.sumOf{it}
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows){
            rowY[i] = rowY[i-1] + rowHeights[i-1]
        }
        layout(width, height) {
            // x cord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }

            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
fun Chip(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp, 16.dp)
                    .background(color = MaterialTheme.colors.secondary)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text)
        }
    }
}
val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)

@Composable
fun LayoutsCodelabs() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "LayoutsCodelab")
                },
                actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        BodyContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .background(color = Color.LightGray)
        .padding(16.dp)
        .size(200.dp)
        .horizontalScroll(rememberScrollState()),
        content = {
            StaggeredGrid {
                for (topic in topics) {
                    Chip(modifier = Modifier.padding(8.dp), text = topic)
                }
            }
        })
}

@Composable
fun ConstraintLayoutContent(){
    ConstraintLayout( ) {

        val (button1, text, button2) = createRefs()

        Button(
            onClick = {

            },
            modifier = Modifier.constrainAs(button1){
                top.linkTo(parent.top, margin = 16.dp)
            }
        ){
            Text("Button 1")
        }
        Text("Text", Modifier.constrainAs(text){
            top.linkTo(button1.bottom, margin = 16.dp)
            centerAround(button1.end)
        })

        val barrier = createEndBarrier(button1, text)
        Button(
            onClick = {},
            modifier = Modifier.constrainAs(button2){
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(barrier)
            }
        ){
            Text("Button 2")
        }
    }
}

@Composable
fun LargeConstraintLayout(){
    ConstraintLayout {
        val text = createRef()

        val guideLine = createGuidelineFromStart(fraction = 0.5f)

        Text(
            "This is a very very very very very very very long text",
            Modifier.constrainAs(text){
                linkTo(start = guideLine, end = parent.end)
                width = Dimension.preferredWrapContent
            }
        )
    }
}

@Composable
fun TwoTexts(modifier : Modifier = Modifier, text1: String, text2: String){
    Row(modifier = modifier.height(IntrinsicSize.Max)){
        // 하위 요소의 높이를 측정하여 제일 작은 큰 값으로 설정
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .wrapContentWidth(Alignment.Start),
            text = text1
        )
        Divider(color = Color.Black,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp))
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .wrapContentWidth(Alignment.End),
            text = text2
        )
    }
}

@Preview
@Composable
fun PhotographerCardPreview() {
    ComposeTutorial3Theme {
        Surface{
            TwoTexts(text1 = "Hi", text2 = "there")
        }
    }
}