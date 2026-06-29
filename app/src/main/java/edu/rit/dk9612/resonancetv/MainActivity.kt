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

        // Check if the user is already signed in
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // Only sign in if we don't have a session
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success!
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