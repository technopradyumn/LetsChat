package com.technopradyumn.letschat.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.technopradyumn.letschat.CommonDivider
import com.technopradyumn.letschat.CommonImage
import com.technopradyumn.letschat.LetsChatViewModel
import com.technopradyumn.letschat.data.CHATS
import com.technopradyumn.letschat.data.MESSAGE
import com.technopradyumn.letschat.data.Message
import com.technopradyumn.letschat.data.OppositeUser
import com.technopradyumn.letschat.data.USER_NODE
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

@Composable
fun SingleChatScreen(function: () -> Unit, chatId: String) {
    val viewModel = remember {
        LetsChatViewModel(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance()
        )
    }
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid
    var reply by rememberSaveable { mutableStateOf("") }
    var messagesState by remember { mutableStateOf<List<Message>>(emptyList()) }
    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        while (true) {
            try {
                val currentTime = System.currentTimeMillis()
                val messagesSnapshot = db.collection(CHATS)
                    .document(chatId)
                    .collection(MESSAGE)
                    .get()
                    .await()

                val messagesList = messagesSnapshot.toObjects(Message::class.java)
                    .filter { it.timeStamp!! >= currentTime.toString() }
                    .sortedBy { it.timeStamp }
                    .reversed()

                messagesState = messagesList

                delay(1)
            } catch (e: Exception) {
                viewModel.handleException(e)
                delay(1)
            }
        }
    }

    LaunchedEffect(messagesState) {
        val lastMessageIndex = messagesState.lastIndex
        if (lastMessageIndex >= 0) {
            listState.animateScrollToItem(lastMessageIndex)
        }
    }


    Scaffold(
        topBar = {
            ChatHeader(
                name = "Friend",
                imageUrl = "",
                onBackClicked = function
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState
                ) {
                    items(messagesState.reversed()) { message ->
                        val isCurrentUser = message.sendBy == currentUserId

                        val backgroundColor = if (!isCurrentUser) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary

                        val textColor = if (!isCurrentUser) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary

                        val alignment = if (!isCurrentUser) Alignment.Start else Alignment.End

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = alignment
                        ) {
                            Card(
                                modifier = Modifier
                                    .wrapContentSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(backgroundColor)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = message.sendBy ?: "",
                                        color = textColor,
                                        fontSize = 10.sp,
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = message.message ?: "",
                                        color = textColor,
                                        fontSize = 20.sp,
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = message.timeStamp ?: "",
                                        color = textColor,
                                        fontSize = 10.sp,
                                    )
                                }
                            }
                        }
                    }



                }

                ReplyBox(
                    reply = reply,
                    onReplyChange = { reply = it },
                    onSendReply = {
                        viewModel.onSendReply(chatId, reply)
                        reply = ""
                    }
                )
            }
        }
    )
}


@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClicked: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClicked() }
            )

            Spacer(modifier = Modifier.widthIn(16.dp))

            Icon(
                imageVector = Icons.Rounded.SupervisedUserCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(48.dp)
            )

//            CommonImage(
//                data = imageUrl,
//                modifier = Modifier
//                    .padding(8.dp)
//                    .size(50.dp)
//                    .clip(CircleShape),
//                onClick = {}
//            )

            Spacer(modifier = Modifier.widthIn(16.dp))

            Text(
                text = name,
                modifier = Modifier,
                style = TextStyle(MaterialTheme.colorScheme.onPrimary, fontSize = 20.sp)
            )
        }
    }
}

@Composable
fun ReplyBox(
    reply: String,
    onReplyChange: (String) -> Unit,
    onSendReply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = reply,
                onValueChange = onReplyChange,
                maxLines = 3,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.widthIn(16.dp))

            Column {
                Spacer(modifier = Modifier.height(5.dp))
                Icon(
                    imageVector = Icons.Rounded.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            if (reply.isNotBlank()) {
                                onSendReply()
                            } else {

                            }
                        }
                )
            }

        }
    }
}

@Preview
@Composable
fun SingleChatScreenPreview() {
    SingleChatScreen(function = {}, chatId = "User123")
}