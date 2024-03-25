package com.technopradyumn.letschat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.technopradyumn.letschat.navigation.graphs.Graph

@Composable
fun CommonProgressbar() {
    Row(modifier = Modifier
        .alpha(0.5f)
        .background(Color.LightGray)
        .clickable(enabled = false) {}
        .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CheckSignedIn(viewModel: LetsChatViewModel, navController: NavController) {
    val alreadySignIn = remember {
        mutableStateOf(false)
    }

    val signIn = viewModel.signIn
    if (signIn && !alreadySignIn.value) {
        alreadySignIn.value = true
        navController.navigate(Graph.HOME)
    }
}

@Composable
fun CommonDivider() {
    Divider(
        color = MaterialTheme.colorScheme.onBackground,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun CommonImage(
    data: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds,
    onClick: () -> Unit
) {
    val painter = rememberAsyncImagePainter(model = data)

    Box(modifier = modifier) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = CircleShape)
                .clickable { onClick() },
            contentScale = contentScale,
        )
    }
}

@Composable
fun CommonRow(name: String?, onItemClick: () -> Unit, imageUrl: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable { onItemClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CommonImage(
                data = imageUrl.toString(),
                onClick = {onItemClick.invoke() },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = name ?: "",
                modifier = Modifier.weight(1f)
                    .padding(start = 4.dp),
                style = TextStyle.Default.copy(fontSize = 16.sp)
            )
        }
    }
}
