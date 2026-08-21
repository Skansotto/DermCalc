package com.casati.dermcalc.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.casati.dermcalc.ui.screens.bmi.BmiScreen
import com.casati.dermcalc.ui.screens.bsa.BsaScreen
import com.casati.dermcalc.ui.screens.easi.EasiScreen
import com.casati.dermcalc.ui.screens.home.HomeScreen
import com.casati.dermcalc.ui.screens.pasi.PasiScreen

@Composable
fun DermCalcNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToBmi = { navController.navigate(Screen.Bmi.route) },
                onNavigateToBsa = { navController.navigate(Screen.Bsa.route) },
                onNavigateToPasi = { navController.navigate(Screen.Pasi.route) },
                onNavigateToEasi = { navController.navigate(Screen.Easi.route) }
            )
        }
        composable(Screen.Bmi.route) { BmiScreen() }
        composable(Screen.Bsa.route) { BsaScreen() }
        composable(Screen.Pasi.route) { PasiScreen() }
        composable(Screen.Easi.route) { EasiScreen() }
    }
}
