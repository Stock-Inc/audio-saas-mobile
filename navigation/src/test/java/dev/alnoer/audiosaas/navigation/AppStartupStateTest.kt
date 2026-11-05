package dev.alnoer.audiosaas.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppStartupStateTest {
    @Test
    fun firstLaunchWithoutSessionShowsIntroduction() {
        val state = resolveAppStartupState(
            isFirstTimeLaunch = true,
            isAuthenticated = false,
        )

        assertEquals("onboarding", state.startDestination)
        assertTrue(state.showIntroduction)
    }

    @Test
    fun returningUserWithoutSessionStartsAtLogin() {
        val state = resolveAppStartupState(
            isFirstTimeLaunch = false,
            isAuthenticated = false,
        )

        assertEquals("onboarding", state.startDestination)
        assertFalse(state.showIntroduction)
    }

    @Test
    fun authenticatedUserStartsAtOverview() {
        val state = resolveAppStartupState(
            isFirstTimeLaunch = false,
            isAuthenticated = true,
        )

        assertEquals("overview", state.startDestination)
        assertFalse(state.showIntroduction)
    }

    @Test
    fun authenticatedSessionWinsOverMissingPreference() {
        val state = resolveAppStartupState(
            isFirstTimeLaunch = true,
            isAuthenticated = true,
        )

        assertEquals("overview", state.startDestination)
        assertFalse(state.showIntroduction)
    }
}
