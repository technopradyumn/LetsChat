package com.technopradyumn.letschat.Screens

import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.events.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.technopradyumn.letschat.CheckSignedIn
import com.technopradyumn.letschat.CommonProgressbar
import com.technopradyumn.letschat.LetsChatViewModel
import com.technopradyumn.letschat.R
import com.technopradyumn.letschat.data.UserData
import com.technopradyumn.letschat.navigation.graphs.Graph
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LetsChatViewModel,
    onClick: () -> Unit,
    onSignUpClick: () -> Unit
) {

    CheckSignedIn(viewModel = viewModel, navController = navController)

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val user = auth.currentUser

    val context = LocalContext.current
    val activity = (context as? Activity)

    viewModel.BackHandler1 {
        activity?.finish()
    }

    val emailState = remember {
        mutableStateOf("")
    }

    val passwordState = remember {
        mutableStateOf("")
    }

    val passworderrorState = remember {
        mutableStateOf(false)
    }

    val emailerrorState = remember {
        mutableStateOf(false)
    }

    val passwordVisibility = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                ), horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Image(
                painter = painterResource(id = R.drawable.app_ic), contentDescription = null
            )

            Text(
                text = "Log In",
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(top = 32.dp)
            )

            OutlinedTextField(
                value = emailState.value,
                onValueChange = {
                    emailState.value = it
                },
                placeholder = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                isError = emailerrorState.value,
                singleLine = true
            )

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = {
                    passwordState.value = it
                },
                visualTransformation = if (passwordVisibility.value) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                placeholder = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                isError = passworderrorState.value,
                singleLine = true,
                trailingIcon = {
                    val icon = if (passwordVisibility.value) {
                        Icons.Default.RemoveRedEye
                    } else {
                        Icons.Default.Clear
                    }
                    IconButton(onClick = {
                        passwordVisibility.value = !passwordVisibility.value
                    }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null
                        )
                    }
                }
            )

            if (passworderrorState.value) {
                Text(
                    text = "Please fill all fields",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                modifier = Modifier.padding(top = 32.dp),
                onClick = {
                    if (isEmailValid(emailState.value) && passwordState.value.isNotBlank()) {
                        emailerrorState.value = false
                        passworderrorState.value = false

                        auth.signInWithEmailAndPassword(emailState.value, passwordState.value)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = auth.currentUser?.uid

                                    viewModel.signIn = true

                                    if (uid != null) {
                                        viewModel.getCurrentUser()
                                    }

                                    if (uid != null) {
                                        task.exception?.let {
                                            Toast.makeText(context, "Sign-in Successfully. ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                                        }
                                        onClick()
                                    } else {
                                        Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    task.exception?.let {
                                        Toast.makeText(context, "Sign-in failed. ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Sign-in failed. Please try again.", Toast.LENGTH_LONG).show()
                            }

                    } else {
                        emailerrorState.value = !isEmailValid(emailState.value)
                        passworderrorState.value = passwordState.value.isBlank()
                        Toast.makeText(
                            context,
                            "Invalid email format or Log in Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Text(text = "Log In")
            }

            Text(
                text = "New user ? Go to Sign Up",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable {
                        onSignUpClick()
                    }
            )
        }
    }

}

private fun isEmailValid(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
