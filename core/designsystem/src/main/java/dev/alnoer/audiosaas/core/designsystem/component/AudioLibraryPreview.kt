package dev.alnoer.audiosaas.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.core.designsystem.theme.AudioSaaSTheme

@Preview(widthDp = 360, showBackground = true)
@Composable
private fun AudioLibraryPreview() {
    AudioSaaSTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AudioPageTitle("Библиотека")
                AudioTabs(listOf("Папки", "Теги"), 0, {})
                AudioSectionHeading("ВАШИ ПАПКИ")
                AudioLibraryRow(
                    title = "Командные встречи",
                    subtitle = "Открыть папку",
                    icon = AudioIconAsset.Folder,
                    onClick = {},
                    actions = listOf(AudioMenuAction("Переименовать", {})),
                )
                AudioSectionHeading("ЗАПИСИ")
                AudioLibraryRow(
                    title = "Интервью с пользователем.webm",
                    subtitle = "Интервью · # Исследование",
                    status = "ГОТОВО",
                    onClick = {},
                    actions = listOf(AudioMenuAction("Переместить в папку", {})),
                )
                AudioLibraryRow(
                    title = "Обсуждение планов команды.m4a",
                    subtitle = "Без папки",
                    status = "В ОЧЕРЕДИ",
                    selected = true,
                    onClick = {},
                )
                AudioLibraryMessage(
                    "Пока нет тегов",
                    "Добавьте теги, чтобы быстро находить нужные записи.",
                )
            }
        }
    }
}
