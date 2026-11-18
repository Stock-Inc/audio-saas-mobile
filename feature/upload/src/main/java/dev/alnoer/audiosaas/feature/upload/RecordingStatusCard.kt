package dev.alnoer.audiosaas.feature.upload

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alnoer.audiosaas.core.designsystem.component.AudioCard
import dev.alnoer.audiosaas.core.designsystem.component.AudioIcon
import dev.alnoer.audiosaas.core.designsystem.component.AudioIconAsset
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors

@Composable
internal fun RecordingStatusCard(phase: RecordingPhase, seconds: Long) {
    AudioCard {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (phase == RecordingPhase.Uploading) {
                CircularProgressIndicator(Modifier.size(32.dp), color = MaterialTheme.colorScheme.primary)
            } else {
                AudioIcon(AudioIconAsset.SaasMark, null, Modifier.size(40.dp), MaterialTheme.colorScheme.primary)
            }
            Text(
                text = "%02d:%02d".format(seconds / 60, seconds % 60),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 56.sp, lineHeight = 64.sp),
            )
            Text(
                text = when (phase) {
                    RecordingPhase.Ready -> "ГОТОВО К ЗАПИСИ"
                    RecordingPhase.Recording -> "ИДЁТ ЗАПИСЬ"
                    RecordingPhase.Review -> "ЗАПИСЬ ГОТОВА"
                    RecordingPhase.Uploading -> "ЗАГРУЗКА"
                    RecordingPhase.Success -> "ЗАГРУЖЕНО"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (phase == RecordingPhase.Recording) MaterialTheme.colorScheme.primary else MaterialTheme.audioColors.mutedForeground,
            )
        }
    }
}
