package edu.rit.dk9612.resonancetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import edu.rit.dk9612.resonancetv.ui.MainAppScreen
import edu.rit.dk9612.resonancetv.ui.theme.ResonanceTVTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                    }
                }
        }

        setContent {
            ResonanceTVTheme {
                MainAppScreen()
            }
        }
    }
}