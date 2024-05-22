package com.testing.chattingapptrial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.testing.chattingapptrial.screens.ChatListScreen
import com.testing.chattingapptrial.screens.LoginScreen
import com.testing.chattingapptrial.screens.ProfileScreen
import com.testing.chattingapptrial.screens.SignUpScreen
import com.testing.chattingapptrial.screens.SingleChatScreen
import com.testing.chattingapptrial.screens.StatusScreen
import com.testing.chattingapptrial.ui.theme.ChattingAppTrialTheme
import dagger.hilt.android.AndroidEntryPoint


sealed class DestinationScreen(var route: String){
    object SignUp:DestinationScreen("signup")
    object Login:DestinationScreen("login")
    object Profile:DestinationScreen("profile")
    object ChatList:DestinationScreen("chatlist")
    object SingleChat:DestinationScreen("singlechat/{chatId}"){
        fun createRoute(id:String) = "singlechat/$id"
    }

    object StatusList: DestinationScreen("StatusList")
    object StatusStatus:DestinationScreen("singlestatus/{userId}"){
        fun createRoute(userId:String) = "singlestatus/$userId"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingAppTrialTheme {
                // A surface container using the 'backgrou  nd' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation()
                }
            }
        }
    }
    @Composable
    fun ChatAppNavigation(){

        val navController = rememberNavController()
        var vm = hiltViewModel<LcViewModel>()
        NavHost(navController = navController, startDestination = DestinationScreen.SignUp.route) {
            composable(DestinationScreen.SignUp.route){
                SignUpScreen(navController, vm)
            }
            composable(DestinationScreen.Login.route){
                LoginScreen(vm, navController)
            }
            composable(DestinationScreen.ChatList.route){
                ChatListScreen(navController,vm)
            }
            composable(DestinationScreen.SingleChat.route){
               val chatId = it.arguments?.getString("chatId")
                chatId?.let {
                    SingleChatScreen(navController,vm,chatId)
                }
            }
            composable(DestinationScreen.StatusList.route){
                StatusScreen(navController,vm)
            }
            composable(DestinationScreen.Profile.route){
                ProfileScreen(navController,vm)
            }

        }
    }
}
