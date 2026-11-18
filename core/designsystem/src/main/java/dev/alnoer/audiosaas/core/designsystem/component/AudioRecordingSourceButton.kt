package dev.alnoer.audiosaas.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors

@Composable
fun AudioRecordingSourceButton(
    onChooseFile: () -> Unit,
    onRecordVoice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        AudioButton(
            text = "Новая запись",
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                AudioIcon(AudioIconAsset.Add, null, Modifier.size(16.dp), MaterialTheme.colorScheme.onPrimary)
            },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(min = 240.dp),
            offset = DpOffset(0.dp, 6.dp),
            shape = RectangleShape,
            containerColor = MaterialTheme.audioColors.card,
            border = BorderStroke(1.dp, MaterialTheme.audioColors.border),
            tonalElevation = 0.dp,
        ) {
            DropdownMenuItem(
                text = { Text("Записать голос", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { AudioIcon(AudioIconAsset.SaasMark, null, Modifier.size(20.dp), MaterialTheme.colorScheme.primary) },
                onClick = {
                    expanded = false
                    onRecordVoice()
                },
            )
            HorizontalDivider(color = MaterialTheme.audioColors.border)
            DropdownMenuItem(
                text = { Text("Выбрать из файлов", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { AudioIcon(AudioIconAsset.Folder, null, Modifier.size(20.dp), MaterialTheme.audioColors.mutedForeground) },
                onClick = {
                    expanded = false
                    onChooseFile()
                },
            )
        }
    }
}
