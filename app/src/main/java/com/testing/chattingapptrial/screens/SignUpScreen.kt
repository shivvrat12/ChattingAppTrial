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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, vm: LcViewModel) {

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
            val nameState = remember {
                mutableStateOf(TextFieldValue())
            }
            val numberState = remember {
                mutableStateOf(TextFieldValue())
            }
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
            androidx.compose.material3.Text(
                text = "Sign Up",
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = nameState.value, onValueChange = {
                    nameState.value = it
                },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = numberState.value, onValueChange = {
                    numberState.value = it
                },
                label = { Text("Phone Number") },
                singleLine = true,
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

            Button(onClick = { vm.signUp(
                name = nameState.value.text,
                number = numberState.value.text,
                email = emailState.value.text,
                password = passwordState.value.text
            ) },
                modifier = Modifier.padding(8.dp)) {
                Text("SIGN UP")
            }
            Text("Already a User ? Go to Login - >",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.Login.route)
                    })
        }
    }
    if (vm.inProcess.value){
        CommonProgressBar()
    }
}