package dev.alnoer.audiosaas.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alnoer.audiosaas.core.designsystem.component.AudioButton
import dev.alnoer.audiosaas.core.designsystem.component.AudioOnboardingCard
import dev.alnoer.audiosaas.core.designsystem.component.AudioOnboardingPage
import dev.alnoer.audiosaas.core.designsystem.component.AudioPagerIndicator
import dev.alnoer.audiosaas.core.designsystem.component.AudioTextAction
import dev.alnoer.audiosaas.core.designsystem.component.AudioTopBar
import kotlinx.coroutines.launch

private val OnboardingPages = listOf(
    AudioOnboardingPage(
        title = "Добро\nпожаловать в SaaS",
        description = "Превращайте разговоры и встречи в понятный текст без ручной расшифровки.",
        summary = "Теперь с поиском!",
    ),
    AudioOnboardingPage(
        title = "Точная расшифровка",
        description = "SaaS определит спикеров, добавит таймкоды и сохранит каждую запись в истории.",
        summary = "СПИКЕРЫ · ТАЙМКОДЫ · ИСТОРИЯ",
    ),
    AudioOnboardingPage(
        title = "Коротко о главном",
        description = "AI выделит ключевые мысли, решения и следующие шаги без лишнего шума.",
        summary = "СВОДКА · РЕШЕНИЯ · ДЕЙСТВИЯ",
    ),
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { OnboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        AudioTopBar(
            action = {
                Box(
                    modifier = Modifier
                        .width(116.dp)
                        .height(36.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    AudioTextAction(
                        text = "Пропустить",
                        onClick = onFinished,
                    )
                }
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            androidx.compose.foundation.layout.Spacer(Modifier.height(86.dp))
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(390.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSize = PageSize.Fixed(300.dp),
                pageSpacing = 16.dp,
            ) { page ->
                AudioOnboardingCard(
                    page = OnboardingPages[page],
                    index = page,
                    selected = page == pagerState.currentPage,
                )
            }
            androidx.compose.foundation.layout.Spacer(Modifier.height(30.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                AudioPagerIndicator(
                    pageCount = OnboardingPages.size,
                    selectedPage = pagerState.currentPage,
                )
            }
            androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
            AudioButton(
                text = if (pagerState.currentPage == OnboardingPages.lastIndex) {
                    "Войти"
                } else {
                    "Далее"
                },
                onClick = {
                    if (pagerState.currentPage == OnboardingPages.lastIndex) {
                        onFinished()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(80.dp))
        }
    }
}
