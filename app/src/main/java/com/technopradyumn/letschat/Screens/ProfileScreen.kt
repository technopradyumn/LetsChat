package com.technopradyumn.letschat.Screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.technopradyumn.letschat.CommonDivider
import com.technopradyumn.letschat.CommonImage
import com.technopradyumn.letschat.LetsChatViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: LetsChatViewModel,
    onLogOut: () -> Unit
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid: String? = user?.uid
    val userData = viewModel.userData

    var name by remember { mutableStateOf(userData?.name.orEmpty()) }
    var number by remember { mutableStateOf(userData?.number.orEmpty()) }
    var imageUrl by remember { mutableStateOf(userData?.imageUrl.orEmpty()) }
    var toast = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfileData(
            uid = uid,
            onSuccess = { nameTxt, numberTxt, _imageUrl ->
                name = nameTxt
                number = numberTxt
                imageUrl = _imageUrl
            },
            onFailure = { exception ->
                Log.e("Firestore", "Error getting document: ", exception)
            }
        )
    }
    androidx.compose.material3.Scaffold(
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Profile",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 20.sp
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "Save",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 20.sp
                            ),
                            modifier = Modifier
                                .clickable {
                                    viewModel.createOrUpdateProfile(imageUrl = imageUrl,name = name,number = number)
                                }
                        )

                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                ProfileContent(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    viewModel = viewModel,
                    uid = uid,
                    name = name,
                    number = number,
                    imageUrl = imageUrl,
                    onNameChange = { name = it },
                    onNumberChange = { number = it },
                    onLogOut = onLogOut
                )

                if (toast.value) {
                    Toast.makeText(context, "Number is Already Exists", Toast.LENGTH_SHORT).show()
                    toast.value = false
                }
            }
        }
    )
}

@Composable
fun ProfileContent(
    modifier: Modifier,
    viewModel: LetsChatViewModel,
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onLogOut: () -> Unit,
    imageUrl: String,
    uid: String?
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            ProfileImage(uid.toString(), imageUrl = imageUrl, viewModel = viewModel)
        }

        ProfileInfoRow("Name", name, onNameChange)
        Spacer(modifier = Modifier.height(8.dp))
        ProfileInfoRow("Number", number, onNumberChange)

        Spacer(modifier = Modifier.height(8.dp))
        CommonDivider()
        Spacer(modifier = Modifier.height(8.dp))

        ElevatedButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                viewModel.signOut()
                onLogOut()
            },
            colors = ButtonDefaults.elevatedButtonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Log Out", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun ProfileImage(uid: String, imageUrl: String?, viewModel: LetsChatViewModel) {

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                viewModel.uploadImage(
                    uid = uid,
                    imageUri = uri,
                    onSuccess = { storageImageUrl ->
                    }
                )
            }
        }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.padding(8.dp)) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(100.dp)
                ) {
                    if (imageUrl != null) {
                        CommonImage(
                            data = imageUrl,
                            contentScale = ContentScale.FillBounds,
                            onClick = { launcher.launch("image/*") }
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Change Profile Picture")
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    // Preview function
}