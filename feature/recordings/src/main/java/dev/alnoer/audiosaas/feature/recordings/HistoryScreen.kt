package dev.alnoer.audiosaas.feature.recordings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioCard
import dev.alnoer.audiosaas.core.designsystem.component.AudioDestination
import dev.alnoer.audiosaas.core.designsystem.component.AudioLibraryMessage
import dev.alnoer.audiosaas.core.designsystem.component.AudioLibraryRow
import dev.alnoer.audiosaas.core.designsystem.component.AudioMainScaffold
import dev.alnoer.audiosaas.core.designsystem.component.AudioMenuAction
import dev.alnoer.audiosaas.core.designsystem.component.AudioPageTitle
import dev.alnoer.audiosaas.core.designsystem.component.AudioRecordingSourceButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioSectionHeading
import dev.alnoer.audiosaas.core.designsystem.component.AudioTextAction
import dev.alnoer.audiosaas.core.designsystem.component.AudioTextField
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors
import dev.alnoer.audiosaas.library.AudioSort

@Composable
fun HistoryScreen(
    onDestinationSelected: (AudioDestination) -> Unit,
    onRecordingSelected: (String) -> Unit,
    onNewRecording: () -> Unit,
    onRecordVoice: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var search by rememberSaveable { mutableStateOf("") }
    var selecting by rememberSaveable { mutableStateOf(false) }
    var moveIds by rememberSaveable { mutableStateOf<List<String>?>(null) }
    var tagAudioId by rememberSaveable { mutableStateOf<String?>(null) }
    var seenRevision by rememberSaveable { mutableIntStateOf(state.revision) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }
    LaunchedEffect(state.revision) {
        if (seenRevision != state.revision) {
            moveIds = null
            tagAudioId = null
            seenRevision = state.revision
        }
    }
    val visible = state.audio.filter { it.name.contains(search, true) }

    AudioMainScaffold(
        selectedDestination = AudioDestination.Recordings,
        onDestinationSelected = onDestinationSelected,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { AudioPageTitle("История распознаваний") }
                item {
                    AudioRecordingSourceButton(
                        onChooseFile = onNewRecording,
                        onRecordVoice = onRecordVoice,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    AudioTextField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = "Поиск по имени файла",
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LibraryDropdown(
                            label = when (viewModel.folderId) {
                                null -> "Все папки"
                                "null" -> "Без папки"
                                else -> state.folders.find { it.id == viewModel.folderId }
                                    ?.name.orEmpty()
                            },
                            choices = listOf(null to "Все папки", "null" to "Без папки") +
                                state.folders.map { it.id to folderPath(it.id, state.folders) },
                            enabled = !state.busy,
                            modifier = Modifier.weight(1f),
                            onSelect = { viewModel.filter(it, viewModel.tagId) },
                        )
                        LibraryDropdown(
                            label = state.tags.find { it.id == viewModel.tagId }
                                ?.let { "# " + it.name } ?: "Все теги",
                            choices = listOf(null to "Все теги") +
                                state.tags.map { it.id to "# " + it.name },
                            enabled = !state.busy,
                            modifier = Modifier.weight(1f),
                            onSelect = { viewModel.filter(viewModel.folderId, it) },
                        )
                    }
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        LibraryDropdown(
                            label = viewModel.sort.label,
                            choices = AudioSort.entries.map { it.name to it.label },
                            enabled = !state.busy,
                            modifier = Modifier.weight(1f),
                            onSelect = {
                                viewModel.filter(
                                    viewModel.folderId, viewModel.tagId,
                                    AudioSort.valueOf(requireNotNull(it)),
                                )
                            },
                        )
                        AudioTextAction(
                            text = if (selecting) "ОТМЕНА" else "ВЫБРАТЬ",
                            modifier = Modifier.heightIn(min = 48.dp),
                            onClick = {
                                if (!state.busy) {
                                    selecting = !selecting
                                    viewModel.clearSelection()
                                }
                            },
                        )
                    }
                }
                if (state.loading) {
                    item {
                        AudioLibraryMessage(
                            "Загружаем записи", "Собираем вашу аудиотеку.", loading = true,
                        )
                    }
                } else if (state.error != null) {
                    item {
                        AudioLibraryMessage(
                            "Не удалось загрузить", state.error.orEmpty(),
                            error = true, onRetry = viewModel::refresh,
                        )
                    }
                } else {
                    item {
                        AudioSectionHeading("ЗАПИСИ") {
                            Text(
                                visible.size.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                    if (visible.isEmpty()) {
                        item {
                            AudioLibraryMessage(
                                "Записей не найдено",
                                if (search.isNotBlank() || viewModel.folderId != null ||
                                    viewModel.tagId != null
                                ) "Измените поиск или выбранные фильтры."
                                else "Запишите голос или загрузите первый аудиофайл.",
                            )
                        }
                    }
                    items(visible, key = { it.id }) { audio ->
                        AudioLibraryRow(
                            title = audio.name,
                            subtitle = listOf(
                                audio.folder?.name ?: "Без папки",
                                audio.tags.joinToString(" · ") { "# " + it.name },
                            ).filter { it.isNotEmpty() }.joinToString(" · "),
                            status = statusLabel(audio.status),
                            failed = audio.status == "FAILED",
                            selected = if (selecting) audio.id in state.selected else null,
                            enabled = !state.busy,
                            onClick = {
                                if (selecting) viewModel.toggle(audio.id)
                                else onRecordingSelected(audio.id)
                            },
                            actions = listOf(
                                AudioMenuAction("Переместить в папку", {
                                    viewModel.clearEditError()
                                    moveIds = listOf(audio.id)
                                }),
                                AudioMenuAction("Изменить теги", {
                                    viewModel.clearEditError()
                                    tagAudioId = audio.id
                                }),
                            ),
                        )
                    }
                }
            }
            if (selecting) {
                AudioCard {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "ВЫБРАНО",
                                color = MaterialTheme.audioColors.mutedForeground,
                                style = MaterialTheme.typography.labelSmall,
                            )
                            Text(
                                state.selected.size.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                        AudioButton(
                            text = "Переместить",
                            onClick = {
                                viewModel.clearEditError()
                                moveIds = state.selected.toList()
                            },
                            enabled = state.selected.isNotEmpty() &&
                                !state.busy && !state.loading,
                            height = 48.dp,
                        )
                    }
                }
            }
        }
    }
    OrganizationDialogs(
        state, moveIds?.toSet(), tagAudioId, viewModel,
        onDismiss = { moveIds = null; tagAudioId = null },
    )
}

@Composable
internal fun LibraryDropdown(
    label: String,
    choices: List<Pair<String?, String>>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onSelect: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(MaterialTheme.audioColors.card)
                .border(1.dp, MaterialTheme.audioColors.border)
                .clickable(enabled = enabled, role = Role.Button) { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.audioColors.mutedForeground,
            )
            Text("⌄", color = MaterialTheme.colorScheme.primary)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(min = 220.dp, max = 320.dp),
            shape = RectangleShape,
            containerColor = MaterialTheme.audioColors.card,
            border = BorderStroke(1.dp, MaterialTheme.audioColors.border),
            tonalElevation = 0.dp,
        ) {
            choices.forEach { (id, name) ->
                DropdownMenuItem(
                    text = { Text(name, style = MaterialTheme.typography.bodyMedium) },
                    onClick = { expanded = false; onSelect(id) },
                )
            }
        }
    }
}
