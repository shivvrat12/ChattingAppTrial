package com.testing.chattingapptrial

import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.testing.chattingapptrial.data.CHATS
import com.testing.chattingapptrial.data.ChatData
import com.testing.chattingapptrial.data.ChatUser
import com.testing.chattingapptrial.data.Event
import com.testing.chattingapptrial.data.MESSAGE
import com.testing.chattingapptrial.data.Message
import com.testing.chattingapptrial.data.USER_NODE
import com.testing.chattingapptrial.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LcViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage,
) : ViewModel() {


    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessage = mutableStateOf<List<Message>>(listOf())
    var inProcess = mutableStateOf(false)
    var inProcessChats = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    var _userData = mutableStateOf<UserData?>(null)
    val inProgressChatMessage = mutableStateOf(false)
    var currentChatMessageListener : ListenerRegistration ?= null

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun populateMsg(chatID: String){
        inProgressChatMessage.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatID).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if (error != null){
                    handleException(error)
                }
                if(value != null){
                    chatMessage.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy { it.timestamp }
                    inProgressChatMessage
            }
            }
    }

    fun depopulateMessage(){
        chatMessage.value = listOf()
        currentChatMessageListener = null
    }

    fun populateChats(){
        inProcessChats.value = true;
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId",_userData.value?.userId),
                Filter.equalTo("user2.userId",_userData.value?.userId),
            )
        ).addSnapshotListener{
            value, error ->
            if (error!=null){
                handleException(error)
            }
            if (value != null){
            chats.value = value.documents.mapNotNull {
                it.toObject<ChatData>()
            }
            inProcessChats.value = false
        }
        }
    }

    fun onSendReply(chatID: String, message: String){
        val time = Calendar.getInstance().time.toString()

        val msg = com.testing.chattingapptrial.data.Message(_userData.value?.userId,message, time)
        db.collection(CHATS).document(chatID).collection(MESSAGE).document().set(msg)
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        inProcess.value = true
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill All Fields")
            return
        }
        inProcess.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        signIn.value = true
                        createOrUpdateProfile(name, number)
                        inProcess.value = false
                    } else {
                        handleException(it.exception, "signUp Failed")
                    }
                }
            } else {
                handleException(customMessage = "number Already Exist")
                inProcess.value = false
            }
        }
    }

    fun logIn(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "please fill all details")
            return
        } else {
            inProcess.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signIn.value = true
                    inProcess.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                } else {
                    handleException(it.exception, "Log in Failed")
                }
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageurl = it.toString())
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProcess.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {

            val result = it.metadata?.reference?.downloadUrl

            result?.addOnSuccessListener(onSuccess)
            inProcess.value = false
        }
            .addOnFailureListener {
                handleException(it)
            }
    }

    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageurl: String? = null,
    ) {
        var uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: _userData.value?.name,
            number = number ?: _userData.value?.number,
            imageUrl = imageurl ?: _userData.value?.imageUrl
        )

        uid.let {
            inProcess.value = true
            if (uid != null) {
                db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                    if (it.exists()) {
                        db.collection(USER_NODE).document(uid).set(userData)
                        inProcess.value = false
                        getUserData(uid)
                    } else {
                        db.collection(USER_NODE).document(uid).set(userData);
                        inProcess.value = false
                        getUserData(uid)
                    }
                }
                    .addOnFailureListener {
                        handleException(it, "Cannot Retrieve User")
                    }
            }
        }
    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot Retrieve User")
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                _userData.value = user
                inProcess.value = false
                populateChats()
            }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String? = null) {
        Log.e("LoginException", "live chat ", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage

        eventMutableState.value = Event(message)
        inProcess.value = false
    }


    fun logout() {
        auth.signOut()
        _userData.value = null
        signIn.value = false
        depopulateMessage()
        currentChatMessageListener = null
        eventMutableState.value = Event("Logged Out")

    }

    fun onAddChat(number: String) {

        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must contain digit only")
        } else {

            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", _userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", _userData.value?.number),
                        Filter.equalTo("user2.number", number),
                    )

                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "number not found")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        _userData.value?.userId,
                                        _userData.value?.name,
                                        _userData.value?.imageUrl,
                                        _userData.value?.number
                                    ),
                                    ChatUser(
                                        chatPartner.userId,
                                        chatPartner.name,
                                        chatPartner.imageUrl,
                                        chatPartner.number
                                    )
                                )

                                db.collection(CHATS).document(id).set(chat)
                            }
                        }
                        .addOnFailureListener {
                            handleException(it)
                        }
                } else {
                    handleException(customMessage = "Chats Already Exist")
                }
            }
        }
    }
}
