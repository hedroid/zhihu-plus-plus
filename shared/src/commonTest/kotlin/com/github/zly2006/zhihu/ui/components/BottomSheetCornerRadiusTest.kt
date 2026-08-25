/*
 * Zhihu++ - Free & Ad-Free Zhihu client for all platforms.
 * Copyright (C) 2024-2026, zly2006 <i@zly2006.me>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation (version 3 only).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.zly2006.zhihu.ui.components

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class BottomSheetCornerRadiusTest {
    @Test
    fun compactWindowUsesMinimumRadius() {
        assertEquals(16.dp, resolveBottomSheetCornerRadius(320.dp, 720.dp))
    }

    @Test
    fun phoneRadiusFollowsTheWindowShortSide() {
        assertEquals(18.dp, resolveBottomSheetCornerRadius(360.dp, 800.dp))
        assertEquals(18.dp, resolveBottomSheetCornerRadius(800.dp, 360.dp))
    }

    @Test
    fun largeWindowCapsTheRadius() {
        assertEquals(24.dp, resolveBottomSheetCornerRadius(600.dp, 960.dp))
    }

    @Test
    fun radiusShrinksWhenSheetApproachesTheWindowTop() {
        assertEquals(8.dp, resolveBottomSheetCornerRadius(360.dp, 800.dp, 8.dp))
    }

    @Test
    fun fullHeightSheetLetsThePhysicalDisplayClipItsCorners() {
        assertEquals(0.dp, resolveBottomSheetCornerRadius(360.dp, 800.dp, 0.dp))
    }
}
