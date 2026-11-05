package dev.alnoer.audiosaas.feature.upload

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
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

    UploadScreen(
        uiState = uiState,
        onBack = onBack,
        onChooseFile = { documentLauncher.launch(arrayOf("audio/*")) },
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
                AudioIcon(
                    asset = AudioIconAsset.ArrowLeft,
                    contentDescription = "Назад",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(
                            enabled = !uploading,
                            role = Role.Button,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBack,
                        ),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AudioPageTitle("Загрузка аудио")
            Text(
                text = "Выберите аудиофайл на устройстве. После выбора загрузка начнётся автоматически.",
                color = MaterialTheme.audioColors.mutedForeground,
                style = MaterialTheme.typography.bodyMedium,
            )
            AudioCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
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
                            "Выбрать аудиофайл"
                        },
                        onClick = onChooseFile,
                        modifier = Modifier.fillMaxWidth(),
                        style = if (uiState is UploadUiState.Failure) {
                            AudioButtonStyle.Outline
                        } else {
                            AudioButtonStyle.Primary
                        },
                        enabled = !uploading,
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
