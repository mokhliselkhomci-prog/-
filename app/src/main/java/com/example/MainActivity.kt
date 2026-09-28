package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.AzkarScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HadithScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrayerScreen
import com.example.ui.screens.QuranScreen
import com.example.ui.screens.SupportScreen
import com.example.ui.screens.SurahReaderScreen
import com.example.ui.screens.TasbihScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Ensure RTL layout for Arabic typography and navigation
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val viewModel: MainViewModel = viewModel()
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    BackHandler(enabled = currentScreen != Screen.HOME) {
                        viewModel.navigateBack()
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (currentScreen != Screen.SURAH_READER) {
                                BottomNavBar(
                                    currentScreen = currentScreen,
                                    onNavigate = { screen ->
                                        viewModel.navigateTo(screen)
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                                when (screen) {
                                    Screen.HOME -> HomeScreen(viewModel = viewModel)
                                    Screen.QURAN -> QuranScreen(viewModel = viewModel)
                                    Screen.SURAH_READER -> SurahReaderScreen(viewModel = viewModel)
                                    Screen.HADITH -> HadithScreen(viewModel = viewModel)
                                    Screen.AZKAR -> AzkarScreen(viewModel = viewModel)
                                    Screen.TASBIH -> TasbihScreen(viewModel = viewModel)
                                    Screen.PRAYER -> PrayerScreen(viewModel = viewModel)
                                    Screen.FAVORITES -> FavoritesScreen(viewModel = viewModel)
                                    Screen.SUPPORT -> SupportScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
