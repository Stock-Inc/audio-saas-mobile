package dev.alnoer.audiosaas.feature.upload

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.size
import dev.alnoer.audiosaas.core.designsystem.component.AudioIcon
import dev.alnoer.audiosaas.core.designsystem.component.AudioIconAsset
import dev.alnoer.audiosaas.core.designsystem.component.AudioButtonStyle
import dev.alnoer.audiosaas.core.designsystem.component.AudioConfirmationDialog
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioPageTitle
import dev.alnoer.audiosaas.core.designsystem.component.AudioTopBar

@Composable
fun VoiceRecordingRoute(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: VoiceRecordingViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var rationale by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var discardAction by remember { mutableStateOf<String?>(null) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    var playing by remember { mutableStateOf(false) }
    var playbackError by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) viewModel.start() else {
            permissionDenied = true
            showSettings = activity?.shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) == false
        }
    }

    fun stopPlayback() {
        player?.release()
        player = null
        playing = false
    }

    fun leave() {
        if (state.phase == RecordingPhase.Uploading) return
        if (state.file != null) {
            viewModel.stop()
            stopPlayback()
            discardAction = "leave"
        } else onBack()
    }

    DisposableEffect(lifecycle, activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && activity?.isChangingConfigurations != true) {
                viewModel.stop()
                stopPlayback()
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            stopPlayback()
        }
    }
    BackHandler { leave() }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.safeDrawingPadding().verticalScroll(rememberScrollState())) {
            AudioTopBar(
                title = "Новая запись",
                showBrand = false,
                navigationIcon = {
                    IconButton(onClick = ::leave, enabled = state.phase != RecordingPhase.Uploading) {
                        AudioIcon(AudioIconAsset.ArrowLeft, "Назад", Modifier.size(16.dp), MaterialTheme.colorScheme.onBackground)
                    }
                },
            )
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AudioPageTitle("Запись голоса")
                RecordingStatusCard(state.phase, state.seconds)
                Text(
                    "При блокировке экрана или выходе из приложения запись остановится.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.audioColors.mutedForeground,
                )
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                if (permissionDenied) Text("Разрешите доступ к микрофону, чтобы начать запись.")
                if (playbackError) Text("Не удалось воспроизвести запись.", color = MaterialTheme.colorScheme.error)
                when (state.phase) {
                    RecordingPhase.Ready -> {
                        AudioButton(
                            text = "Начать запись",
                            modifier = Modifier.fillMaxWidth(),
                            height = 48.dp,
                            onClick = {
                                permissionDenied = false
                                when {
                                    context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> viewModel.start()
                                    activity?.shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) == true -> rationale = true
                                    else -> permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                        )
                        if (showSettings) TextButton(onClick = {
                            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.packageName)))
                        }) { Text("Открыть настройки") }
                    }
                    RecordingPhase.Recording -> AudioButton(text = "Остановить запись", onClick = viewModel::stop, modifier = Modifier.fillMaxWidth(), height = 48.dp)
                    RecordingPhase.Review -> {
                        AudioButton(text = if (playing) "Пауза" else "Прослушать", modifier = Modifier.fillMaxWidth(), height = 48.dp, style = AudioButtonStyle.Outline, onClick = {
                            try {
                                if (playing) {
                                    player?.pause()
                                    playing = false
                                } else {
                                    if (player == null) {
                                        val newPlayer = MediaPlayer()
                                        player = newPlayer
                                        newPlayer.apply {
                                            setDataSource(checkNotNull(state.file).absolutePath)
                                            setOnCompletionListener { playing = false }
                                            setOnErrorListener { _, _, _ ->
                                                stopPlayback()
                                                playbackError = true
                                                true
                                            }
                                            prepare()
                                        }
                                    }
                                    player?.start()
                                    playing = true
                                    playbackError = false
                                }
                            } catch (_: Exception) {
                                stopPlayback()
                                playbackError = true
                            }
                        })
                        AudioButton(text = "Записать заново", modifier = Modifier.fillMaxWidth(), height = 48.dp, style = AudioButtonStyle.Ghost, onClick = {
                            stopPlayback()
                            discardAction = "again"
                        })
                        AudioButton(text = if (state.error == null) "Загрузить запись" else "Повторить загрузку", modifier = Modifier.fillMaxWidth(), height = 48.dp, onClick = {
                            stopPlayback()
                            viewModel.upload()
                        })
                    }
                    RecordingPhase.Uploading -> Text("Загрузка записи…")
                    RecordingPhase.Success -> Text("Запись успешно загружена")
                }
                if (state.phase == RecordingPhase.Success) {
                    AudioButton(text = "Готово", onClick = ::leave, modifier = Modifier.fillMaxWidth(), height = 48.dp)
                }
            }
        }
    }
    if (rationale) AudioConfirmationDialog(
        title = "Доступ к микрофону",
        message = "Микрофон нужен только для записи вашего голоса.",
        confirmLabel = "Продолжить",
        onConfirm = {
            rationale = false
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        },
        onDismiss = { rationale = false },
    )
    if (discardAction != null) AudioConfirmationDialog(
        title = "Удалить запись?",
        message = "Эта запись ещё не загружена и будет удалена.",
        confirmLabel = "Удалить запись",
        destructive = true,
        onConfirm = {
            val leaveScreen = discardAction == "leave"
            discardAction = null
            viewModel.discard()
            playbackError = false
            if (leaveScreen) onBack()
        },
        onDismiss = { discardAction = null },
    )
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
