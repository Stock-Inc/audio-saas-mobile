package dev.alnoer.audiosaas.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors

@Composable
fun AudioCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.audioColors.card,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .border(1.dp, MaterialTheme.audioColors.border),
    ) {
        content()
    }
}
