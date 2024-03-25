package com.technopradyumn.letschat.Screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.technopradyumn.letschat.CommonDivider
import com.technopradyumn.letschat.CommonProgressbar
import com.technopradyumn.letschat.CommonRow
import com.technopradyumn.letschat.LetsChatViewModel

@Composable
fun StatusScreen(
    navController: NavHostController,
    viewModel: LetsChatViewModel,
    onClick: (Any?) -> Unit

) {

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid

    val inProgressStatus = viewModel.inProgressStatus.value

    if (inProgressStatus) {
        CommonProgressbar()
    } else {
        val status = viewModel.status.value
        val userData = viewModel.userData

        val myStatus = status.filter {
            it.user.userId == userData?.userId
        }

        val otherStatus = status.filter {
            it.user.userId != userData?.userId
        }

        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {uri->

            uri?.let{
                viewModel.uploadStatus(uri,currentUserId)
            }

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
                            text = "Status",
                            style = TextStyle(
                                MaterialTheme.colorScheme.onPrimary,
                                fontSize = 20.sp
                            )
                        )
                    }
                }
            },
            floatingActionButton = {
                FAB {
                    launcher.launch("image/*")
                }
            },
            content = { it ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    if (status.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "No Status Available")
                        }
                    }else{
                        if (myStatus.isEmpty()){
                            CommonRow(
                                onItemClick = { onClick(myStatus[0].user.userId!!) },
                                imageUrl = myStatus[0].user.imageUrl,
                                name = myStatus[0].user.name,

                            )
                            CommonDivider()
                            val uniqueUsers = otherStatus.map { it.user }.toSet().toList()

                            LazyColumn(modifier = Modifier
                                .weight(1f)) {

                                items(uniqueUsers){ user->
                                    
                                    CommonRow(
                                        name = user.name,
                                        onItemClick = { onClick(user.userId) },
                                        imageUrl = user.imageUrl
                                    )
                                    
                                }

                            }
                        }
                    }
                }
            }
        )

    }


}

@Composable
fun FAB(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 76.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}