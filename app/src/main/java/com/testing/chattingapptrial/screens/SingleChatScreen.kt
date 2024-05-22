package com.testing.chattingapptrial.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.testing.chattingapptrial.LcViewModel
import com.testing.chattingapptrial.data.Message

@Composable
fun SingleChatScreen(navController: NavController, vm: LcViewModel, chatId: String) {
    var replyText by rememberSaveable {
        mutableStateOf("")
    }
    val onSendReply = {
        vm.onSendReply(chatId, replyText)
        replyText = ""
    }

    var chatMessages = vm.chatMessage
    val myUser = vm._userData.value
    val currentChat = vm.chats.value.first{it.chatId == chatId}
    val chatUser = if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    LaunchedEffect(key1 = Unit) {
        vm.populateMsg(chatId)
    }
    BackHandler {
        vm.depopulateMessage()

    }
    Column(modifier = Modifier.fillMaxSize()) {
        ChatHeader(name = chatUser.name ?: "", imageUrl = chatUser.imageUrl ?: "") {
            navController.popBackStack()
            vm.depopulateMessage()
        }
        Spacer(modifier = Modifier.weight(1f))
        MessageBox(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            chatMessages = chatMessages.value,
            currentUserId = myUser?.userId ?: ""
        )
        replyBox(
            reply = replyText,
            onValueChange = { replyText = it },
            onSendReply = onSendReply,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Composable
fun MessageBox(modifier: Modifier,chatMessages: List<Message>,currentUserId: String){
    LazyColumn() {
        items(chatMessages){ msg ->
            val alignment  = if (msg.sendBy==currentUserId) Alignment.End else Alignment.Start
            val color  = if (msg.sendBy==currentUserId) Color(0xFF68C400) else Color(0xFFC0C0C0)
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalAlignment = alignment) {

            Text(text = msg.message?:"",
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(color)
                    .padding(12.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold)
        }
        }
    }
}
@Composable
fun ChatHeader(name: String, imageUrl: String?, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
            .clickable {
                onBackClick.invoke()
            }
            .padding(top = 10.dp, start = 8.dp))

        Image(painter = rememberAsyncImagePainter(model = imageUrl), contentDescription = null, modifier = Modifier
            .padding(4.dp)
            .size(50.dp)
            .clip(CircleShape))
//        CommonImage(
//            data = imageUrl, modifier = Modifier
//                .padding(8.dp)
//                .size(50.dp)
//                .clip(CircleShape)
//        )
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, top = 6.dp))
    }
}


@Composable
fun replyBox(reply: String, onValueChange: (String) -> Unit, onSendReply: () -> Unit,modifier: Modifier) {

    Row(
        modifier = Modifier
            .height(56.dp)
            .padding(top = 10.dp)
    ) {
        TextField(
            value = reply, onValueChange = onValueChange,
            Modifier
                .weight(1f)
                .clip(
                    CircleShape
                ), textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
        )
        Icon(Icons.Rounded.Send, contentDescription = null,
            Modifier
                .size(56.dp)
                .clickable {
                    onSendReply()
                })
    }
}