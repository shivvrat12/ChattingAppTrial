package com.testing.chattingapptrial.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.testing.chattingapptrial.CommonDivider
import com.testing.chattingapptrial.CommonImage
import com.testing.chattingapptrial.CommonProgressBar
import com.testing.chattingapptrial.DestinationScreen
import com.testing.chattingapptrial.LcViewModel
import com.testing.chattingapptrial.navigateTo

@Composable
fun ProfileScreen(navController: NavController, vm: LcViewModel) {
    val inProgress = vm.inProcess.value
    if (inProgress) {
        CommonProgressBar()
    } else {
        val userData = vm._userData.value
        var name by remember {
            mutableStateOf(userData?.name?:"")
        }
        var number by remember {
            mutableStateOf(userData?.number?:"")
        }
        Column {
            ProfileContent(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                vm = vm,
                name = name,
                number = number,
                onNameChange = {name = it},
                onNumberChange = {number = it},
                onSave = {
                    vm.createOrUpdateProfile(name = name,number = number)
                         },
                onBack = {
                    navigateTo(navController, DestinationScreen.ChatList.route)
                         },
                onLogout ={
                    vm.logout()
                    navigateTo(navController,DestinationScreen.SignUp.route)
                }
            )
            BottomNavigationMenu(BottomNavigationItem.PROFILE, navController)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    modifier: Modifier,
    vm : LcViewModel,
    name : String,
    number : String,
    onNameChange:(String) -> Unit,
    onNumberChange:(String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onLogout:()-> Unit
) {

    val imageUrl = vm._userData.value?.imageUrl
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
            ) {
            Text(text = "Back", Modifier.clickable {
                onBack.invoke()
            })
            Text(text = "Save", Modifier.clickable {
                onSave.invoke()
            })
        }
            CommonDivider()

            ProfileImage(imageUrl = imageUrl,vm = vm)

            CommonDivider()

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Name" ,modifier = Modifier.width(100.dp))
                TextField(value = name, onValueChange = onNameChange,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                )

            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Number" ,modifier = Modifier.width(100.dp))
                TextField(value = number, onValueChange = onNumberChange,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                )

            }
            CommonDivider()

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.Center) {
                Text(text = "Log Out", modifier = Modifier.clickable {
                    onLogout
                })

            }
        }

}

@Composable
fun ProfileImage(imageUrl: String?, vm: LcViewModel) {

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                vm.uploadProfileImage(uri)
            }
        }
    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min))
    {
        Column(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {

                CommonImage(data = imageUrl)
            }
            Text(text = "Change Profile Picture")
        }
        if (vm.inProcess.value) {
            CommonProgressBar()
        }
    }
}