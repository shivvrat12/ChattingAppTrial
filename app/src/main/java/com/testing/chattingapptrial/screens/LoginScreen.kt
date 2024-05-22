package com.testing.chattingapptrial.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.testing.chattingapptrial.CommonProgressBar
import com.testing.chattingapptrial.DestinationScreen
import com.testing.chattingapptrial.LcViewModel
import com.testing.chattingapptrial.R
import com.testing.chattingapptrial.checkSignedIn
import com.testing.chattingapptrial.navigateTo

@Composable
fun LoginScreen(vm: LcViewModel, navController : NavController){

    checkSignedIn(vm, navController)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val emailState = remember {
                mutableStateOf(TextFieldValue())
            }
            val passwordState = remember {
                mutableStateOf(TextFieldValue())
            }

            val focus = LocalFocusManager

            Image(
                painter = painterResource(id = R.drawable.chat__3_),
                contentDescription = null,
                modifier = Modifier
                    .width(150.dp)
                    .padding(top = 19.dp)
                    .padding(9.dp)
            )
            Text(
                text = "Sign In",
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            OutlinedTextField(
                value = emailState.value, onValueChange = {
                    emailState.value = it
                },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = passwordState.value, onValueChange = {
                    passwordState.value = it
                },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.padding(8.dp)
            )

            Button(onClick = {
                             vm.logIn(emailState.value.text,passwordState.value.text)
            },
                modifier = Modifier.padding(8.dp)) {
                Text("SIGN IN")
            }
            Text("New User ? Go to Sign UP - >",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.SignUp.route)
                    })
        }
    }
    if (vm.inProcess.value){
        CommonProgressBar()
    }
}