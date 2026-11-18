package dev.alnoer.audiosaas.feature.recordings

import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alnoer.audiosaas.core.designsystem.component.AudioBadge
import dev.alnoer.audiosaas.core.designsystem.component.AudioBadgeStyle
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioButtonStyle
import dev.alnoer.audiosaas.core.designsystem.component.AudioCard
import dev.alnoer.audiosaas.core.designsystem.component.AudioIcon
import dev.alnoer.audiosaas.core.designsystem.component.AudioIconAsset
import dev.alnoer.audiosaas.core.designsystem.component.AudioLibraryMessage
import dev.alnoer.audiosaas.core.designsystem.component.AudioSectionHeading
import dev.alnoer.audiosaas.core.designsystem.component.AudioTopBar
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors
import dev.alnoer.audiosaas.library.Folder
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal fun folderPath(id: String, folders: List<Folder>): String {
    val names = mutableListOf<String>()
    val visited = mutableSetOf<String>()
    var current = folders.find { it.id == id }
    while (current != null && visited.add(current.id)) {
        names.add(0, current.name)
        val parent = current.parentId
        current = folders.find { it.id == parent }
    }
    return names.joinToString(" / ")
}

internal fun statusLabel(status: String): String = when (status) {
    "RECEIVED" -> "ПОЛУЧЕНО"
    "FORWARDED" -> "В ОЧЕРЕДИ"
    "PROCESSING" -> "ОБРАБОТКА"
    "COMPLETED" -> "ГОТОВО"
    "FAILED" -> "ОШИБКА"
    else -> status
}

internal fun recordingDate(value: String): String = try {
    DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale.getDefault())
        .withZone(ZoneId.systemDefault())
        .format(Instant.parse(value))
} catch (_: java.time.DateTimeException) {
    value
}

