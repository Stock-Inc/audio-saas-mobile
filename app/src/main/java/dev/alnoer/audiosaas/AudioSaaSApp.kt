package dev.alnoer.audiosaas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.alnoer.audiosaas.core.designsystem.component.AudioDestination
import dev.alnoer.audiosaas.feature.auth.SignInScreen
import dev.alnoer.audiosaas.feature.auth.SignUpScreen
import dev.alnoer.audiosaas.feature.folders.FoldersScreen
import dev.alnoer.audiosaas.feature.home.OverviewScreen
import dev.alnoer.audiosaas.feature.onboarding.OnboardingScreen
import dev.alnoer.audiosaas.feature.recordings.HistoryScreen
import dev.alnoer.audiosaas.feature.recordings.ResultScreen

private object AppRoute {
    const val ONBOARDING = "onboarding"
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"
    const val OVERVIEW = "overview"
    const val HISTORY = "history"
    const val FOLDERS = "folders"
    const val RESULT = "result"
}

@Composable
fun AudioSaaSApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    fun navigate(route: String) {
        navController.navigate(route)
    }
    fun selectDestination(destination: AudioDestination) {
        navigate(
            when (destination) {
                AudioDestination.Overview -> AppRoute.OVERVIEW
                AudioDestination.Recordings -> AppRoute.HISTORY
                AudioDestination.Folders -> AppRoute.FOLDERS
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.ONBOARDING,
        modifier = modifier,
    ) {
        composable(AppRoute.ONBOARDING) {
            OnboardingScreen(
                onFinished = { navigate(AppRoute.SIGN_IN) },
                modifier = Modifier,
            )
        }
        composable(AppRoute.SIGN_IN) {
            SignInScreen(
                onSignIn = { navigate(AppRoute.OVERVIEW) },
                onCreateAccount = { navigate(AppRoute.SIGN_UP) },
                modifier = Modifier,
            )
        }
        composable(AppRoute.SIGN_UP) {
            SignUpScreen(
                onCreateAccount = { navigate(AppRoute.OVERVIEW) },
                onSignIn = { navigate(AppRoute.SIGN_IN) },
                modifier = Modifier,
            )
        }
        composable(AppRoute.OVERVIEW) {
            OverviewScreen(
                onDestinationSelected = ::selectDestination,
                onRecordingSelected = { navigate(AppRoute.RESULT) },
                onNewRecording = { navigate(AppRoute.RESULT) },
                onViewAll = { navigate(AppRoute.HISTORY) },
                modifier = Modifier,
            )
        }
        composable(AppRoute.HISTORY) {
            HistoryScreen(
                onDestinationSelected = ::selectDestination,
                onRecordingSelected = { navigate(AppRoute.RESULT) },
                onNewRecording = { navigate(AppRoute.RESULT) },
                modifier = Modifier,
            )
        }
        composable(AppRoute.FOLDERS) {
            FoldersScreen(
                onDestinationSelected = ::selectDestination,
                modifier = Modifier,
            )
        }
        composable(AppRoute.RESULT) {
            ResultScreen(
                onBack = { navController.popBackStack() },
                modifier = Modifier,
            )
        }
    }
}
