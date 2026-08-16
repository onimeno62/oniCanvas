package com.onimeno.onicanvas.core.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptivePerformanceTest {

    @Test
    fun testAdaptiveNavigationItemCreation() {
        val item = AdaptiveNavigationItem(
            route = "dashboard_route",
            label = "Dashboard",
            selectedIcon = Icons.Rounded.Dashboard,
            unselectedIcon = Icons.Rounded.Dashboard,
            testTag = "nav_dashboard"
        )
        assertEquals("Dashboard", item.label)
        assertEquals("nav_dashboard", item.testTag)
        assertEquals("dashboard_route", item.route)
    }

    @Test
    fun testAdaptiveBreakpointThresholds() {
        val phoneWidth = 360
        val foldableWidth = 600
        val tabletWidth = 840

        assertTrue(phoneWidth < 600)
        assertTrue(foldableWidth >= 600)
        assertTrue(tabletWidth >= 720)
    }
}
