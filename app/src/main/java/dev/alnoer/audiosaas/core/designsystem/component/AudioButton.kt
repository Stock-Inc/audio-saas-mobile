package dev.alnoer.audiosaas.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.ui.theme.audioColors

enum class AudioButtonStyle {
    Primary,
    Secondary,
    Destructive,
    Outline,
    Ghost,
}

@Composable
fun AudioButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AudioButtonStyle = AudioButtonStyle.Primary,
    enabled: Boolean = true,
    height: Dp = 40.dp,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val colors = MaterialTheme.audioColors
    val (containerColor, contentColor) = when (style) {
        AudioButtonStyle.Primary -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        AudioButtonStyle.Secondary -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        AudioButtonStyle.Destructive -> MaterialTheme.colorScheme.error to colors.destructiveForeground
        AudioButtonStyle.Outline -> Color.Transparent to MaterialTheme.colorScheme.onBackground
        AudioButtonStyle.Ghost -> Color.Transparent to MaterialTheme.colorScheme.onBackground
    }
    val border = when (style) {
        AudioButtonStyle.Outline -> BorderStroke(1.dp, colors.border)
        else -> null
    }

    Box(
        modifier = modifier
            .height(height)
            .then(if (border == null) Modifier else Modifier.border(border, RectangleShape))
            .background(containerColor, RectangleShape)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
