package dev.alnoer.audiosaas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.alnoer.audiosaas.core.designsystem.component.AudioDestination
import dev.alnoer.audiosaas.feature.auth.SignInScreen
import dev.alnoer.audiosaas.feature.auth.SignUpScreen
import dev.alnoer.audiosaas.feature.folders.FoldersScreen
import dev.alnoer.audiosaas.feature.home.OverviewScreen
import dev.alnoer.audiosaas.feature.onboarding.OnboardingScreen
import dev.alnoer.audiosaas.feature.recordings.HistoryScreen
import dev.alnoer.audiosaas.feature.recordings.ResultScreen

private enum class AppScreen {
    Onboarding,
    SignIn,
    SignUp,
    Overview,
    History,
    Folders,
    Result,
}

@Composable
fun AudioSaaSApp(modifier: Modifier = Modifier) {
    var currentScreenName by rememberSaveable { mutableStateOf(AppScreen.Onboarding.name) }
    val currentScreen = AppScreen.valueOf(currentScreenName)
    val navigate: (AppScreen) -> Unit = { currentScreenName = it.name }
    val selectDestination: (AudioDestination) -> Unit = {
        navigate(
            when (it) {
                AudioDestination.Overview -> AppScreen.Overview
                AudioDestination.Recordings -> AppScreen.History
                AudioDestination.Folders -> AppScreen.Folders
            },
        )
    }

    when (currentScreen) {
        AppScreen.Onboarding -> OnboardingScreen(
            onFinished = { navigate(AppScreen.SignIn) },
            modifier = modifier,
        )

        AppScreen.SignIn -> SignInScreen(
            onSignIn = { navigate(AppScreen.Overview) },
            onCreateAccount = { navigate(AppScreen.SignUp) },
            modifier = modifier,
        )

        AppScreen.SignUp -> SignUpScreen(
            onCreateAccount = { navigate(AppScreen.Overview) },
            onSignIn = { navigate(AppScreen.SignIn) },
            modifier = modifier,
        )

        AppScreen.Overview -> OverviewScreen(
            onDestinationSelected = selectDestination,
            onRecordingSelected = { navigate(AppScreen.Result) },
            onNewRecording = { navigate(AppScreen.Result) },
            onViewAll = { navigate(AppScreen.History) },
            modifier = modifier,
        )

        AppScreen.History -> HistoryScreen(
            onDestinationSelected = selectDestination,
            onRecordingSelected = { navigate(AppScreen.Result) },
            onNewRecording = { navigate(AppScreen.Result) },
            modifier = modifier,
        )

        AppScreen.Folders -> FoldersScreen(
            onDestinationSelected = selectDestination,
            modifier = modifier,
        )

        AppScreen.Result -> ResultScreen(
            onBack = { navigate(AppScreen.History) },
            modifier = modifier,
        )
    }
}
