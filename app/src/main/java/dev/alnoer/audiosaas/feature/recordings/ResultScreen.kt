package dev.alnoer.audiosaas.feature.recordings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alnoer.audiosaas.core.designsystem.component.AudioBadge
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioCompactTabs
import dev.alnoer.audiosaas.core.designsystem.component.AudioIcon
import dev.alnoer.audiosaas.core.designsystem.component.AudioIconAsset
import dev.alnoer.audiosaas.core.designsystem.component.AudioPlayer
import dev.alnoer.audiosaas.core.designsystem.component.AudioSummaryCard
import dev.alnoer.audiosaas.core.designsystem.component.AudioTopBar
import dev.alnoer.audiosaas.core.designsystem.component.AudioTranscriptExcerpt
import dev.alnoer.audiosaas.ui.theme.audioColors

@Composable
fun ResultScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val dividerColor = MaterialTheme.audioColors.sidebarBorder

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AudioTopBar(
            title = "К записям",
            showBrand = false,
            navigationIcon = {
                AudioIcon(
                    asset = AudioIconAsset.ArrowLeft,
                    contentDescription = "Назад к записям",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(
                            role = Role.Button,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBack,
                        ),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            },
            action = {
                AudioBadge(
                    text = "ГОТОВО",
                    modifier = Modifier.width(78.dp),
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "ИНТЕРВЬЮ · СЕГОДНЯ",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                )
                Text(
                    text = "Интервью с пользователем",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 23.sp,
                        lineHeight = 29.sp,
                    ),
                )
            }
            AudioPlayer(
                elapsed = "12:34",
                duration = "48:06",
                speed = "1.0×",
                onPlay = {},
            )
            AudioCompactTabs(
                items = listOf("Текст", "Сводка", "Задачи"),
                selectedIndex = selectedTab,
                onSelected = { selectedTab = it },
            )
            AudioTranscriptExcerpt(
                utterances = listOf(
                    "00:12 · СПИКЕР 1" to "Давайте сначала запустим загрузку и базовую транскрипцию.",
                    "00:28 · СПИКЕР 2" to "Согласен. Подготовлю pipeline и разделение спикеров.",
                ),
                modifier = Modifier.height(174.dp),
            )
            AudioSummaryCard(
                summary = "Запустить загрузку и распознавание. Добавить экспорт в следующей итерации.",
                decision = "РЕШЕНИЕ · MVP без внешних интеграций",
                taskCount = 3,
                modifier = Modifier.height(142.dp),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(MaterialTheme.audioColors.sidebar)
                .drawBehind {
                    drawLine(
                        color = dividerColor,
                        start = androidx.compose.ui.geometry.Offset.Zero,
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AudioButton(
                text = "Экспорт результата",
                onClick = {},
                modifier = Modifier.width(250.dp),
            )
            Box(
                modifier = Modifier
                    .width(92.dp)
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable(
                        role = Role.Button,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {},
                contentAlignment = Alignment.Center,
            ) {
                AudioIcon(
                    asset = AudioIconAsset.Share,
                    contentDescription = "Поделиться",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}
