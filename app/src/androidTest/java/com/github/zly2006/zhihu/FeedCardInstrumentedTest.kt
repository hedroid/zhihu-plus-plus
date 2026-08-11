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

package com.github.zly2006.zhihu

import android.content.Context
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.zly2006.zhihu.data.FeedDisplayItem
import com.github.zly2006.zhihu.test.MainActivityComposeRule
import com.github.zly2006.zhihu.test.resetAppPreferences
import com.github.zly2006.zhihu.test.setScreenContent
import com.github.zly2006.zhihu.ui.PREFERENCE_NAME
import com.github.zly2006.zhihu.ui.components.FEED_CARD_MORE_BUTTON_TAG
import com.github.zly2006.zhihu.ui.components.FeedCard
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedCardInstrumentedTest {
    @get:Rule
    val composeRule: MainActivityComposeRule = createAndroidComposeRule()

    @Before
    fun setUp() {
        composeRule.resetAppPreferences()
        composeRule.activity.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit(commit = true) {
            putString("feedCardStyle", "divider")
            putBoolean("duo3_card_layout", false)
            putBoolean("showFeedThumbnail", true)
        }
    }

    @Test
    fun classicCardWithThumbnailKeepsMoreButtonAtTrailingContentEdge() {
        composeRule.setScreenContent {
            FeedCard(
                item = FeedDisplayItem(
                    title = "带缩略图的信息流卡片",
                    summary = "缩略图不应把底部三点按钮挤向卡片中间。",
                    details = "固定详情",
                    feed = null,
                ),
                thumbnailUrl = "https://example.invalid/thumbnail.png",
                modifier = Modifier.testTag(FEED_CARD_TAG),
            )
        }

        val cardBounds = composeRule.onNodeWithTag(FEED_CARD_TAG).fetchSemanticsNode().boundsInRoot
        val moreButtonBounds = composeRule
            .onNodeWithTag(FEED_CARD_MORE_BUTTON_TAG)
            .fetchSemanticsNode()
            .boundsInRoot
        val thumbnailBounds = composeRule
            .onNodeWithContentDescription("Thumbnail", useUnmergedTree = true)
            .fetchSemanticsNode()
            .boundsInRoot
        val expectedTrailingInset = with(composeRule.density) { 16.dp.toPx() }
        val expectedThumbnailSize = with(composeRule.density) { 60.dp.toPx() }

        assertEquals(
            "三点按钮应固定在卡片内容区右边缘，不能被缩略图挤向左侧",
            expectedTrailingInset.toDouble(),
            (cardBounds.right - moreButtonBounds.right).toDouble(),
            1.0,
        )
        assertEquals(
            "经典卡片缩略图宽度不能随原图比例或 Row 权重膨胀",
            expectedThumbnailSize.toDouble(),
            thumbnailBounds.width.toDouble(),
            1.0,
        )
        assertEquals(
            "经典卡片缩略图高度不能挤掉底部统计和菜单",
            expectedThumbnailSize.toDouble(),
            thumbnailBounds.height.toDouble(),
            1.0,
        )

        composeRule.onNodeWithTag(FEED_CARD_MORE_BUTTON_TAG).performClick()
        composeRule.onNodeWithText("外观设置").assertIsDisplayed()
    }

    @Test
    fun retainedCardAndDuo3AppearanceKeepsMoreButtonAtTrailingContentEdge() {
        composeRule.activity.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit(commit = true) {
            putString("feedCardStyle", "card")
            putBoolean("duo3_card_appearance", true)
            putBoolean("duo3_card_layout", true)
        }
        composeRule.setScreenContent {
            FeedCard(
                item = FeedDisplayItem(
                    title = "保留外观设置的普通卡片",
                    summary = "覆盖安装会保留手机上的卡片与 Duo3 设置。",
                    details = "固定详情",
                    feed = null,
                    avatarSrc = "https://example.invalid/avatar.png",
                    authorName = "固定作者",
                ),
                thumbnailUrl = "https://example.invalid/thumbnail.png",
                modifier = Modifier.testTag(FEED_CARD_TAG),
            )
        }

        val cardBounds = composeRule.onNodeWithTag(FEED_CARD_TAG).fetchSemanticsNode().boundsInRoot
        val moreButtonBounds = composeRule
            .onNodeWithTag(FEED_CARD_MORE_BUTTON_TAG)
            .fetchSemanticsNode()
            .boundsInRoot
        val expectedTrailingInset = with(composeRule.density) { 32.dp.toPx() }

        assertEquals(
            "卡片与 Duo3 外观组合下，三点按钮也应固定在卡片右侧",
            expectedTrailingInset.toDouble(),
            (cardBounds.right - moreButtonBounds.right).toDouble(),
            1.0,
        )
    }

    private companion object {
        const val FEED_CARD_TAG = "feed_card_alignment_test"
    }
}
