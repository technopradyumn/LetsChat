package com.technopradyumn.letschat.Screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.technopradyumn.letschat.CommonRow
import com.technopradyumn.letschat.LetsChatViewModel

@Composable
fun ChatListScreen(
    navController: NavHostController,
    viewModel: LetsChatViewModel,
    onClick: (Any?) -> Unit
) {
    val context = LocalContext.current

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val user = auth.currentUser
    val uid: String = user?.uid.orEmpty()
    val activity = (context as? Activity)
    viewModel.BackHandler1 {
        activity?.finish()
    }

    val chats by viewModel.chats.collectAsState()
    val userData = viewModel.userData
    val showDialog = remember {
        mutableStateOf(false)
    }
    val addChatNumber = remember {
        mutableStateOf("")
    }

    val onFabClick: () -> Unit = { showDialog.value = true }
    val onDismiss: () -> Unit = { showDialog.value = false }
    val onAddChat: () -> Unit = {
        viewModel.onAddChat(addChatNumber.value)
        showDialog.value = false
        addChatNumber.value = ""
    }

    LaunchedEffect(Unit) {
        viewModel.populateChats()
        viewModel.getCurrentUser()
        viewModel.getUserData(uid)
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                color = MaterialTheme.colorScheme.primary // Set the background color here
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Let's Chat",
                        style = TextStyle(
                            MaterialTheme.colorScheme.onPrimary,
                            fontSize = 20.sp
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            Fab(
                showDialog = showDialog.value,
                onFabClick = onFabClick,
                onDismiss = onDismiss,
                onAddChat = onAddChat,
                addChatNumber = addChatNumber
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                if (chats.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No Chats Available")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 0.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
                    ) {
                        items(chats) { chat ->
                            val chatUser = if (chat.user1.userId == userData?.userId) {
                                chat.user2
                            } else {
                                chat.user1
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            chatUser.userId?.let { userId ->
                                chatUser.imageUrl?.let { imageUrl ->
                                    CommonRow(
                                        name = chatUser.name,
                                        onItemClick = { onClick(chat.chatId) },
                                        imageUrl = imageUrl
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun Fab(
    showDialog: Boolean,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: () -> Unit,
    addChatNumber: MutableState<String>
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss.invoke()
                addChatNumber.value = ""
            },
            confirmButton = {
                Button(onClick = onAddChat) {
                    Text(text = "Add Chat")
                }
            },
            title = { Text(text = "Add Chat") },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value,
                    onValueChange = { addChatNumber.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        )
    }
    FloatingActionButton(
        onClick = { onFabClick() },
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 76.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}