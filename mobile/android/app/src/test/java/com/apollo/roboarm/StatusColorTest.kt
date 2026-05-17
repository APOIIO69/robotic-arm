package com.apollo.roboarm

import androidx.compose.ui.graphics.Color
import com.apollo.roboarm.data.models.RobotStatus
import com.apollo.roboarm.ui.components.getStatusColor
import com.apollo.roboarm.ui.components.getStatusDimColor
import com.apollo.roboarm.ui.theme.DarkColors
import org.junit.Assert.assertEquals
import org.junit.Test

class StatusColorTest {
    @Test
    fun `test getStatusColor returns correct colors for DarkColors`() {
        val colors = DarkColors
        assertEquals(colors.ok, getStatusColor(RobotStatus.OK, colors))
        assertEquals(colors.warning, getStatusColor(RobotStatus.WARNING, colors))
        assertEquals(colors.critical, getStatusColor(RobotStatus.CRITICAL, colors))
        assertEquals(colors.offline, getStatusColor(RobotStatus.OFFLINE, colors))
    }

    @Test
    fun `test getStatusDimColor returns correct colors for DarkColors`() {
        val colors = DarkColors
        assertEquals(colors.okDim, getStatusDimColor(RobotStatus.OK, colors))
        assertEquals(colors.warningDim, getStatusDimColor(RobotStatus.WARNING, colors))
        assertEquals(colors.criticalDim, getStatusDimColor(RobotStatus.CRITICAL, colors))
        assertEquals(Color.Transparent, getStatusDimColor(RobotStatus.OFFLINE, colors))
    }
}
