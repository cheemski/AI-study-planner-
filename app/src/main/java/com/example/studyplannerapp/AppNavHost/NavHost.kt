package com.example.studyplannerapp.AppNavHost

import android.telecom.Call
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.navigation.NavHostController

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    androidx.navigation.NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(onItemClick = { id -> navController.navigate(Call.Details(id)) })
        }
        composable<Call.Details> { backStackEntry ->
            val details: Call.Details = backStackEntry.toRoute()
            DetailsScreen(
                itemId = details.itemId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}