package com.technopradyumn.letschat

import android.net.Uri
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.*
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.technopradyumn.letschat.data.CHATS
import com.technopradyumn.letschat.data.ChatData
import com.technopradyumn.letschat.data.ChatUser
import com.technopradyumn.letschat.data.Events
import com.technopradyumn.letschat.data.MESSAGE
import com.technopradyumn.letschat.data.Message
import com.technopradyumn.letschat.data.STATUS
import com.technopradyumn.letschat.data.Status
import com.technopradyumn.letschat.data.USER_NODE
import com.technopradyumn.letschat.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LetsChatViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {
    var inProcess by mutableStateOf(false)
    val chats = MutableStateFlow<List<ChatData>>(listOf())

    var eventMutableState by mutableStateOf<Events<String>?>(null)
    var signIn by mutableStateOf(false)
    var userData by mutableStateOf<UserData?>(null)

    val storageReference = storage.reference.child("profile_images")

    private val _inProcessChats = MutableStateFlow(false)

    val status = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)

    init {
        initAuthentication()
    }

    private fun initAuthentication() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val currentUser = auth.currentUser
                    signIn = currentUser != null
                    currentUser?.uid?.let { userId ->
                        getUserData(userId)
                        signIn = true
                        populateChats()
                        getCurrentUser()
                    }
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }


    fun signOut() {
        if (auth.currentUser != null) {
            auth.signOut()
            signIn = false
        }
    }

    fun uploadImage(
        uid: String?,
        imageUri: Uri,
        onSuccess: (imageUrl: String) -> Unit
    ) {
        inProcess = true

        val filename = UUID.randomUUID().toString()
        val ref = storageReference.child(filename)
        val uploadTask = ref.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                updateImageUrlInFirestore(uid, uri.toString(),
                    onSuccess = {
                        onSuccess(uri.toString())
                    }
                )
            }.addOnFailureListener {
                handleException(it, "Failed to get image download URL")

            }
        }.addOnFailureListener {
            handleException(it, "Failed to upload image")
        }
    }

    fun updateImageUrlInFirestore(
        uid: String?,
        imageUrl: String,
        onSuccess: () -> Unit
    ) {
        db.collection(USER_NODE).document(uid!!)
            .update("imageUrl", imageUrl)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
            }
    }

    fun fetchUserProfileData(
        uid: String?,
        onSuccess: (name: String, number: String, imageUrl: String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (uid != null) {
            db.collection(USER_NODE).whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val name = document.getString("name").orEmpty()
                        val number = document.getString("number").orEmpty()
                        val imageUrl = document.getString("imageUrl").orEmpty()
                        onSuccess(name, number, imageUrl)
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            onFailure(Exception("User ID is null"))
        }
    }

    fun createOrUpdateProfile(imageUrl: String, name: String, number: String) {
        val uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name,
            number = number,
            imageUrl = imageUrl
        )

        uid?.let {
            db.collection(USER_NODE).document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        documentSnapshot.reference.set(userData)
                    } else {
                        db.collection(USER_NODE).document(uid)
                            .set(userData)
                        getUserData(uid)
                        signIn = true
                    }
                }
                .addOnFailureListener { e ->
                    handleException(e, "Failed to create/update user profile")
                    inProcess = false
                }
        }
    }

    fun getUserData(userId: String) {
        db.collection(USER_NODE).document(userId).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot Retrieve User")
            }

            if (value != null) {
                val user = value.toObject<UserData>()
                userData = user
                populateChats()
            } else {
                println("Snapshot value is null")
            }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {
        exception?.let {
            exception.printStackTrace()
        }
        val errorMessage = exception?.localizedMessage ?: ""
        val message = customMessage.ifEmpty { errorMessage }
        eventMutableState = Events(message)
        inProcess = false
    }

    @Composable
    fun BackHandler1(onBackPressed: () -> Unit) {
        val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

        DisposableEffect(dispatcher) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            }
            dispatcher?.addCallback(callback)
            onDispose {
                callback.remove()
            }
        }
    }

    fun onAddChat(number: String) {

        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must contain digits only")
        } else {
            // Assuming db is your Firestore instance
            db.collection(CHATS)
                .whereEqualTo("user1.number", number)
                .whereEqualTo("user2.number", userData?.number)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) {
                        // If the chat doesn't exist, check if the user exists
                        db.collection(USER_NODE)
                            .whereEqualTo("number", number)
                            .get()
                            .addOnSuccessListener { userSnapshot ->
                                if (userSnapshot.isEmpty) {
                                    handleException(customMessage = "Number not found")
                                } else {
                                    // If the user exists, fetch data and store it
                                    val chatPartner = userSnapshot.documents[0].toObject<UserData>()
                                    chatPartner?.let { partner ->
                                        val id = db.collection(CHATS).document().id
                                        val chat = ChatData(
                                            chatId = id,
                                            user1 = ChatUser(
                                                userData?.userId.toString(),
                                                userData?.name.toString(),
                                                userData?.number.toString(),
                                                userData?.imageUrl.toString()
                                            ),
                                            user2 = ChatUser(
                                                partner.userId,
                                                partner.name,
                                                partner.number,
                                                partner.imageUrl
                                            )
                                        )
                                        // Add the chat document
                                        db.collection(CHATS).document(id).set(chat)
                                            .addOnSuccessListener {
                                                // Handle success
                                            }
                                            .addOnFailureListener {
                                                // Handle failure
                                                handleException(it)
                                            }
                                    }
                                }
                            }
                            .addOnFailureListener {
                                handleException(it)
                            }
                    } else {
                        handleException(customMessage = "Chat already exists")
                    }
                }
                .addOnFailureListener {
                    handleException(it)
                }
        }
    }

    fun populateChats() {
        _inProcessChats.value = true

        db.collection(CHATS)
            .whereEqualTo("user1.userId", userData?.userId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    // Map documents to ChatData objects
                    chats.value = value.documents.mapNotNull { it.toObject<ChatData>() }
                }
            }

        db.collection(CHATS)
            .whereEqualTo("user2.userId", userData?.userId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    // Append documents to existing chats list
                    chats.value += value.documents.mapNotNull { it.toObject<ChatData>() }
                }
            }
    }

    fun getCurrentUser() {
        val uid = auth.currentUser?.uid.orEmpty()
        val db = FirebaseFirestore.getInstance()
        db.collection(USER_NODE)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentUser = document.toObject(UserData::class.java)
                    userData = currentUser
                    populateChats()
                } else {

                }
            }
            .addOnFailureListener { exception ->

            }
    }

    fun onSendReply(chatId: String, message: String) {
        try {
            val currentUser = auth.currentUser
            signIn = currentUser != null
            currentUser?.uid?.let { userId ->
                val currentTime = Calendar.getInstance().time
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss") // specify the format for date and time
                val time = sdf.format(currentTime)

                val msg = Message(userId, message, time)
                db.collection(CHATS).document(chatId)
                    .collection(MESSAGE).document().set(msg)
            }

        } catch (e: Exception) {
            handleException(e)
        }
    }

    fun uploadStatus(uri: Uri, uid: String?) {
        storeImage(uri){
            createStatus(it)
        }
    }

    fun createStatus(imageUrl: String){
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss") // specify the format for date and time
        val time = sdf.format(currentTime)
        val newStatus = Status(
            user = ChatUser(
                userData?.userId,
                userData?.name,
                userData?.number,
                userData?.imageUrl
            ),
            imageUrl = imageUrl,
            timeStamp = time,
        )

        db.collection(STATUS).document().set(newStatus)

    }

    fun storeImage(imageUri: Uri, onSuccess: (String) -> Unit) {
        val fileName = "${UUID.randomUUID()}"
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference.child("images/$fileName")

        storageRef.putFile(imageUri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl
                        .addOnCompleteListener { downloadUrlTask ->
                            if (downloadUrlTask.isSuccessful) {
                                val downloadUrl = downloadUrlTask.result.toString()
                                onSuccess.invoke(downloadUrl)
                            } else {

                            }
                        }
                } else {

                }
            }
    }

}