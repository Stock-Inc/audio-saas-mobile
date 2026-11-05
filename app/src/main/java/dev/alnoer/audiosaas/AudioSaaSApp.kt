package dev.alnoer.audiosaas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.alnoer.audiosaas.navigation.AudioSaaSNavigation

@Composable
fun AudioSaaSApp(modifier: Modifier = Modifier) {
    AudioSaaSNavigation(modifier = modifier)
}
