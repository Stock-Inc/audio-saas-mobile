package dev.alnoer.audiosaas.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.ui.theme.audioColors

@Composable
fun AudioPlayer(
    elapsed: String,
    duration: String,
    speed: String,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AudioCard(modifier = modifier.height(58.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(32.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(
                        role = Role.Button,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onPlay,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                AudioIcon(
                    asset = AudioIconAsset.Play,
                    contentDescription = "Воспроизвести",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "$elapsed / $duration",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = speed,
                color = MaterialTheme.audioColors.mutedForeground,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun AudioTranscriptExcerpt(
    utterances: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
) {
    AudioCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "РАСШИФРОВКА",
                color = MaterialTheme.audioColors.mutedForeground,
                style = MaterialTheme.typography.labelSmall,
            )
            utterances.forEach { (speaker, text) ->
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = speaker,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
fun AudioSummaryCard(
    summary: String,
    decision: String,
    taskCount: Int,
    modifier: Modifier = Modifier,
) {
    AudioCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "AI-СВОДКА",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(Modifier.weight(1f))
                AudioBadge(
                    text = "$taskCount ЗАДАЧИ",
                    height = 24.dp,
                )
            }
            Text(
                text = summary,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.audioColors.border),
            )
            Text(
                text = decision,
                color = MaterialTheme.audioColors.mutedForeground,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
