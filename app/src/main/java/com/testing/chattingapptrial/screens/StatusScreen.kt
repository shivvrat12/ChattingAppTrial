package com.testing.chattingapptrial.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.testing.chattingapptrial.DestinationScreen
import com.testing.chattingapptrial.LcViewModel

@Composable
fun StatusScreen(navController: NavController,vm: LcViewModel) {
    BottomNavigationMenu(BottomNavigationItem.STATUSLIST,navController)
}