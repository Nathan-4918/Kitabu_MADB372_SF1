package com.nathan.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nathan.kitabu.di.AppContainer
import com.nathan.kitabu.ui.navigation.KitabuApp
import com.nathan.kitabu.ui.screens.CatalogScreen
import com.nathan.kitabu.ui.theme.KitabuTheme
import com.nathan.kitabu.ui.viewmodel.LibraryViewModel
import com.nathan.kitabu.ui.viewmodel.LibraryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = AppContainer(applicationContext)
        setContent {
            KitabuTheme {
                val viewModel: LibraryViewModel = viewModel(
                    factory = LibraryViewModelFactory(container.repository)
                )
                KitabuApp(viewModel = viewModel)
            }
        }
    }
}