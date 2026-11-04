package dev.alnoer.audiosaas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.alnoer.audiosaas.core.designsystem.component.AudioDestination
import dev.alnoer.audiosaas.feature.auth.AuthState
import dev.alnoer.audiosaas.feature.auth.AuthViewModel
import dev.alnoer.audiosaas.feature.folders.FoldersScreen
import dev.alnoer.audiosaas.feature.home.OverviewScreen
import dev.alnoer.audiosaas.feature.onboarding.OnboardingScreen
import dev.alnoer.audiosaas.feature.recordings.HistoryScreen
import dev.alnoer.audiosaas.feature.recordings.ResultScreen

private object AppRoute {
    const val ONBOARDING = "onboarding"
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
            val authViewModel: AuthViewModel = hiltViewModel()

            val authLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                val intent = result.data ?: return@rememberLauncherForActivityResult
                authViewModel.processAuthResult(intent)
            }
            val authState = authViewModel.authState.collectAsStateWithLifecycle().value

            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Failure -> {}
                    AuthState.InProgress -> {}
                    AuthState.NotStarted -> {}
                    AuthState.Success -> navigate(AppRoute.OVERVIEW)
                }
            }

            OnboardingScreen(
                onFinished = {
                    if (authState is AuthState.NotStarted || authState is AuthState.Failure) {
                        authViewModel.getLoginIntent()?.let(authLauncher::launch)
                    }
                },
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
