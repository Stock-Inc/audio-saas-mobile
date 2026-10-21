package dev.alnoer.audiosaas.feature.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioDestination
import dev.alnoer.audiosaas.core.designsystem.component.AudioFolderRow
import dev.alnoer.audiosaas.core.designsystem.component.AudioMainScaffold
import dev.alnoer.audiosaas.core.designsystem.component.AudioPageTitle
import dev.alnoer.audiosaas.core.designsystem.component.AudioTextField
import dev.alnoer.audiosaas.ui.theme.audioColors

@Composable
fun FoldersScreen(
    onDestinationSelected: (AudioDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    var search by remember { mutableStateOf("") }
    val folders = listOf(
        "Интервью" to 24,
        "Командные встречи" to 18,
        "Исследования" to 12,
        "Без папки" to 74,
    ).filter { search.isBlank() || it.first.contains(search, ignoreCase = true) }

    AudioMainScaffold(
        selectedDestination = AudioDestination.Folders,
        onDestinationSelected = onDestinationSelected,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AudioPageTitle("Папки")
            AudioButton(
                text = "Новая папка",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
            AudioTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = "Поиск по папкам",
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "ВАШИ ПАПКИ",
                    color = MaterialTheme.audioColors.mutedForeground,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                )
                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                Text(
                    text = folders.size.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                folders.forEach { (name, count) ->
                    AudioFolderRow(
                        name = name,
                        count = count,
                        onClick = {},
                    )
                }
            }
        }
    }
}
