package edu.rit.dk9612.resonancetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import edu.rit.dk9612.resonancetv.ui.MainAppScreen
import edu.rit.dk9612.resonancetv.ui.theme.ResonanceTVTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResonanceTVTheme {
                MainAppScreen()
            }
        }
    }
}