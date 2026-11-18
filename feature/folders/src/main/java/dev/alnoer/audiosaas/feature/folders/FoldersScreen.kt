package dev.alnoer.audiosaas.feature.folders

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioButtonStyle
import dev.alnoer.audiosaas.core.designsystem.component.AudioCard
import dev.alnoer.audiosaas.core.designsystem.component.AudioDestination
import dev.alnoer.audiosaas.core.designsystem.component.AudioIcon
import dev.alnoer.audiosaas.core.designsystem.component.AudioIconAsset
import dev.alnoer.audiosaas.core.designsystem.component.AudioLibraryMessage
import dev.alnoer.audiosaas.core.designsystem.component.AudioLibraryRow
import dev.alnoer.audiosaas.core.designsystem.component.AudioMainScaffold
import dev.alnoer.audiosaas.core.designsystem.component.AudioMenuAction
import dev.alnoer.audiosaas.core.designsystem.component.AudioPageTitle
import dev.alnoer.audiosaas.core.designsystem.component.AudioSectionHeading
import dev.alnoer.audiosaas.core.designsystem.component.AudioTabs
import dev.alnoer.audiosaas.core.designsystem.component.AudioTextAction
import dev.alnoer.audiosaas.core.designsystem.component.AudioTextField
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors
import dev.alnoer.audiosaas.library.Folder

