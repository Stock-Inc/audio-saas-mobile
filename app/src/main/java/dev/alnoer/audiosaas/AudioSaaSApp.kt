package dev.alnoer.audiosaas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.alnoer.audiosaas.core.designsystem.component.AudioDestination
import dev.alnoer.audiosaas.core.designsystem.component.AudioSnackbarHost
import dev.alnoer.audiosaas.core.designsystem.component.AudioSnackbarType
import dev.alnoer.audiosaas.core.designsystem.component.showAudioSnackbar
import dev.alnoer.audiosaas.feature.auth.domain.model.AuthState
import dev.alnoer.audiosaas.feature.auth.presentation.AuthSessionViewModel
import dev.alnoer.audiosaas.feature.auth.presentation.AuthViewModel
import dev.alnoer.audiosaas.feature.folders.FoldersScreen
import dev.alnoer.audiosaas.feature.home.OverviewScreen
import dev.alnoer.audiosaas.feature.onboarding.OnboardingScreen
import dev.alnoer.audiosaas.feature.preferences.PreferencesViewModel
import dev.alnoer.audiosaas.feature.recordings.HistoryScreen
import dev.alnoer.audiosaas.feature.recordings.ResultScreen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private object AppRoute {
    const val ONBOARDING = "onboarding"
    const val OVERVIEW = "overview"
    const val HISTORY = "history"
    const val FOLDERS = "folders"
    const val RESULT = "result"
}

internal data class AppStartupState(
    val startDestination: String,
    val showIntroduction: Boolean,
)

internal fun resolveAppStartupState(
    isFirstTimeLaunch: Boolean,
    isAuthenticated: Boolean,
): AppStartupState = AppStartupState(
    startDestination = if (isAuthenticated) AppRoute.OVERVIEW else AppRoute.ONBOARDING,
    showIntroduction = isFirstTimeLaunch && !isAuthenticated,
)

@Composable
fun AudioSaaSApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    fun selectDestination(destination: AudioDestination) {
        navController.navigate(
            when (destination) {
                AudioDestination.Overview -> AppRoute.OVERVIEW
                AudioDestination.Recordings -> AppRoute.HISTORY
                AudioDestination.Folders -> AppRoute.FOLDERS
            }
        )
    }
    val preferencesViewModel: PreferencesViewModel = hiltViewModel()
    val authSessionViewModel: AuthSessionViewModel = hiltViewModel()
    val isFirstTimeLaunch = preferencesViewModel.isFirstTimeLaunch
        .collectAsStateWithLifecycle(initialValue = null)
        .value
    val isAuthenticated = authSessionViewModel.isAuthenticated
        .collectAsStateWithLifecycle(initialValue = null)
        .value
    if (isFirstTimeLaunch == null || isAuthenticated == null) return

    val startupState = remember {
        resolveAppStartupState(
            isFirstTimeLaunch = isFirstTimeLaunch,
            isAuthenticated = isAuthenticated,
        )
    }

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            val currentRoute = navController.currentBackStackEntryFlow
                .first()
                .destination
                .route
            if (currentRoute != AppRoute.ONBOARDING) {
                navController.navigate(AppRoute.ONBOARDING) {
                    popUpTo(AppRoute.OVERVIEW) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startupState.startDestination,
        modifier = modifier,
    ) {
        composable(AppRoute.ONBOARDING) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            val authLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                val intent = result.data ?: return@rememberLauncherForActivityResult
                authViewModel.processAuthResult(intent)
            }
            val authState = authViewModel.authState.collectAsStateWithLifecycle().value
            val authAvailable = authState is AuthState.NotStarted || authState is AuthState.Failure

            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Failure -> snackbarHostState.showAudioSnackbar(
                        message = authState.message,
                        type = AudioSnackbarType.Error,
                        withDismissAction = true,
                        duration = SnackbarDuration.Long,
                    )
                    AuthState.Success -> navController.navigate(AppRoute.OVERVIEW) {
                        popUpTo(AppRoute.ONBOARDING) {
                            inclusive = true
                        }
                    }

                    else -> {}
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                OnboardingScreen(
                    onFinished = {
                        if (authAvailable) {
                            coroutineScope.launch {
                                try {
                                    val loginIntent = authViewModel.getLoginIntent()
                                    if (loginIntent == null) {
                                        snackbarHostState.showAudioSnackbar(
                                            message = "Не удалось начать вход. Попробуйте ещё раз.",
                                            type = AudioSnackbarType.Error,
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Long,
                                        )
                                        return@launch
                                    }

                                    preferencesViewModel.setNotFirstTimeLaunch()
                                    authLauncher.launch(loginIntent)
                                } catch (exception: CancellationException) {
                                    throw exception
                                } catch (_: Exception) {
                                    snackbarHostState.showAudioSnackbar(
                                        message = "Не удалось сохранить настройки. Попробуйте ещё раз.",
                                        type = AudioSnackbarType.Error,
                                        withDismissAction = true,
                                        duration = SnackbarDuration.Long,
                                    )
                                }
                            }
                        }
                    },
                    nextButtonEnabled = authAvailable,
                    showIntroduction = isFirstTimeLaunch && startupState.showIntroduction,
                    modifier = Modifier,
                )
                AudioSnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                )
            }
        }
        composable(AppRoute.OVERVIEW) {
            OverviewScreen(
                onDestinationSelected = ::selectDestination,
                onRecordingSelected = { navController.navigate(AppRoute.RESULT) },
                onNewRecording = { navController.navigate(AppRoute.RESULT) },
                onViewAll = { navController.navigate(AppRoute.HISTORY) },
                modifier = Modifier,
            )
        }
        composable(AppRoute.HISTORY) {
            HistoryScreen(
                onDestinationSelected = ::selectDestination,
                onRecordingSelected = { navController.navigate(AppRoute.RESULT) },
                onNewRecording = { navController.navigate(AppRoute.RESULT) },
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
