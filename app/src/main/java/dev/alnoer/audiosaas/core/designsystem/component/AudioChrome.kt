package dev.alnoer.audiosaas.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.ui.theme.audioColors

@Composable
fun AudioTopBar(
    modifier: Modifier = Modifier,
    title: String = "SaaS",
    showBrand: Boolean = true,
    navigationIcon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val borderColor = MaterialTheme.audioColors.sidebarBorder
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.audioColors.sidebar)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = androidx.compose.ui.geometry.Offset(0f, size.height),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        navigationIcon?.invoke()
        if (navigationIcon != null) Spacer(Modifier.width(10.dp))
        if (showBrand) {
            AudioIcon(
                asset = AudioIconAsset.SaasMark,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(10.dp))
        }
        Text(
            text = title,
            color = MaterialTheme.audioColors.sidebarForeground,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(Modifier.weight(1f))
        action?.invoke()
    }
}

enum class AudioDestination(
    val label: String,
    val icon: AudioIconAsset,
) {
    Overview("Обзор", AudioIconAsset.Overview),
    Recordings("Записи", AudioIconAsset.History),
    Folders("Папки", AudioIconAsset.Folder),
}

@Composable
fun AudioBottomNavigation(
    selected: AudioDestination,
    onDestinationSelected: (AudioDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = MaterialTheme.audioColors.sidebarBorder
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.audioColors.sidebar)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = androidx.compose.ui.geometry.Offset.Zero,
                    end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .padding(horizontal = 22.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AudioDestination.entries.forEach { destination ->
            val isSelected = destination == selected
            val color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.audioColors.mutedForeground
            }
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .width(90.dp)
                    .height(40.dp)
                    .clickable(
                        role = Role.Tab,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onDestinationSelected(destination) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AudioIcon(
                    asset = destination.icon,
                    contentDescription = destination.label,
                    modifier = Modifier.size(20.dp),
                    tint = color,
                )
                Text(
                    text = destination.label,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.audioColors.mutedForeground
                    },
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
fun AudioTextAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.clickable(
            role = Role.Button,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun AudioMainScaffold(
    selectedDestination: AudioDestination,
    onDestinationSelected: (AudioDestination) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AudioTopBar()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            content = content,
        )
        AudioBottomNavigation(
            selected = selectedDestination,
            onDestinationSelected = onDestinationSelected,
        )
    }
}

@Composable
fun AudioPageTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