@Composable
fun RecordingOrganizationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var moving by rememberSaveable { mutableStateOf(false) }
    var tagging by rememberSaveable { mutableStateOf(false) }
    var seenRevision by rememberSaveable { mutableIntStateOf(state.revision) }
    val audio = state.audio.singleOrNull()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }
    LaunchedEffect(state.revision) {
        if (seenRevision != state.revision) {
            moving = false
            tagging = false
            seenRevision = state.revision
        }
    }
    Column(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        AudioTopBar(
            title = "К записям",
            showBrand = false,
            navigationIcon = {
                IconButton(onClick = onBack) {
                    AudioIcon(
                        AudioIconAsset.ArrowLeft, "Назад к записям",
                        Modifier.size(20.dp), MaterialTheme.colorScheme.onBackground,
                    )
                }
            },
        )
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.loading) {
                AudioLibraryMessage("Загружаем запись", "Информация об аудиофайле.", loading = true)
            } else if (state.error != null) {
                AudioLibraryMessage(
                    "Запись недоступна", state.error.orEmpty(),
                    error = true, onRetry = viewModel::refresh,
                )
            } else if (audio != null) {
                AudioSectionHeading("АУДИОЗАПИСЬ") {
                    AudioBadge(
                        text = statusLabel(audio.status),
                        style = if (audio.status == "FAILED") AudioBadgeStyle.Destructive
                        else AudioBadgeStyle.Secondary,
                    )
                }
                Text(
                    text = audio.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                audio.errorMessage?.let { message ->
                    AudioLibraryMessage("Ошибка обработки", message, error = true)
                }
                AudioCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AudioSectionHeading("О ФАЙЛЕ")
                        Text(
                            text = Formatter.formatShortFileSize(
                                LocalContext.current, audio.sizeBytes,
                            ),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            text = "Загружено " + recordingDate(audio.createdAt),
                            color = MaterialTheme.audioColors.mutedForeground,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                AudioCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        AudioSectionHeading("РАСПОЛОЖЕНИЕ")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AudioIcon(
                                AudioIconAsset.Folder, null, Modifier.size(24.dp),
                                MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = audio.folder?.let { folderPath(it.id, state.folders) }
                                    ?: "Без папки",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        AudioButton(
                            text = "Переместить в папку",
                            onClick = { viewModel.clearEditError(); moving = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.busy,
                            style = AudioButtonStyle.Outline,
                        )
                    }
                }
                AudioCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        AudioSectionHeading("ТЕГИ")
                        if (audio.tags.isEmpty()) {
                            Text(
                                "Добавьте теги, чтобы проще находить эту запись.",
                                color = MaterialTheme.audioColors.mutedForeground,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                audio.tags.forEach { tag ->
                                    Text(
                                        text = "# " + tag.name,
                                        modifier = Modifier
                                            .border(1.dp, MaterialTheme.audioColors.border)
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        }
                        AudioButton(
                            text = "Изменить теги",
                            onClick = { viewModel.clearEditError(); tagging = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.busy,
                            style = AudioButtonStyle.Outline,
                        )
                    }
                }
            }
        }
    }
    OrganizationDialogs(
        state = state,
        moveIds = if (moving) state.audio.map { it.id }.toSet() else null,
        tagAudioId = if (tagging) audio?.id else null,
        viewModel = viewModel,
        onDismiss = { moving = false; tagging = false },
    )
}

@Composable
internal fun OrganizationDialogs(
    state: LibraryState,
    moveIds: Set<String>?,
    tagAudioId: String?,
    viewModel: LibraryViewModel,
    onDismiss: () -> Unit,
) {
    if (moveIds == null && tagAudioId == null) return
    var destination by rememberSaveable(moveIds) { mutableStateOf<String?>(null) }
    var selectedTags by rememberSaveable(tagAudioId) {
        mutableStateOf(state.audio.find { it.id == tagAudioId }?.tags?.map { it.id }.orEmpty())
    }
    Dialog(onDismissRequest = { if (!state.busy) onDismiss() }) {
        AudioCard {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AudioSectionHeading(if (moveIds != null) "РАСПОЛОЖЕНИЕ" else "ОРГАНИЗАЦИЯ")
                Text(
                    text = if (moveIds != null) "Переместить в папку" else "Теги записи",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = if (moveIds != null) "Выберите новое место для записей."
                    else "Отметьте подходящие теги. Пустой выбор снимет все теги.",
                    color = MaterialTheme.audioColors.mutedForeground,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (moveIds != null) {
                    LibraryDropdown(
                        label = destination?.let { folderPath(it, state.folders) } ?: "Без папки",
                        choices = listOf(null to "Без папки (корень)") +
                            state.folders.map { it.id to folderPath(it.id, state.folders) },
                        enabled = !state.busy,
                        onSelect = { destination = it },
                    )
                } else {
                    if (state.tags.isEmpty()) {
                        Text(
                            "Создайте первый тег в разделе «Библиотека → Теги».",
                            color = MaterialTheme.audioColors.mutedForeground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    state.tags.forEach { tag ->
                        val checked = tag.id in selectedTags
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (checked) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.audioColors.border,
                                )
                                .toggleable(
                                    value = checked,
                                    enabled = !state.busy,
                                    role = Role.Checkbox,
                                    onValueChange = {
                                        selectedTags = if (it) selectedTags + tag.id
                                        else selectedTags - tag.id
                                    },
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Checkbox(checked = checked, onCheckedChange = null)
                            Text(
                                "# " + tag.name,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
                state.editError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
                AudioButton(
                    text = if (state.busy) "Сохранение…" else "Сохранить",
                    onClick = {
                        if (moveIds != null) viewModel.move(moveIds, destination)
                        else viewModel.assignTags(requireNotNull(tagAudioId), selectedTags.toSet())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    height = 48.dp,
                    enabled = !state.busy,
                )
                AudioButton(
                    "Отмена", onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.busy,
                    style = AudioButtonStyle.Outline,
                    height = 48.dp,
                )
            }
        }
    }
}
