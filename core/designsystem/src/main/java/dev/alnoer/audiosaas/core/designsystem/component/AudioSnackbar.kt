package dev.alnoer.audiosaas.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.core.designsystem.theme.AudioSaaSTheme
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors

enum class AudioSnackbarType {
    Info,
    Error,
}

@Immutable
data class AudioSnackbarVisuals(
    override val message: String,
    val type: AudioSnackbarType = AudioSnackbarType.Info,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals

suspend fun SnackbarHostState.showAudioSnackbar(
    message: String,
    type: AudioSnackbarType = AudioSnackbarType.Info,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult = showSnackbar(
    AudioSnackbarVisuals(
        message = message,
        type = type,
        actionLabel = actionLabel,
        withDismissAction = withDismissAction,
        duration = duration,
    ),
)

@Composable
fun AudioSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { data ->
        AudioSnackbar(data = data)
    }
}

@Composable
fun AudioSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
) {
    val visuals = data.visuals
    AudioSnackbarContent(
        message = visuals.message,
        type = (visuals as? AudioSnackbarVisuals)?.type ?: AudioSnackbarType.Info,
        actionLabel = visuals.actionLabel,
        withDismissAction = visuals.withDismissAction,
        onAction = data::performAction,
        onDismiss = data::dismiss,
        modifier = modifier,
    )
}

@Composable
private fun AudioSnackbarContent(
    message: String,
    type: AudioSnackbarType,
    actionLabel: String?,
    withDismissAction: Boolean,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accentColor = when (type) {
        AudioSnackbarType.Info -> MaterialTheme.colorScheme.primary
        AudioSnackbarType.Error -> MaterialTheme.colorScheme.error
    }
    val title = when (type) {
        AudioSnackbarType.Info -> "Информация"
        AudioSnackbarType.Error -> "Ошибка"
    }

    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .semantics {
                liveRegion = LiveRegionMode.Polite
                paneTitle = title
            },
        shape = RectangleShape,
        color = MaterialTheme.audioColors.popover,
        contentColor = MaterialTheme.audioColors.popoverForeground,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .drawBehind {
                    drawRect(
                        color = accentColor,
                        size = size.copy(width = 4.dp.toPx()),
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title.uppercase(),
                    color = accentColor,
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (actionLabel != null) {
                Box(
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clickable(
                            role = Role.Button,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onAction,
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = actionLabel,
                        color = accentColor,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            if (withDismissAction) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .semantics { contentDescription = "Закрыть" }
                        .clickable(
                            role = Role.Button,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismiss,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "×",
                        color = MaterialTheme.audioColors.mutedForeground,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AudioSnackbarPreview() {
    AudioSaaSTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AudioSnackbarContent(
                message = "Запись сохранена в историю",
                type = AudioSnackbarType.Info,
                actionLabel = null,
                withDismissAction = false,
                onAction = {},
                onDismiss = {},
            )
            AudioSnackbarContent(
                message = "Не удалось войти. Проверьте подключение и попробуйте снова.",
                type = AudioSnackbarType.Error,
                actionLabel = "Повторить",
                withDismissAction = true,
                onAction = {},
                onDismiss = {},
            )
        }
    }
}
