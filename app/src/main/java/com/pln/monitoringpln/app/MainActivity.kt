package com.pln.monitoringpln.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pln.monitoringpln.presentation.navigation.AppNavigation

import com.pln.monitoringpln.presentation.theme.MonitoringPLNTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonitoringPLNTheme {
                AppNavigation()
            }
        }
    }
}
