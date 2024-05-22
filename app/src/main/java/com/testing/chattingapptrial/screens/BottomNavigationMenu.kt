package com.testing.chattingapptrial.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.testing.chattingapptrial.DestinationScreen
import com.testing.chattingapptrial.R
import com.testing.chattingapptrial.navigateTo

enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen) {
    CHATLIST(R.drawable.chat__4_, DestinationScreen.ChatList),
    STATUSLIST(R.drawable.status, DestinationScreen.StatusList),
    PROFILE(R.drawable.user, DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White)
    ) {
        for (items in BottomNavigationItem.values()){
            Image(painter = painterResource(id = items . icon), contentDescription = null
            ,modifier = Modifier.size(40.dp).padding(4.dp).weight(1f).clickable {
                navigateTo(navController,items.navDestination.route)
                },
                colorFilter = if (items==selectedItem)
                ColorFilter.tint(color = Color.Black)
                else{
                    ColorFilter.tint(Color.Gray)
                }
            )
        }
    }

}