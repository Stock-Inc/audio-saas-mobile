package dev.alnoer.audiosaas.feature.recordings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioDestination
import dev.alnoer.audiosaas.core.designsystem.component.AudioFilterChip
import dev.alnoer.audiosaas.core.designsystem.component.AudioIcon
import dev.alnoer.audiosaas.core.designsystem.component.AudioIconAsset
import dev.alnoer.audiosaas.core.designsystem.component.AudioMainScaffold
import dev.alnoer.audiosaas.core.designsystem.component.AudioPageTitle
import dev.alnoer.audiosaas.core.designsystem.component.AudioRecordingRow
import dev.alnoer.audiosaas.core.designsystem.component.AudioTextField
import dev.alnoer.audiosaas.ui.theme.audioColors

@Composable
fun HistoryScreen(
    onDestinationSelected: (AudioDestination) -> Unit,
    onRecordingSelected: () -> Unit,
    onNewRecording: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var search by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableIntStateOf(0) }
    val filters = listOf("Все", "Интервью", "Встречи")
    val visibleRecordings = SampleRecordings.filter {
        search.isBlank() || it.title.contains(search, ignoreCase = true)
    }

    AudioMainScaffold(
        selectedDestination = AudioDestination.Recordings,
        onDestinationSelected = onDestinationSelected,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AudioPageTitle("История распознаваний")
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
            AudioTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = "Поиск по записям",
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEachIndexed { index, filter ->
                    AudioFilterChip(
                        text = filter,
                        selected = index == selectedFilter,
                        onClick = { selectedFilter = index },
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "НЕДАВНИЕ",
                    color = MaterialTheme.audioColors.mutedForeground,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                )
                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                Text(
                    text = "Сначала новые",
                    color = MaterialTheme.audioColors.mutedForeground,
                    style = MaterialTheme.typography.bodySmall,
                )
                androidx.compose.foundation.layout.Spacer(Modifier.size(6.dp))
                AudioIcon(
                    asset = AudioIconAsset.SortDescending,
                    contentDescription = "Сортировка: сначала новые",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.audioColors.mutedForeground,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                visibleRecordings.take(4).forEach { recording ->
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
