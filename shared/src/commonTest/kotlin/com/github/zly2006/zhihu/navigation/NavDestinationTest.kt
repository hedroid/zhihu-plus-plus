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

package com.github.zly2006.zhihu.navigation

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NavDestinationTest {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun serializesSearchDestinationFromCommonCode() {
        val destination: NavDestination = Search(query = "kmp")

        val decoded = json.decodeFromString<NavDestination>(
            json.encodeToString<NavDestination>(destination),
        )

        assertEquals(destination, decoded)
    }

    @Test
    fun resolvesZhihuAnswerUrlFromCommonCode() {
        val destination = resolveContent("https://www.zhihu.com/question/1/answer/42")

        val article = assertIs<Article>(destination)
        assertEquals(ArticleType.Answer, article.type)
        assertEquals(42L, article.id)
    }

    @Test
    fun resolvesZhihuHybridUrlFromEmbeddedZhUrl() {
        val destination = resolveContent(
            "zhihu://hybrid?open=1&zh_url=https%3A%2F%2Fzhuanlan.zhihu.com%2Fp%2F703712120" +
                "&fallback_url=https%3A%2F%2Fwww.zhihu.com%2Foia%2Fhybrid%3Fopen%3D1",
        )

        val article = assertIs<Article>(destination)
        assertEquals(ArticleType.Article, article.type)
        assertEquals(703712120L, article.id)
    }

    @Test
    fun resolvesZhihuHybridUrlFromNestedFallbackUrl() {
        val destination = resolveContent(
            "zhihu://hybrid?open=1" +
                "&fallback_url=https%3A%2F%2Fwww.zhihu.com%2Foia%2Fhybrid%3Fopen%3D1%26" +
                "zh_url%3Dhttps%253A%252F%252Fzhuanlan.zhihu.com%252Fp%252F703712120",
        )

        val article = assertIs<Article>(destination)
        assertEquals(ArticleType.Article, article.type)
        assertEquals(703712120L, article.id)
    }
}
