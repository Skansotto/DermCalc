package com.casati.dermcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.casati.dermcalc.navigation.DermCalcNavHost
import com.casati.dermcalc.ui.theme.DermCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DermCalcTheme {
                DermCalcNavHost()
            }
        }
    }
}