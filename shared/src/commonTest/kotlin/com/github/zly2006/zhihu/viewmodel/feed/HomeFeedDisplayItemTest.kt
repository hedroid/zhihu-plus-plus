/*
 * Zhihu++ - Free & Ad-Free Zhihu client for all platforms.
 * Copyright (C) 2024-2026, zly2006 <i@zly2006.me>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation (version 3 only).
 */

package com.github.zly2006.zhihu.viewmodel.feed

import com.github.zly2006.zhihu.data.CommonFeed
import com.github.zly2006.zhihu.data.Feed
import com.github.zly2006.zhihu.data.Person
import com.github.zly2006.zhihu.viewmodel.FeedDisplayEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeFeedDisplayItemTest {
    @Test
    fun onlyCompactsCountsFromTenThousand() {
        assertEquals(
            "9999 赞同 · 1 万 评论",
            compactHomeFeedCountText("9999 赞同 · 10000 评论"),
        )
    }

    @Test
    fun compactsWebRecommendationCounts() {
        val feed = CommonFeed(
            id = "answer-1",
            target = Feed.AnswerTarget(
                id = 1,
                url = "https://www.zhihu.com/question/2/answer/1",
                author = Person(
                    id = "author-id",
                    url = "https://www.zhihu.com/people/author-id",
                    userType = "people",
                    name = "作者",
                    headline = "",
                    avatarUrl = "",
                ),
                voteupCount = 12_600,
                commentCount = 729,
                question = Feed.QuestionTarget(
                    id = 2,
                    _title = "问题",
                    url = "https://www.zhihu.com/question/2",
                    type = "question",
                ),
            ),
        )

        val item = HomeFeedViewModel().createDisplayItem(object : FeedDisplayEnvironment {}, feed)

        assertEquals("回答 · 1.3 万 赞同 · 729 评论", item.details)
    }
}