@Composable
fun FoldersScreen(
    onDestinationSelected: (AudioDestination) -> Unit,
    onRecordingSelected: (String) -> Unit,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FoldersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var tagsTab by rememberSaveable { mutableStateOf(false) }
    var search by rememberSaveable { mutableStateOf("") }
    var editing by rememberSaveable { mutableStateOf(false) }
    var deleting by rememberSaveable { mutableStateOf(false) }
    var editId by rememberSaveable { mutableStateOf<String?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var seenRevision by rememberSaveable { mutableIntStateOf(state.revision) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }
    LaunchedEffect(state.revision) {
        if (seenRevision != state.revision) {
            editing = false
            deleting = false
            seenRevision = state.revision
        }
    }
    BackHandler(enabled = viewModel.parentId != null && !tagsTab) {
        viewModel.open(state.folders.find { it.id == viewModel.parentId }?.parentId)
    }
    fun edit(id: String?, value: String, delete: Boolean = false) {
        editId = id
        name = value
        viewModel.clearEditError()
        editing = !delete
        deleting = delete
    }

    val ancestors = remember(state.folders, viewModel.parentId) {
        val result = mutableListOf<Folder>()
        var current = state.folders.find { it.id == viewModel.parentId }
        while (current != null && result.none { it.id == current.id }) {
            result.add(0, current)
            val parent = current.parentId
            current = state.folders.find { it.id == parent }
        }
        result
    }
    val entries = (if (tagsTab) state.tags.map { it.id to it.name }
    else state.children.map { it.id to it.name }).filter { it.second.contains(search, true) }
    val recordings = state.audio.filter { it.name.contains(search, true) }

    AudioMainScaffold(
        selectedDestination = AudioDestination.Folders,
        onDestinationSelected = onDestinationSelected,
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                AudioPageTitle("Библиотека")
                AudioTabs(
                    items = listOf("Папки", "Теги"),
                    selectedIndex = if (tagsTab) 1 else 0,
                    onSelected = { tagsTab = it == 1; search = "" },
                )
            }
            item {
                AudioButton(
                    text = if (tagsTab) "Новый тег" else "Новая папка",
                    onClick = { edit(null, "") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.loading && !state.busy,
                    leadingIcon = {
                        AudioIcon(
                            AudioIconAsset.Add, null, Modifier.size(16.dp),
                            MaterialTheme.colorScheme.onPrimary,
                        )
                    },
                )
            }
            item {
                AudioTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = if (tagsTab) "Поиск по тегам" else "Поиск в папке",
                )
            }
            if (!tagsTab && ancestors.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AudioTextAction(
                            "Все папки", { viewModel.open(null) },
                            Modifier.heightIn(min = 48.dp),
                        )
                        ancestors.forEach { folder ->
                            Text("/", color = MaterialTheme.audioColors.mutedForeground)
                            AudioTextAction(
                                folder.name, { viewModel.open(folder.id) },
                                Modifier.heightIn(min = 48.dp),
                            )
                        }
                    }
                }
            }
            if (state.loading) {
                item {
                    AudioLibraryMessage("Загружаем библиотеку", "Папки, теги и ваши записи.", loading = true)
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
                    AudioSectionHeading(
                        if (tagsTab) "ВАШИ ТЕГИ"
                        else if (viewModel.parentId == null) "ВАШИ ПАПКИ" else "ПОДПАПКИ",
                    ) {
                        Text(
                            entries.size.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
                items(entries, key = { "collection-" + it.first }) { (id, title) ->
                    AudioLibraryRow(
                        title = if (tagsTab) "# $title" else title,
                        subtitle = if (tagsTab) "Записи с этим тегом" else "Открыть папку",
                        icon = if (tagsTab) null else AudioIconAsset.Folder,
                        onClick = { if (tagsTab) onTagSelected(id) else viewModel.open(id) },
                        enabled = !state.busy,
                        actions = listOf(
                            AudioMenuAction("Переименовать", { edit(id, title) }),
                            AudioMenuAction("Удалить", { edit(id, title, true) }, destructive = true),
                        ),
                    )
                }
                if (entries.isEmpty()) {
                    item {
                        AudioLibraryMessage(
                            title = if (search.isNotBlank()) "Ничего не найдено"
                            else if (tagsTab) "Пока нет тегов" else "Пока нет папок",
                            description = if (search.isNotBlank()) "Попробуйте другое имя."
                            else if (tagsTab) "Добавьте теги, чтобы быстро находить нужные записи."
                            else "Создайте папку для встреч, интервью или идей.",
                        )
                    }
                }
                if (!tagsTab) {
                    item {
                        AudioSectionHeading("ЗАПИСИ В ПАПКЕ") {
                            Text(
                                recordings.size.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                    items(recordings, key = { "audio-" + it.id }) { audio ->
                        AudioLibraryRow(
                            title = audio.name,
                            subtitle = audio.tags.joinToString(" · ") { "# " + it.name },
                            icon = AudioIconAsset.SaasMark,
                            onClick = { onRecordingSelected(audio.id) },
                        )
                    }
                    if (recordings.isEmpty()) {
                        item {
                            AudioLibraryMessage(
                                "Здесь пока нет записей",
                                "Переместите записи из истории в эту папку.",
                            )
                        }
                    }
                }
            }
        }
    }

    if (editing || deleting) {
        Dialog(onDismissRequest = { if (!state.busy) { editing = false; deleting = false } }) {
            AudioCard {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    AudioSectionHeading(if (tagsTab) "ТЕГИ" else "ПАПКИ")
                    Text(
                        text = if (deleting) "Удалить «$name»?"
                        else if (editId != null) "Переименовать"
                        else if (tagsTab) "Новый тег" else "Новая папка",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    if (deleting) {
                        Text(
                            text = if (tagsTab) "Тег будет снят со всех записей. Записи сохранятся."
                            else "Записи сохранятся в корне библиотеки. Сначала нужно удалить подпапки.",
                            color = MaterialTheme.audioColors.mutedForeground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        AudioTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = if (tagsTab) "Например, Важное" else "Например, Интервью",
                            label = "НАЗВАНИЕ",
                            enabled = !state.busy,
                        )
                        Text(
                            "${name.trim().length} / 100",
                            color = MaterialTheme.audioColors.mutedForeground,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    state.editError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                    AudioButton(
                        text = if (state.busy) "Сохранение…"
                        else if (deleting) "Удалить" else "Сохранить",
                        onClick = {
                            if (deleting) viewModel.delete(tagsTab, requireNotNull(editId))
                            else viewModel.save(tagsTab, editId, name)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        height = 48.dp,
                        enabled = !state.busy && (deleting || name.trim().length in 1..100),
                        style = if (deleting) AudioButtonStyle.Destructive
                        else AudioButtonStyle.Primary,
                    )
                    AudioButton(
                        "Отмена", { editing = false; deleting = false },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.busy,
                        style = AudioButtonStyle.Outline,
                        height = 48.dp,
                    )
                }
            }
        }
    }
}
