package dev.alnoer.audiosaas.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alnoer.audiosaas.ui.theme.Newsreader
import dev.alnoer.audiosaas.ui.theme.audioColors

data class AudioOnboardingPage(
    val title: String,
    val description: String,
    val summary: String,
)

@Composable
fun AudioOnboardingCard(
    page: AudioOnboardingPage,
    index: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.audioColors
    Column(
        modifier = modifier
            .width(300.dp)
            .height(390.dp)
            .background(colors.card)
            .border(
                width = 1.dp,
                color = if (selected) colors.onboardingAccent else colors.onboardingBorder,
            )
            .padding(24.dp),
    ) {
        Text(
            text = "0${index + 1} / 03",
            color = if (selected) colors.onboardingAccent else colors.onboardingMuted,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = page.title,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
                fontFamily = Newsreader,
                fontWeight = FontWeight.Normal,
                fontSize = if (index == 0) 28.sp else 30.sp,
                lineHeight = if (index == 0) 33.sp else 35.sp,
            ),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = page.description,
            color = colors.mutedForeground,
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = page.summary,
            color = if (selected) colors.onboardingAccent else colors.mutedForeground,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
fun AudioPagerIndicator(
    pageCount: Int,
    selectedPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .width(if (index == selectedPage) 20.dp else 6.dp)
                    .height(6.dp)
                    .background(
                        if (index == selectedPage) {
                            MaterialTheme.audioColors.onboardingAccent
                        } else {
                            MaterialTheme.audioColors.onboardingBorder
                        },
                    ),
            )
        }
    }
}
