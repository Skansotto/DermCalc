package com.casati.dermcalc.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.casati.dermcalc.ui.screens.bmi.BmiScreen
import com.casati.dermcalc.ui.screens.bsa.BsaScreen
import com.casati.dermcalc.ui.screens.home.HomeScreen

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
        composable(Screen.Pasi.route) { PlaceholderScreen(title = "PASI") }
        composable(Screen.Easi.route) { PlaceholderScreen(title = "EASI") }
    }
}

// Segnaposto temporaneo: verrà sostituito dalla schermata reale nei prossimi step.
@Composable
private fun PlaceholderScreen(title: String) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$title - in sviluppo")
        }
    }
}
