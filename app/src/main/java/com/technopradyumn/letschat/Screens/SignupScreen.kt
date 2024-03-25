package com.technopradyumn.letschat.Screens

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.technopradyumn.letschat.CheckSignedIn
import com.technopradyumn.letschat.LetsChatViewModel
import com.technopradyumn.letschat.R
import com.technopradyumn.letschat.data.USER_NODE
import com.technopradyumn.letschat.data.UserData

@Composable
fun SignupScreen(
    navController: NavHostController,
    viewModel: LetsChatViewModel,
    onClick: () -> Unit,
    onLogInClick: () -> Unit
) {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val user = auth.currentUser

    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    CheckSignedIn(viewModel = viewModel, navController = navController)

    val context = LocalContext.current

    val nameState = remember {
        mutableStateOf("")
    }

    val emailState = remember {
        mutableStateOf("")
    }

    val passwordState = remember {
        mutableStateOf("")
    }

    val mobileNumberState = remember {
        mutableStateOf("")
    }

    val errorState = remember {
        mutableStateOf(false)
    }

    val passworderrorState = remember {
        mutableStateOf(false)
    }

    val emailerrorState = remember {
        mutableStateOf(false)
    }

    val isNameError = nameState.value.isEmpty()
    val isMobileNumberError = mobileNumberState.value.isEmpty()
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
                text = "Sign Up",
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(top = 32.dp)
            )

            OutlinedTextField(
                value = nameState.value,
                onValueChange = {
                    nameState.value = it
                },
                placeholder = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                    )
                },
                isError = errorState.value && isNameError,
                singleLine = true
            )

            OutlinedTextField(
                value = emailState.value,
                onValueChange = {
                    emailState.value = it
                },
                placeholder = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                isError = emailerrorState.value ,
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

            OutlinedTextField(
                value = mobileNumberState.value,
                onValueChange = {
                    mobileNumberState.value = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                placeholder = { Text("Mobile Number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Call, contentDescription = null
                    )
                },
                isError = errorState.value && isMobileNumberError,
                singleLine = true
            )

            if (errorState.value) {
                Text(
                    text = "All fields must be filled!",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                modifier = Modifier.padding(top = 32.dp),
                onClick = {
                        if (isEmailValid(emailState.value)) {
                            passworderrorState.value = false
                            emailerrorState.value = false
//                            viewModel.signUp(
//                                name = nameState.value,
//                                email =  emailState.value,
//                                password =  passwordState.value,
//                                number = mobileNumberState.value
//                            )

                            db.collection(USER_NODE).whereEqualTo("number", mobileNumberState.value).get().addOnSuccessListener { querySnapshot ->
                                if (querySnapshot.isEmpty) {
                                    auth.createUserWithEmailAndPassword(emailState.value, passwordState.value).addOnCompleteListener { createUserTask ->
                                        if (createUserTask.isSuccessful) {
                                            val uid = auth.currentUser?.uid
                                            viewModel.signIn = true
                                            val userData = UserData(
                                                userId = uid,
                                                name = nameState.value,
                                                number = mobileNumberState.value
                                            )
                                            if (uid != null) {
                                                db.collection(USER_NODE).document(uid)
                                                    .set(userData)
                                                    .addOnSuccessListener {
                                                        onClick()
                                                        viewModel.getCurrentUser()

                                                    }
                                                    .addOnFailureListener { createUserFailure ->
                                                        Toast.makeText(context, "Failed to create user profile", Toast.LENGTH_LONG).show()
                                                    }

                                                viewModel.getCurrentUser()

                                            }
                                        } else {
                                            Toast.makeText(context, "Sign-up failed. Please try again.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "User with the same mobile number already exists.", Toast.LENGTH_LONG).show()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            passworderrorState.value = false
                            emailerrorState.value = true
                            Toast.makeText(
                                context,
                                "Invalid email format",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            ) {
                Text(text = "Sign Up")
            }

            Text(
                text = "Already a User? Go to LogIn",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable {
                        onLogInClick()
                    }
            )
        }
    }

}

private fun isEmailValid(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}


//@Preview(showSystemUi = true)
//@Composable
//fun SignupScreenPreview() {
//
//    val navController = rememberNavController()
//    val viewModel = LetsChatViewModel(auth = FirebaseAuth())
//
//    SignupScreen(
//        navController = navController,
//        viewModel = viewModel,
//        onClick = {
//            navController.popBackStack()
//            navController.navigate(Graph.HOME)
//        },
//        onLogInClick= {
//            navController.navigate(AuthScreen.SignUp.route)
//        }
//    )
//}