package dev.alnoer.audiosaas.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioCard
import dev.alnoer.audiosaas.core.designsystem.component.AudioDestination
import dev.alnoer.audiosaas.core.designsystem.component.AudioIcon
import dev.alnoer.audiosaas.core.designsystem.component.AudioIconAsset
import dev.alnoer.audiosaas.core.designsystem.component.AudioMainScaffold
import dev.alnoer.audiosaas.core.designsystem.component.AudioPageTitle
import dev.alnoer.audiosaas.core.designsystem.component.AudioRecordingRow
import dev.alnoer.audiosaas.feature.recordings.SampleRecordings
import dev.alnoer.audiosaas.core.designsystem.theme.AudioSaaSTheme
import dev.alnoer.audiosaas.core.designsystem.theme.Newsreader
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors

@Composable
fun OverviewScreen(
    onDestinationSelected: (AudioDestination) -> Unit,
    onRecordingSelected: () -> Unit,
    onNewRecording: () -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AudioMainScaffold(
        selectedDestination = AudioDestination.Overview,
        onDestinationSelected = onDestinationSelected,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AudioPageTitle("Обзор")
            AudioButton(
                text = "Новая запись",
                onClick = onNewRecording,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    AudioIcon(
                        asset = AudioIconAsset.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                },
            )
            MonthlySummary()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SectionLabel("НЕДАВНИЕ ЗАПИСИ")
                Spacer(Modifier.weight(1f))
                Text(
                    text = "ВСЕ ЗАПИСИ",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = Modifier.clickable(onClick = onViewAll),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SampleRecordings.take(3).forEach { recording ->
                    AudioRecordingRow(
                        title = recording.title,
                        metadata = recording.metadata,
                        status = recording.status,
                        onClick = onRecordingSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthlySummary() {
    AudioCard {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SectionLabel("ЭТОТ МЕСЯЦ")
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Metric(label = "ОБРАБОТАНО", value = "42,8 h", modifier = Modifier.weight(1f))
                Metric(label = "ЗАПИСЕЙ", value = "128", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Metric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        SectionLabel(label)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
                fontFamily = Newsreader,
                fontWeight = FontWeight.Normal,
                fontSize = 34.sp,
                lineHeight = 40.sp,
            ),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.audioColors.mutedForeground,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Medium,
        ),
    )
}

@PreviewScreenSizes
@Composable
private fun OverviewScreenPreview() {
    AudioSaaSTheme {
        Surface {
            OverviewScreen(
                onDestinationSelected = {},
                onRecordingSelected = {},
                onNewRecording = {},
                onViewAll = {},
            )
        }
    }
}
