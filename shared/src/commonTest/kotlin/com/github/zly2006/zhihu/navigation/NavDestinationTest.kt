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
    fun resolvesTopicUrlsFromCommonCode() {
        listOf(
            "https://www.zhihu.com/topic/19550517" to Topic("19550517"),
            "zhihu://topic/19550517" to Topic("19550517"),
            "zhihu://pin20/topic?topic_id=19550517" to Topic("19550517"),
            "https://www.zhihu.com/topic/19550517/hot" to Topic("19550517", section = "hot"),
            "https://www.zhihu.com/topic/19550517/newest" to Topic("19550517", section = "newest"),
            "https://www.zhihu.com/topic/19550517/top-answers" to Topic("19550517", section = "top-answers"),
            "https://www.zhihu.com/topic/19550517/unanswered" to Topic("19550517", section = "unanswered"),
        ).forEach { (url, expected) ->
            assertEquals(expected, resolveContent(url), url)
        }
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

    @Test
    fun resolvesOiaShareUrlPaths() {
        // 从知乎分享/运营域名 oia.zhihu.com 打开的直链：路径结构与主站一致，
        // 且个人主页分享链接自带 utm_* 与 fallback_url 跟踪参数。
        val sharedPeopleUrl = "https://oia.zhihu.com/people/c9bbc36330f1065183014cbfdf956cbe" +
            "?utm_source=wechat_session&utm_medium=profile&utm_content=description" +
            "&mcid=df8e35d1-67d2-4587-bb12-b6b1cef22976" +
            "&fallback_url=https%3A%2F%2Foia.zhihu.com%2Fpeople%2Fc9bbc36330f1065183014cbfdf956cbe"
        val person = assertIs<Person>(resolveContent(sharedPeopleUrl))
        assertEquals("c9bbc36330f1065183014cbfdf956cbe", person.id)

        val article = assertIs<Article>(resolveContent("https://oia.zhihu.com/question/628888888/answer/123456789"))
        assertEquals(ArticleType.Answer, article.type)
        assertEquals(123456789L, article.id)
    }

    @Test
    fun resolvesPeopleProfileTabUrls() {
        // 个人主页子 tab 分享链接：进入个人主页并直接落在对应 tab。
        val expectedBase = Person(id = Person.EMPTY_ID, urlToken = "zly2006")
        listOf(
            "https://www.zhihu.com/people/zly2006/answers" to expectedBase.copy(jumpTo = "回答"),
            "https://www.zhihu.com/people/zly2006/posts" to expectedBase.copy(jumpTo = "文章"),
            "https://www.zhihu.com/people/zly2006/articles" to expectedBase.copy(jumpTo = "文章"),
            "https://www.zhihu.com/people/zly2006/activities" to expectedBase.copy(jumpTo = "动态"),
            "https://www.zhihu.com/people/zly2006/asks" to expectedBase.copy(jumpTo = "提问"),
            "https://www.zhihu.com/people/zly2006/pins" to expectedBase.copy(jumpTo = "想法"),
            "https://www.zhihu.com/people/zly2006/columns" to expectedBase.copy(jumpTo = "专栏"),
            "https://www.zhihu.com/people/zly2006/followers" to expectedBase.copy(jumpTo = "粉丝"),
            "https://www.zhihu.com/people/zly2006/followees" to expectedBase.copy(jumpTo = "关注"),
            // 未知子路径降级为个人主页本身，而不是解析失败。
            "https://www.zhihu.com/people/zly2006/unknown-tab" to expectedBase,
            "https://www.zhihu.com/people/zly2006" to expectedBase,
        ).forEach { (url, expected) ->
            assertEquals(expected, resolveContent(url), url)
        }
    }

    @Test
    fun resolvesQuestionAnswerListAndVideoUrls() {
        assertEquals(Question(628888888), resolveContent("https://www.zhihu.com/question/628888888/answers"))
        assertEquals(Video(1234567890), resolveContent("https://www.zhihu.com/zvideo/1234567890"))
        assertEquals(Video(1234567890), resolveContent("https://v.zhihu.com/video/1234567890"))
    }

    @Test
    fun resolvesAnswerCommentDeepLinkWithAnchor() {
        val destination = resolveContent(
            "zhihu://comment/list/answer/42?anchor_comment_id=123456&is_child=false",
        )

        val holder = assertIs<CommentHolder>(destination)
        val article = assertIs<Article>(holder.article)
        assertEquals(ArticleType.Answer, article.type)
        assertEquals(42L, article.id)
        assertEquals("123456", holder.commentId)
    }

    @Test
    fun resolvesArticleCommentDeepLinkWithAnchor() {
        val destination = resolveContent(
            "zhihu://comment/list/article/43?anchor_comment_id=123457&is_child=false",
        )

        val holder = assertIs<CommentHolder>(destination)
        val article = assertIs<Article>(holder.article)
        assertEquals(ArticleType.Article, article.type)
        assertEquals(43L, article.id)
        assertEquals("123457", holder.commentId)
    }

    @Test
    fun resolvesPinCommentDeepLinkWithAnchor() {
        val destination = resolveContent(
            "zhihu://comment/list/pin/44?anchor_comment_id=123458&is_child=true",
        )

        val holder = assertIs<CommentHolder>(destination)
        val pin = assertIs<Pin>(holder.article)
        assertEquals(44L, pin.id)
        assertEquals("123458", holder.commentId)
    }

    @Test
    fun resolvesQuestionCommentDeepLinkWithAnchor() {
        val destination = resolveContent(
            "zhihu://comment/list/question/45?anchor_comment_id=123459&is_child=false",
        )

        val holder = assertIs<CommentHolder>(destination)
        val question = assertIs<Question>(holder.article)
        assertEquals(45L, question.questionId)
        assertEquals("123459", holder.commentId)
    }

    @Test
    fun resolvesCommentDeepLinkWithExtraAndroidParameters() {
        val destination = resolveContent(
            "zhihu://comment/list/answer/46?anchor_comment_id=123460&list_height_ratio=0.66&dragIconVisible=true&segment=%7B%22id%22%3A1%7D",
        )

        val holder = assertIs<CommentHolder>(destination)
        val article = assertIs<Article>(holder.article)
        assertEquals(ArticleType.Answer, article.type)
        assertEquals(46L, article.id)
        assertEquals("123460", holder.commentId)
    }

    @Test
    fun resolvesZhihuAppViewPinUrlFromCommonCode() {
        val destination = resolveContent("https://www.zhihu.com/appview/pin/2059710318939301395")

        val pin = assertIs<Pin>(destination)
        assertEquals(2059710318939301395L, pin.id)
    }

    @Test
    fun resolvesZhihuAppViewAnswerUrlFromCommonCode() {
        val destination = resolveContent("https://www.zhihu.com/appview/answer/2040633177593619876")

        val article = assertIs<Article>(destination)
        assertEquals(ArticleType.Answer, article.type)
        assertEquals(2040633177593619876L, article.id)
    }

    @Test
    fun resolvesZhihuAppViewArticleUrlFromCommonCode() {
        val destination = resolveContent("https://www.zhihu.com/appview/p/1981671287999981270")

        val article = assertIs<Article>(destination)
        assertEquals(ArticleType.Article, article.type)
        assertEquals(1981671287999981270L, article.id)
    }

    @Test
    fun resolvesNotificationTimelineEntryFromOfficialMessageLink() {
        val destination = resolveContent(
            "https://www.zhihu.com/notifications/v3/timeline/entry/system?title=%E7%B3%BB%E7%BB%9F%E6%B6%88%E6%81%AF",
        )

        val entry = assertIs<Notification.Entry>(destination)
        assertEquals("system", entry.entryName)
        assertEquals("系统消息", entry.title)
    }

    @Test
    fun resolvesInvitationAnswerPageFromOfficialMessageLink() {
        val destination = resolveContent(
            "https://www.zhihu.com/compose_answer_tab?default_selected_page=2&title=%E9%82%80%E8%AF%B7%E5%9B%9E%E7%AD%94",
        )

        assertEquals(Notification.Invitations, destination)
    }

    @Test
    fun resolvesPrivateMessageFromOfficialInboxLink() {
        val destination = resolveContent(
            "https://www.zhihu.com/inbox/peer-token?title=%E7%9F%A5%E4%B9%8E%E5%B0%8F%E7%AE%A1%E5%AE%B6&source_type=message_list",
        )

        val message = assertIs<Notification.Message>(destination)
        assertEquals("peer-token", message.peerId)
        assertEquals("知乎小管家", message.name)
    }
}
