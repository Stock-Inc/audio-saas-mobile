package dev.alnoer.audiosaas.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors

@Composable
fun AudioConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = false,
) {
    Dialog(onDismissRequest = onDismiss) {
        AudioCard {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text(title, style = MaterialTheme.typography.headlineMedium)
                Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.audioColors.mutedForeground)
                AudioButton(
                    text = confirmLabel,
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    style = if (destructive) AudioButtonStyle.Destructive else AudioButtonStyle.Primary,
                    height = 48.dp,
                )
                AudioButton(
                    text = "Отмена",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    style = AudioButtonStyle.Outline,
                    height = 48.dp,
                )
            }
        }
    }
}
