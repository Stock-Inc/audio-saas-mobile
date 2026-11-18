package dev.alnoer.audiosaas.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alnoer.audiosaas.core.designsystem.theme.audioColors

data class AudioMenuAction(
    val label: String,
    val onClick: () -> Unit,
    val destructive: Boolean = false,
)

@Composable
fun AudioSectionHeading(
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.audioColors.mutedForeground,
            style = MaterialTheme.typography.labelSmall,
        )
        trailing()
    }
}

@Composable
fun AudioLibraryRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: AudioIconAsset? = null,
    status: String? = null,
    failed: Boolean = false,
    selected: Boolean? = null,
    actions: List<AudioMenuAction> = emptyList(),
    enabled: Boolean = true,
) {
    AudioCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (selected == null) {
                            Modifier.clickable(
                                enabled = enabled, role = Role.Button, onClick = onClick,
                            )
                        } else {
                            Modifier.toggleable(
                                value = selected, enabled = enabled,
                                role = Role.Checkbox, onValueChange = { onClick() },
                            )
                        },
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (selected != null) {
                    Checkbox(
                        checked = selected,
                        onCheckedChange = null,
                        enabled = enabled,
                    )
                } else if (icon != null) {
                    AudioIcon(
                        asset = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    if (subtitle.isNotEmpty()) {
                        Text(
                            text = subtitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.audioColors.mutedForeground,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    if (status != null) {
                        Text(
                            text = status,
                            color = if (failed) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
            if (actions.isNotEmpty() && selected == null) {
                AudioOverflowMenu(title, actions, enabled)
            }
        }
    }
}

@Composable
private fun AudioOverflowMenu(
    title: String,
    actions: List<AudioMenuAction>,
    enabled: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { expanded = true },
            enabled = enabled,
            modifier = Modifier.semantics { contentDescription = "Действия: $title" },
        ) {
            Text(
                text = "⋮",
                fontSize = 24.sp,
                color = MaterialTheme.audioColors.mutedForeground,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(min = 200.dp, max = 320.dp),
            shape = RectangleShape,
            containerColor = MaterialTheme.audioColors.card,
            border = BorderStroke(1.dp, MaterialTheme.audioColors.border),
            tonalElevation = 0.dp,
        ) {
            actions.forEach { action ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = action.label,
                            color = if (action.destructive) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    onClick = {
                        expanded = false
                        action.onClick()
                    },
                )
            }
        }
    }
}

@Composable
fun AudioLibraryMessage(
    title: String,
    description: String,
    loading: Boolean = false,
    error: Boolean = false,
    onRetry: (() -> Unit)? = null,
) {
    AudioCard {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text(
                text = title,
                color = if (error) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = description,
                color = MaterialTheme.audioColors.mutedForeground,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (onRetry != null) {
                AudioButton("Повторить", onRetry, style = AudioButtonStyle.Outline)
            }
        }
    }
}
