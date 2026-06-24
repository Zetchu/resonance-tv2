package edu.rit.dk9612.resonancetv.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

// We map your custom colors to the TV Material 3 color scheme
private val ResonanceDarkColorScheme = darkColorScheme(
    background = BackgroundDark,
    surface = SurfaceElevated,
    surfaceVariant = SurfaceHighlight,
    primary = PrimaryNeon,
    secondary = SecondaryPurple,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

@Composable
fun ResonanceTVTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ResonanceDarkColorScheme,
        // We will use default typography for this step,
        // you can add the Inter font later!
        content = content
    )
}