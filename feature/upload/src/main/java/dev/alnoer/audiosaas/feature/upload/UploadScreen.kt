package dev.alnoer.audiosaas.feature.upload

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.material3.IconButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioButtonStyle
import dev.alnoer.audiosaas.core.designsystem.component.AudioCard
import dev.alnoer.audiosaas.core.designsystem.component.AudioIcon
import dev.alnoer.audiosaas.core.designsystem.component.AudioIconAsset
import dev.alnoer.audiosaas.core.designsystem.component.AudioPageTitle
import dev.alnoer.audiosaas.core.designsystem.component.AudioTopBar
import dev.alnoer.audiosaas.core.designsystem.theme.AudioSaaSTheme
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors

@Composable
fun UploadRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UploadViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val documentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) viewModel.upload(uri)
    }

    var pickerOpened by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!pickerOpened) {
            pickerOpened = true
            documentLauncher.launch(arrayOf("*/*"))
        }
    }

    UploadScreen(
        uiState = uiState,
        onBack = onBack,
        onChooseFile = { documentLauncher.launch(arrayOf("*/*")) },
        modifier = modifier,
    )
}

@Composable
fun UploadScreen(
    uiState: UploadUiState,
    onBack: () -> Unit,
    onChooseFile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uploading = uiState == UploadUiState.Uploading

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AudioTopBar(
            title = "Новая запись",
            showBrand = false,
            navigationIcon = {
                IconButton(onClick = onBack, enabled = !uploading) {
                    AudioIcon(
                        asset = AudioIconAsset.ArrowLeft,
                        contentDescription = "Назад",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AudioPageTitle("Загрузка файла")
            Text(
                text = "Выберите файл на устройстве. После выбора загрузка начнётся автоматически.",
                color = MaterialTheme.audioColors.mutedForeground,
                style = MaterialTheme.typography.bodyMedium,
            )
            AudioCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    AudioIcon(
                        asset = AudioIconAsset.Folder,
                        contentDescription = null,
                        modifier = Modifier.padding(top = 8.dp).size(40.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = when (uiState) {
                            UploadUiState.Idle -> "АУДИО ИЛИ ВИДЕО"
                            UploadUiState.Uploading -> "ЗАГРУЗКА"
                            UploadUiState.Success -> "ЗАГРУЖЕНО"
                            is UploadUiState.Failure -> "НЕ УДАЛОСЬ ЗАГРУЗИТЬ"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.audioColors.mutedForeground,
                    )
                    when (uiState) {
                        UploadUiState.Idle -> StatusText("Файл ещё не выбран")
                        UploadUiState.Uploading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary,
                            )
                            StatusText("Загрузка файла…")
                        }
                        UploadUiState.Success -> StatusText("Файл успешно загружен")
                        is UploadUiState.Failure -> {
                            Text(
                                text = uiState.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    AudioButton(
                        text = if (uiState == UploadUiState.Success) {
                            "Выбрать другой файл"
                        } else {
                            "Выбрать файл"
                        },
                        onClick = onChooseFile,
                        modifier = Modifier.fillMaxWidth(),
                        style = if (uiState is UploadUiState.Failure) {
                            AudioButtonStyle.Outline
                        } else {
                            AudioButtonStyle.Primary
                        },
                        enabled = !uploading,
                        height = 48.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@PreviewScreenSizes
@Composable
private fun UploadScreenPreview() {
    AudioSaaSTheme {
        Surface {
            UploadScreen(
                uiState = UploadUiState.Idle,
                onBack = {},
                onChooseFile = {},
            )
        }
    }
}
