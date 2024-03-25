package com.technopradyumn.letschat.data

data class UserData(
    var userId: String? = "",
    var name: String?="",
    var number: String?="",
    var imageUrl: String? = ""
){
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "number" to number,
        "imageUrl" to imageUrl
    )
}

data class ChatData(
    val chatId:String? = "",
    val user1:ChatUser = ChatUser(),
    val user2:ChatUser = ChatUser()
)

data class ChatUser(
    var userId: String? = "",
    var name: String?="",
    var number: String?="",
    var imageUrl: String? = ""
)

data class Message(
    var sendBy: String? = "",
    var message: String? = "",
    var timeStamp: String? = "",
    var emoji: String? = "",
)

data class OppositeUser(
    var userId: String? = "",
    var name: String?="",
    var number: String?="",
    var imageUrl: String? = ""
)

data class Status(
    val user: ChatUser = ChatUser(),
    var imageUrl: String = "",
    var timeStamp: String? = ""
)