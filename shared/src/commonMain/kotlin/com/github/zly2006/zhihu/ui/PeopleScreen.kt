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

package com.github.zly2006.zhihu.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SpeakerNotesOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil3.compose.AsyncImage
import com.fleeksoft.ksoup.Ksoup
import com.github.zly2006.zhihu.data.DataHolder
import com.github.zly2006.zhihu.data.FeedDisplayItem
import com.github.zly2006.zhihu.data.OfficialBadge
import com.github.zly2006.zhihu.data.ZhihuJson
import com.github.zly2006.zhihu.data.officialBadge
import com.github.zly2006.zhihu.data.officialBadgeDetails
import com.github.zly2006.zhihu.data.toFeedDisplayItemNavDestinationJson
import com.github.zly2006.zhihu.navigation.Article
import com.github.zly2006.zhihu.navigation.ArticleType
import com.github.zly2006.zhihu.navigation.CollectionContent
import com.github.zly2006.zhihu.navigation.LocalNavigator
import com.github.zly2006.zhihu.navigation.Person
import com.github.zly2006.zhihu.navigation.Pin
import com.github.zly2006.zhihu.navigation.Question
import com.github.zly2006.zhihu.platform.rememberExternalUrlOpener
import com.github.zly2006.zhihu.platform.rememberImagePreviewOpener
import com.github.zly2006.zhihu.platform.rememberSettingsStore
import com.github.zly2006.zhihu.platform.rememberUserMessageSink
import com.github.zly2006.zhihu.platform.rememberZhihuWebUrlOpener
import com.github.zly2006.zhihu.reading.RegisterReadingQueueSource
import com.github.zly2006.zhihu.ui.components.AuthorBadge
import com.github.zly2006.zhihu.ui.components.FeedCard
import com.github.zly2006.zhihu.ui.components.PaginatedList
import com.github.zly2006.zhihu.ui.components.ProgressIndicatorFooter
import com.github.zly2006.zhihu.ui.subscreens.DEFAULT_FAB_OPACITY
import com.github.zly2006.zhihu.ui.subscreens.PREF_FAB_OPACITY
import com.github.zly2006.zhihu.util.Log
import com.github.zly2006.zhihu.util.raiseForStatus
import com.github.zly2006.zhihu.viewmodel.ContentBlocklistEnvironment
import com.github.zly2006.zhihu.viewmodel.PaginationEnvironment
import com.github.zly2006.zhihu.viewmodel.PaginationViewModel
import com.github.zly2006.zhihu.viewmodel.ProfileLoadEnvironment
import com.github.zly2006.zhihu.viewmodel.ZhihuApiEnvironment
import com.github.zly2006.zhihu.viewmodel.addReadHistory
import com.github.zly2006.zhihu.viewmodel.deleteSigned
import com.github.zly2006.zhihu.viewmodel.feed.BaseFeedViewModel
import com.github.zly2006.zhihu.viewmodel.postSigned
import com.github.zly2006.zhihu.viewmodel.rememberPaginationEnvironment
import io.ktor.client.call.body
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.resources.painterResource
import zhihu.shared.generated.resources.Res
import zhihu.shared.generated.resources.ic_zh_plus_author_badge
import kotlin.reflect.typeOf
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import com.github.zly2006.zhihu.navigation.Search as SearchDestination

class PeopleAnswersViewModel(
    val person: Person,
) : PaginationViewModel<DataHolder.Answer>(
        typeOf<DataHolder.Answer>(),
    ) {
    var sortBy by mutableStateOf("voteups")
        private set

    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/answers?sort_by=$sortBy"

    override val include: String
        get() = "data[*].is_normal,admin_closed_comment,reward_info,is_collapsed,annotation_action,annotation_detail,collapse_reason,collapsed_by,suggest_edit,comment_count,thanks_count,can_comment,content,editable_content,attachment,voteup_count,reshipment_settings,comment_permission,created_time,updated_time,review_info,excerpt,paid_info,reaction_instruction,is_labeled,label_info,relationship.is_authorized,voting,is_author,is_thanked,is_nothelp,author.badge_v2"

    fun updateSortBy(newSort: String): Boolean {
        if (sortBy == newSort) {
            return false
        }
        sortBy = newSort
        return true
    }

    fun changeSortBy(newSort: String, environment: PaginationEnvironment) {
        if (updateSortBy(newSort)) {
            refresh(environment)
        }
    }
}

class PeopleArticlesViewModel(
    val person: Person,
) : PaginationViewModel<DataHolder.Article>(
        typeOf<DataHolder.Article>(),
    ) {
    var sortBy by mutableStateOf("created")
        private set

    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/articles?sort_by=$sortBy"

    override val include: String
        get() = "data[*].comment_count,suggest_edit,is_normal,thumbnail_extra_info,thumbnail,can_comment,comment_permission,admin_closed_comment,content,voteup_count,created,updated,upvoted_followees,voting,review_info,reaction_instruction,is_labeled,label_info,author.badge_v2;data[*].vessay_info;data[*].author.badge[?(type=best_answerer)].topics;"

    fun updateSortBy(newSort: String): Boolean {
        if (sortBy == newSort) {
            return false
        }
        sortBy = newSort
        return true
    }

    fun changeSortBy(newSort: String, environment: PaginationEnvironment) {
        if (updateSortBy(newSort)) {
            refresh(environment)
        }
    }
}

class PeopleActivitiesViewModel(
    val person: Person,
    val sort: String = "created",
) : BaseFeedViewModel() {
    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v3/moments/${person.userTokenOrId}/activities"
}

class PeopleFollowersViewModel(
    val person: Person,
) : PaginationViewModel<DataHolder.People>(
        typeOf<DataHolder.People>(),
    ) {
    override val initialUrl: String
        // 签名有bug，暂时无法使用新的API，先回退到旧的API
        // get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/followers"
        get() = "https://api.zhihu.com/people/${person.id}/followers"

    override val include: String
        get() = "data[*].answer_count,articles_count,gender,follower_count,is_followed,is_following,badge_v2,badge[?(type=best_answerer)].topics"
}

class PeopleFollowingViewModel(
    val person: Person,
) : PaginationViewModel<DataHolder.People>(
        typeOf<DataHolder.People>(),
    ) {
    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/followees"

    override val include: String
        get() = "data[*].answer_count,articles_count,gender,follower_count,is_followed,is_following,badge_v2,badge[?(type=best_answerer)].topics"
}

class PeopleCollectionsViewModel(
    val person: Person,
) : PaginationViewModel<DataHolder.Collection>(
        typeOf<DataHolder.Collection>(),
    ) {
    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/favlists"

    override val include: String
        get() = "data[*].updated_time,answer_count,follower_count,creator"
}

class PeopleQuestionsViewModel(
    val person: Person,
) : PaginationViewModel<DataHolder.Question>(
        typeOf<DataHolder.Question>(),
    ) {
    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/questions"

    override val include: String
        get() = "data[*].created,answer_count,follower_count,author,visit_count,comment_count,detail,relationship,topics,voteup_count"
}

class PeoplePinsViewModel(
    val person: Person,
) : PaginationViewModel<DataHolder.Pin>(
        typeOf<DataHolder.Pin>(),
    ) {
    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/v2/pins/${person.userTokenOrId}/moments"

    override val include: String
        get() = "data[*].like_count,comment_count,created,updated,content"
}

class PeopleColumnContributionsViewModel(
    val person: Person,
) : PaginationViewModel<DataHolder.Column>(
        typeOf<DataHolder.Column>(),
    ) {
    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/column-contributions"

    override val include: String
        get() = "data[*].articles_count,followers,author"
}

class PeopleFollowingCollectionsViewModel(
    val person: Person,
) : PaginationViewModel<DataHolder.Collection>(
        typeOf<DataHolder.Collection>(),
    ) {
    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/following-favlists"

    override val include: String
        get() = "data[*].updated_time,answer_count,follower_count,creator"
}

@Serializable
data class FollowedQuestion(
    val id: String,
    val type: String = "question",
    val url: String = "",
    val title: String = "",
    val questionType: String = "",
    val created: Long = 0L,
    val updatedTime: Long = 0L,
)

@Serializable
data class FollowedTopic(
    val id: String = "",
    val type: String = "topic",
    val url: String = "",
    val name: String = "",
    val avatarUrl: String? = null,
    val topicType: String? = null,
    val topic: DataHolder.Topic? = null,
) {
    val displayId: String get() = topic?.id ?: id
    val displayName: String get() = topic?.name ?: name
    val displayAvatarUrl: String? get() = topic?.avatarUrl ?: avatarUrl
}

class PeopleFollowingQuestionsViewModel(
    val person: Person,
) : PaginationViewModel<FollowedQuestion>(
        typeOf<FollowedQuestion>(),
    ) {
    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/following-questions"

    override val include: String
        get() = ""
}

class PeopleFollowingTopicsViewModel(
    val person: Person,
) : PaginationViewModel<FollowedTopic>(
        typeOf<FollowedTopic>(),
    ) {
    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/following-topic-contributions"

    override val include: String
        get() = ""
}

class PeopleFollowingColumnsViewModel(
    val person: Person,
) : PaginationViewModel<DataHolder.Column>(
        typeOf<DataHolder.Column>(),
    ) {
    override val initialUrl: String
        get() = "https://www.zhihu.com/api/v4/members/${person.userTokenOrId}/following-columns"

    override val include: String
        get() = "data[*].articles_count,followers,author"
}

class PersonViewModel(
    val person: Person,
) : ViewModel() {
    var avatar by mutableStateOf("")
    var name by mutableStateOf(person.name)
    var headline by mutableStateOf("")
    var officialBadge by mutableStateOf<OfficialBadge?>(null)
    var officialBadgeDetails by mutableStateOf<List<OfficialBadge>>(emptyList())
    var githubSocial by mutableStateOf<GithubSocialUiState?>(null)
    var followerCount by mutableIntStateOf(0)
    var followingCount by mutableIntStateOf(0)
    var answerCount by mutableIntStateOf(0)
    var articleCount by mutableIntStateOf(0)
    var isFollowing by mutableStateOf(false)
    var isBlocking by mutableStateOf(false)
    var isBlockedInRecommendations by mutableStateOf(false)
    var isBlockedAsQuestionAuthor by mutableStateOf(false)
    var memberHashId by mutableStateOf(person.id)

    // 只实现已有数据类型的 ViewModel
    val answersFeedModel = PeopleAnswersViewModel(person)
    val articlesFeedModel = PeopleArticlesViewModel(person)
    val activitiesFeedModel = PeopleActivitiesViewModel(person)
    val collectionsFeedModel = PeopleCollectionsViewModel(person)
    val questionsFeedModel = PeopleQuestionsViewModel(person)
    val pinsFeedModel = PeoplePinsViewModel(person)
    val columnsFeedModel = PeopleColumnContributionsViewModel(person)
    val followersFeedModel = PeopleFollowersViewModel(person)
    val followingFeedModel = PeopleFollowingViewModel(person)
    val followingCollectionsFeedModel = PeopleFollowingCollectionsViewModel(person)
    val followingQuestionsFeedModel = PeopleFollowingQuestionsViewModel(person)
    val followingTopicsFeedModel = PeopleFollowingTopicsViewModel(person)
    val followingColumnsFeedModel = PeopleFollowingColumnsViewModel(person)
    val subFeedModels = arrayOf(
        answersFeedModel,
        articlesFeedModel,
        activitiesFeedModel,
        collectionsFeedModel,
        questionsFeedModel,
        pinsFeedModel,
        columnsFeedModel,
        followersFeedModel,
        followingFeedModel,
    )

    suspend fun toggleFollow(environment: ZhihuApiEnvironment) {
        val followersUrl = "https://www.zhihu.com/api/v4/members/${person.urlToken}/followers"
        val newFollowingState = !isFollowing
        val response = if (newFollowingState) {
            environment.postSigned(followersUrl)
        } else {
            environment.deleteSigned(followersUrl)
        }
        val jojo = response.raiseForStatus().body<JsonObject>()
        followerCount = jojo["follower_count"]?.jsonPrimitive?.int ?: (followerCount + if (newFollowingState) 1 else -1)
        isFollowing = newFollowingState
    }

    suspend fun toggleBlock(environment: ZhihuApiEnvironment) {
        val blockUrl = "https://www.zhihu.com/api/v4/members/${person.urlToken}/actions/block"
        val newBlockingState = !isBlocking
        if (newBlockingState) {
            environment.postSigned(blockUrl)
        } else {
            environment.deleteSigned(blockUrl)
        }.raiseForStatus()
        isBlocking = newBlockingState
    }

    suspend fun toggleRecommendationBlock(environment: ContentBlocklistEnvironment) {
        if (isBlockedInRecommendations) {
            environment.removeBlockedUser(person.id)
            isBlockedInRecommendations = false
        } else {
            environment.addBlockedUser(
                userId = person.id,
                userName = name,
                urlToken = person.urlToken,
                avatarUrl = avatar,
            )
            isBlockedInRecommendations = true
        }
    }

    suspend fun toggleQuestionAuthorBlock(environment: ContentBlocklistEnvironment) {
        if (isBlockedAsQuestionAuthor) {
            environment.removeBlockedQuestionAuthor(person.id)
            isBlockedAsQuestionAuthor = false
        } else {
            environment.addBlockedQuestionAuthor(
                userId = person.id,
                userName = name,
                urlToken = person.urlToken,
                avatarUrl = avatar,
            )
            isBlockedAsQuestionAuthor = true
        }
    }

    suspend fun load(environment: ProfileLoadEnvironment) {
        environment.addReadHistory(person.id, "profile")

        val profileUrl = "https://api.zhihu.com/people/${person.urlToken.takeIf(String::isNotBlank) ?: person.id}"
        val jojo = environment.fetchJson(profileUrl, PEOPLE_PROFILE_INCLUDE_PATH)
            ?: error("用户资料为空")

        val loadedPerson = ZhihuJson.decodeJson<DataHolder.People>(jojo)
        val urlToken = loadedPerson.urlToken

        environment.postHistoryDestination(
            Person(
                id = loadedPerson.id,
                name = loadedPerson.name,
                urlToken = urlToken ?: "",
            ),
        )

        this.avatar = loadedPerson.avatarUrl
        this.name = loadedPerson.name
        this.headline = loadedPerson.headline
        this.officialBadge = loadedPerson.badgeV2.officialBadge()
        this.officialBadgeDetails = loadedPerson.badgeV2.officialBadgeDetails()
        this.followerCount = loadedPerson.followerCount
        this.followingCount = loadedPerson.followingCount
        this.answerCount = loadedPerson.answerCount
        this.articleCount = loadedPerson.articlesCount
        this.isFollowing = loadedPerson.isFollowing
        this.isBlocking = loadedPerson.isBlocking
        this.isBlockedInRecommendations = environment.isUserBlocked(loadedPerson.id)
        this.isBlockedAsQuestionAuthor = environment.isQuestionAuthorBlocked(loadedPerson.id)
        this.memberHashId = loadedPerson.id
        this.person.id = loadedPerson.id
        if (urlToken != null) {
            this.person.urlToken = urlToken
        }

        this.githubSocial = try {
            val detailUrl =
                "https://api.zhihu.com/people/${person.urlToken.takeIf(String::isNotBlank) ?: person.id}/profile/detail"
            environment
                .fetchJson(detailUrl, "")
                ?.let { ZhihuJson.decodeJson<DataHolder.People>(it).githubSocialUiState() }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.e("PersonViewModel", "Failed to load optional social media profile detail", error)
            null
        }
    }
}

private fun DataHolder.Answer.toPeopleAnswerDisplayItem(): FeedDisplayItem {
    val destination = Article(
        type = ArticleType.Answer,
        id = id,
        title = question.title,
        authorName = author.name,
        authorBio = author.headline,
        avatarSrc = author.avatarUrl,
        excerpt = excerpt,
    )
    return FeedDisplayItem(
        title = question.title,
        summary = excerpt,
        details = "回答 · $voteupCount 赞同 · $commentCount 评论",
        feed = null,
        navDestinationJson = destination.toFeedDisplayItemNavDestinationJson(),
        raw = this,
    )
}

private fun DataHolder.Article.toPeopleArticleDisplayItem(): FeedDisplayItem {
    val destination = Article(
        type = ArticleType.Article,
        id = id,
        title = title,
        authorName = author.name,
        authorBio = author.headline,
        avatarSrc = author.avatarUrl,
        excerpt = excerpt,
    )
    return FeedDisplayItem(
        title = title,
        summary = excerpt,
        details = "文章 · $voteupCount 赞同 · $commentCount 评论",
        feed = null,
        navDestinationJson = destination.toFeedDisplayItemNavDestinationJson(),
        raw = this,
    )
}

private fun DataHolder.Pin.toPeoplePinDisplayItem(): FeedDisplayItem? {
    val pinId = id.toLongOrNull() ?: return null
    return FeedDisplayItem(
        title = Ksoup.parse(excerptTitle).text(),
        summary = null,
        details = "想法 · $likeCount 赞 · $commentCount 评论",
        feed = null,
        navDestinationJson = Pin(id = pinId, authorName = author.name).toFeedDisplayItemNavDestinationJson(),
        raw = this,
    )
}

private val PEOPLE_SCREEN_TITLES = listOf(
    "回答",
    "文章",
    "动态",
    "收藏",
    "提问",
    "想法",
    "专栏",
    "粉丝",
    "关注",
    "关注订阅",
)

private val PEOPLE_SCREEN_SUBSCRIPTION_TITLES = listOf(
    "订阅的专栏",
    "关注的话题",
    "关注的问题",
    "关注的收藏夹",
)

const val PEOPLE_SCREEN_ROOT_TAG = "people_screen_root"
const val PEOPLE_SCREEN_HEADER_TAG = "people_screen_header"
const val PEOPLE_SCREEN_AVATAR_TAG = "people_screen_avatar"
const val PEOPLE_SCREEN_TAB_ROW_TAG = "people_screen_tab_row"
const val PEOPLE_SCREEN_PAGER_TAG = "people_screen_pager"
const val PEOPLE_SCREEN_ANSWERS_LIST_TAG = "people_screen_answers_list"
const val PEOPLE_SCREEN_ARTICLES_LIST_TAG = "people_screen_articles_list"
const val PEOPLE_SCREEN_ACTIVITIES_LIST_TAG = "people_screen_activities_list"
const val PEOPLE_SCREEN_COLLECTIONS_LIST_TAG = "people_screen_collections_list"
const val PEOPLE_SCREEN_QUESTIONS_LIST_TAG = "people_screen_questions_list"
const val PEOPLE_SCREEN_PINS_LIST_TAG = "people_screen_pins_list"
const val PEOPLE_SCREEN_COLUMNS_LIST_TAG = "people_screen_columns_list"
const val PEOPLE_SCREEN_FOLLOWERS_LIST_TAG = "people_screen_followers_list"
const val PEOPLE_SCREEN_FOLLOWING_LIST_TAG = "people_screen_following_list"
const val PEOPLE_SCREEN_SUBSCRIPTION_DROPDOWN_TAG = "people_screen_subscription_dropdown"
const val PEOPLE_SCREEN_SUBSCRIPTIONS_LIST_TAG = "people_screen_subscriptions_list"
const val PEOPLE_SCREEN_ANSWER_COUNT_TAG = "people_screen_stat_answers"
const val PEOPLE_SCREEN_ARTICLE_COUNT_TAG = "people_screen_stat_articles"
const val PEOPLE_SCREEN_FOLLOWER_COUNT_TAG = "people_screen_stat_followers"
const val PEOPLE_SCREEN_FOLLOWING_COUNT_TAG = "people_screen_stat_following"
const val PEOPLE_SCREEN_ACTION_FAB_TAG = "people_screen_action_fab"
const val PEOPLE_SCREEN_ACTION_MENU_TAG = "people_screen_action_menu"
const val PEOPLE_SCREEN_FOLLOW_BUTTON_TAG = "people_screen_follow_button"
const val PEOPLE_SCREEN_BLOCK_BUTTON_TAG = "people_screen_block_button"
const val PEOPLE_SCREEN_RECOMMENDATION_BLOCK_BUTTON_TAG = "people_screen_recommendation_block_button"
const val PEOPLE_SCREEN_QUESTION_AUTHOR_BLOCK_BUTTON_TAG = "people_screen_question_author_block_button"
const val PEOPLE_SCREEN_SEARCH_BUTTON_TAG = "people_screen_search_button"
const val PEOPLE_SCREEN_GITHUB_STARS_TAG = "people_screen_github_stars"
const val PEOPLE_SCREEN_ANSWER_SORT_HOT_TAG = "people_screen_answer_sort_voteups"
const val PEOPLE_SCREEN_ANSWER_SORT_TIME_TAG = "people_screen_answer_sort_created"
const val PEOPLE_SCREEN_ARTICLE_SORT_HOT_TAG = "people_screen_article_sort_voteups"
const val PEOPLE_SCREEN_ARTICLE_SORT_TIME_TAG = "people_screen_article_sort_created"
const val PEOPLE_SCREEN_OFFICIAL_BADGE_TAG = "people_screen_official_badge"

private fun peopleScreenInitialPage(person: Person): Int {
    val jumpToIndex = PEOPLE_SCREEN_TITLES.indexOf(person.jumpTo)
    return if (jumpToIndex >= 0) jumpToIndex else 0
}

data class GithubSocialUiState(
    val title: String,
    val starCount: String,
    val profileUrl: String,
    val iconUrl: String? = null,
)

internal fun DataHolder.People.githubSocialUiState(): GithubSocialUiState? = socialMedias.firstNotNullOfOrNull { media ->
    if (!media.title.startsWith("GitHub", ignoreCase = true)) {
        return@firstNotNullOfOrNull null
    }
    val starCount = media.modules
        .firstOrNull { it.title.equals("stars", ignoreCase = true) }
        ?.value
        ?.takeIf { it.isNotBlank() }
        ?: return@firstNotNullOfOrNull null
    val profileLink = media.link.takeIf { it.isNotBlank() }
        ?: return@firstNotNullOfOrNull null
    val profileUrl = if (profileLink.startsWith("zhihu://", ignoreCase = true)) {
        // link 是知乎内部 AppView，GitHub 用户名来自同一条社交资料的标题。
        val username = media.title
            .substringAfter('·', missingDelimiterValue = "")
            .trim()
            .takeIf { it.isNotBlank() }
            ?: return@firstNotNullOfOrNull null
        "https://github.com/$username"
    } else {
        profileLink
    }

    GithubSocialUiState(
        title = media.title,
        starCount = starCount,
        profileUrl = profileUrl,
        iconUrl = media.icon.takeIf { it.isNotBlank() },
    )
}

/**
 * 用户主页的生产入口。
 *
 * 用户页展示资料头部、关注/屏蔽状态、回答、文章、想法、收藏等内容 tab，并支持从 `Person.jumpTo` 跳到指定子区域。
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(
    person: Person,
    currentAccount: AccountSettingsAccountState = rememberAccountSettingsAccountState().value,
) {
    val navigator = LocalNavigator.current
    val userMessages = rememberUserMessageSink()
    val settings = rememberSettingsStore()
    val paginationEnvironment = rememberPaginationEnvironment(allowGuestAccess = false)
    val viewModel = composeViewModel { PersonViewModel(person) }
    val coroutineScope = rememberCoroutineScope()
    var showActions by rememberSaveable { mutableStateOf(false) }
    val actionFabOpacity = remember(settings) {
        settings
            .getInt(PREF_FAB_OPACITY, DEFAULT_FAB_OPACITY)
            .coerceIn(10, 100) / 100f
    }
    val isOwnProfile = currentAccount.login &&
        (
            currentAccount.id.isNotBlank() &&
                currentAccount.id == viewModel.memberHashId ||
                !currentAccount.urlToken.isNullOrBlank() &&
                currentAccount.urlToken == viewModel.person.urlToken
        )
    val actionMenuBlurRadius by animateDpAsState(
        targetValue = if (showActions && !isOwnProfile) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "peopleActionMenuBlurRadius",
    )

    val pagerState = rememberPagerState(
        initialPage = peopleScreenInitialPage(person),
        pageCount = { PEOPLE_SCREEN_TITLES.size },
    )
    val readingQueueSourceId = when (pagerState.currentPage) {
        0 -> "people:${person.userTokenOrId}:answers:${viewModel.answersFeedModel.sortBy}"
        1 -> "people:${person.userTokenOrId}:articles:${viewModel.articlesFeedModel.sortBy}"
        2 -> "people:${person.userTokenOrId}:activities:${viewModel.activitiesFeedModel.sort}"
        5 -> "people:${person.userTokenOrId}:pins"
        else -> null
    }
    when (pagerState.currentPage) {
        0 -> RegisterReadingQueueSource(
            sourceId = requireNotNull(readingQueueSourceId),
            items = viewModel.answersFeedModel.allData.map(DataHolder.Answer::toPeopleAnswerDisplayItem),
        )
        1 -> RegisterReadingQueueSource(
            sourceId = requireNotNull(readingQueueSourceId),
            items = viewModel.articlesFeedModel.allData.map(DataHolder.Article::toPeopleArticleDisplayItem),
        )
        2 -> RegisterReadingQueueSource(
            sourceId = requireNotNull(readingQueueSourceId),
            items = viewModel.activitiesFeedModel.displayItems,
        )
        5 -> RegisterReadingQueueSource(
            sourceId = requireNotNull(readingQueueSourceId),
            items = viewModel.pinsFeedModel.allData.mapNotNull(DataHolder.Pin::toPeoplePinDisplayItem),
        )
    }

    LaunchedEffect(viewModel) {
        try {
            viewModel.load(paginationEnvironment)
        } catch (e: Exception) {
            userMessages.showShortMessage("加载用户信息失败: ${e.message}")
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        try {
            viewModel.subFeedModels.getOrNull(pagerState.currentPage)?.let { feedModel ->
                val hasData = when (feedModel) {
                    is BaseFeedViewModel -> feedModel.allData.isNotEmpty() || feedModel.displayItems.isNotEmpty()
                    else -> feedModel.allData.isNotEmpty()
                }
                if (!hasData) {
                    feedModel.loadMore(paginationEnvironment)
                }
            }
        } catch (e: Exception) {
            userMessages.showShortMessage("加载页面内容失败: ${e.message}")
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .testTag(PEOPLE_SCREEN_ROOT_TAG)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
                .blur(actionMenuBlurRadius),
            topBar = {
                Box {
                    TopAppBar(
                        title = {
                            UserInfoHeader(
                                viewModel = viewModel,
                                pagerState = pagerState,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .testTag(PEOPLE_SCREEN_HEADER_TAG),
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors().copy(
                            scrolledContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                        scrollBehavior = scrollBehavior,
                        expandedHeight = 184.dp,
                    )
                    if (viewModel.memberHashId.isNotBlank() && viewModel.memberHashId != Person.EMPTY_ID) {
                        IconButton(
                            onClick = {
                                val memberName = viewModel.name.takeIf { it.isNotBlank() } ?: person.name
                                navigator.onNavigate(
                                    SearchDestination(
                                        restrictedMemberHashId = viewModel.memberHashId,
                                        restrictedMemberName = memberName,
                                    ),
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 32.dp, end = 8.dp)
                                .testTag(PEOPLE_SCREEN_SEARCH_BUTTON_TAG),
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "搜索 TA 的创作")
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp),
            ) {
                PrimaryScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 4.dp,
                    minTabWidth = 64.dp,
                    modifier = Modifier.testTag(PEOPLE_SCREEN_TAB_ROW_TAG),
                ) {
                    PEOPLE_SCREEN_TITLES.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            modifier = Modifier.testTag("people_screen_tab_$index"),
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(PEOPLE_SCREEN_PAGER_TAG),
                ) { page ->
                    when (page) {
                        0 -> {
                            // 回答
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("people_screen_page_$page"),
                            ) {
                                SortBar(
                                    currentSort = viewModel.answersFeedModel.sortBy,
                                    onSortChange = { viewModel.answersFeedModel.changeSortBy(it, paginationEnvironment) },
                                    hotTag = PEOPLE_SCREEN_ANSWER_SORT_HOT_TAG,
                                    timeTag = PEOPLE_SCREEN_ANSWER_SORT_TIME_TAG,
                                )
                                PaginatedList(
                                    items = viewModel.answersFeedModel.allData,
                                    onLoadMore = { viewModel.answersFeedModel.loadMore(paginationEnvironment) },
                                    isEnd = { viewModel.answersFeedModel.isEnd },
                                    footer = ProgressIndicatorFooter,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag(PEOPLE_SCREEN_ANSWERS_LIST_TAG),
                                    key = { it.id },
                                ) {
                                    FeedCard(
                                        it.toPeopleAnswerDisplayItem(),
                                        readingQueueSourceId = readingQueueSourceId,
                                        modifier = Modifier.testTag("people_screen_answer_item_${it.id}"),
                                        horizontalPadding = 4.dp,
                                    ) { _, destination ->
                                        destination?.let(navigator.onNavigate)
                                    }
                                }
                            }
                        }

                        1 -> {
                            // 文章
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("people_screen_page_$page"),
                            ) {
                                SortBar(
                                    currentSort = viewModel.articlesFeedModel.sortBy,
                                    onSortChange = { viewModel.articlesFeedModel.changeSortBy(it, paginationEnvironment) },
                                    hotTag = PEOPLE_SCREEN_ARTICLE_SORT_HOT_TAG,
                                    timeTag = PEOPLE_SCREEN_ARTICLE_SORT_TIME_TAG,
                                )
                                PaginatedList(
                                    items = viewModel.articlesFeedModel.allData,
                                    onLoadMore = { viewModel.articlesFeedModel.loadMore(paginationEnvironment) },
                                    isEnd = { viewModel.articlesFeedModel.isEnd },
                                    footer = ProgressIndicatorFooter,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag(PEOPLE_SCREEN_ARTICLES_LIST_TAG),
                                    key = { it.id },
                                ) {
                                    FeedCard(
                                        it.toPeopleArticleDisplayItem(),
                                        readingQueueSourceId = readingQueueSourceId,
                                        modifier = Modifier.testTag("people_screen_article_item_${it.id}"),
                                        horizontalPadding = 4.dp,
                                    ) { _, destination ->
                                        destination?.let(navigator.onNavigate)
                                    }
                                }
                            }
                        }

                        2 -> {
                            // 动态
                            PaginatedList(
                                items = viewModel.activitiesFeedModel.displayItems,
                                onLoadMore = { viewModel.activitiesFeedModel.loadMore(paginationEnvironment) },
                                isEnd = { viewModel.activitiesFeedModel.isEnd },
                                footer = ProgressIndicatorFooter,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag(PEOPLE_SCREEN_ACTIVITIES_LIST_TAG),
                            ) {
                                FeedCard(
                                    it,
                                    readingQueueSourceId = readingQueueSourceId,
                                    modifier = Modifier.testTag("people_screen_activity_item_${it.stableKey}"),
                                    horizontalPadding = 4.dp,
                                )
                            }
                        }

                        3 -> {
                            // 收藏
                            PaginatedList(
                                items = viewModel.collectionsFeedModel.allData,
                                onLoadMore = { viewModel.collectionsFeedModel.loadMore(paginationEnvironment) },
                                isEnd = { viewModel.collectionsFeedModel.isEnd },
                                footer = ProgressIndicatorFooter,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag(PEOPLE_SCREEN_COLLECTIONS_LIST_TAG),
                                key = { it.id },
                            ) { collection ->
                                CollectionListItem(
                                    collection = collection,
                                    itemTag = "people_screen_collection_item_${collection.id}",
                                )
                            }
                        }

                        4 -> {
                            // 提问
                            PaginatedList(
                                items = viewModel.questionsFeedModel.allData,
                                onLoadMore = { viewModel.questionsFeedModel.loadMore(paginationEnvironment) },
                                isEnd = { viewModel.questionsFeedModel.isEnd },
                                footer = ProgressIndicatorFooter,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag(PEOPLE_SCREEN_QUESTIONS_LIST_TAG),
                                key = { it.id },
                            ) { question ->
                                QuestionListItem(
                                    question = question,
                                    itemTag = "people_screen_question_item_${question.id}",
                                )
                            }
                        }

                        5 -> {
                            // 想法
                            PaginatedList(
                                items = viewModel.pinsFeedModel.allData,
                                onLoadMore = { viewModel.pinsFeedModel.loadMore(paginationEnvironment) },
                                isEnd = { viewModel.pinsFeedModel.isEnd },
                                footer = ProgressIndicatorFooter,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag(PEOPLE_SCREEN_PINS_LIST_TAG),
                                key = { it.id },
                            ) { pin ->
                                PinListItem(
                                    pin = pin,
                                    itemTag = "people_screen_pin_item_${pin.id}",
                                    readingQueueSourceId = readingQueueSourceId,
                                )
                            }
                        }

                        6 -> {
                            // 专栏
                            PaginatedList(
                                items = viewModel.columnsFeedModel.allData,
                                onLoadMore = { viewModel.columnsFeedModel.loadMore(paginationEnvironment) },
                                isEnd = { viewModel.columnsFeedModel.isEnd },
                                footer = ProgressIndicatorFooter,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag(PEOPLE_SCREEN_COLUMNS_LIST_TAG),
                                key = { it.id },
                            ) { column ->
                                ColumnListItem(
                                    column = column,
                                    itemTag = "people_screen_column_item_${column.id}",
                                )
                            }
                        }

                        7 -> {
                            // 粉丝
                            PaginatedList(
                                items = viewModel.followersFeedModel.allData,
                                onLoadMore = { viewModel.followersFeedModel.loadMore(paginationEnvironment) },
                                isEnd = { viewModel.followersFeedModel.isEnd },
                                footer = ProgressIndicatorFooter,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag(PEOPLE_SCREEN_FOLLOWERS_LIST_TAG),
                                key = { it.id },
                            ) { people ->
                                PeopleListItem(
                                    people = people,
                                    itemTag = "people_screen_follower_item_${people.id}",
                                    actionTag = "people_screen_follower_action_${people.id}",
                                )
                            }
                        }

                        8 -> {
                            // 关注
                            PaginatedList(
                                items = viewModel.followingFeedModel.allData,
                                onLoadMore = { viewModel.followingFeedModel.loadMore(paginationEnvironment) },
                                isEnd = { viewModel.followingFeedModel.isEnd },
                                footer = ProgressIndicatorFooter,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag(PEOPLE_SCREEN_FOLLOWING_LIST_TAG),
                                key = { it.id },
                            ) { people ->
                                PeopleListItem(
                                    people = people,
                                    itemTag = "people_screen_following_item_${people.id}",
                                    actionTag = "people_screen_following_action_${people.id}",
                                )
                            }
                        }

                        9 -> {
                            FollowingSubscriptionsPage(
                                viewModel = viewModel,
                                onLoadMore = { subscriptionPage ->
                                    when (subscriptionPage) {
                                        0 -> viewModel.followingColumnsFeedModel.loadMore(paginationEnvironment)
                                        1 -> viewModel.followingTopicsFeedModel.loadMore(paginationEnvironment)
                                        2 -> viewModel.followingQuestionsFeedModel.loadMore(paginationEnvironment)
                                        3 -> viewModel.followingCollectionsFeedModel.loadMore(paginationEnvironment)
                                    }
                                },
                                modifier = Modifier.testTag("people_screen_page_$page"),
                            )
                        }
                    }
                }
            }
        }

        if (!isOwnProfile) {
            AnimatedVisibility(
                visible = showActions,
                enter = fadeIn(animationSpec = tween(durationMillis = 120)),
                exit = fadeOut(animationSpec = tween(durationMillis = 120)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.16f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            showActions = false
                        },
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp),
                horizontalAlignment = Alignment.End,
            ) {
                AnimatedVisibility(
                    visible = showActions,
                    enter = fadeIn(animationSpec = tween(durationMillis = 120)) +
                        scaleIn(
                            initialScale = 0.92f,
                            transformOrigin = TransformOrigin(1f, 1f),
                            animationSpec = tween(durationMillis = 180),
                        ) +
                        slideInVertically(animationSpec = tween(durationMillis = 180)) { it / 8 },
                    exit = fadeOut(animationSpec = tween(durationMillis = 90)) +
                        scaleOut(
                            targetScale = 0.96f,
                            transformOrigin = TransformOrigin(1f, 1f),
                            animationSpec = tween(durationMillis = 120),
                        ) +
                        slideOutVertically(animationSpec = tween(durationMillis = 120)) { it / 8 },
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            modifier = Modifier
                                .width(180.dp)
                                .testTag(PEOPLE_SCREEN_ACTION_MENU_TAG),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            tonalElevation = 6.dp,
                            shadowElevation = 6.dp,
                        ) {
                            Column {
                                DropdownMenuItem(
                                    text = { Text(if (viewModel.isFollowing) "取消关注" else "关注") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (viewModel.isFollowing) {
                                                Icons.Default.PersonRemove
                                            } else {
                                                Icons.Default.PersonAddAlt1
                                            },
                                            contentDescription = if (viewModel.isFollowing) "取消关注" else "关注",
                                        )
                                    },
                                    onClick = {
                                        showActions = false
                                        coroutineScope.launch {
                                            try {
                                                viewModel.toggleFollow(paginationEnvironment)
                                            } catch (e: Exception) {
                                                userMessages.showShortMessage("操作失败: ${e.message}")
                                            }
                                        }
                                    },
                                    modifier = Modifier.testTag(PEOPLE_SCREEN_FOLLOW_BUTTON_TAG),
                                )
                                DropdownMenuItem(
                                    text = { Text(if (viewModel.isBlocking) "取消拉黑" else "拉黑") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (viewModel.isBlocking) Icons.Default.LockOpen else Icons.Default.Block,
                                            contentDescription = if (viewModel.isBlocking) "取消拉黑" else "拉黑",
                                        )
                                    },
                                    onClick = {
                                        showActions = false
                                        coroutineScope.launch {
                                            try {
                                                viewModel.toggleBlock(paginationEnvironment)
                                            } catch (e: Exception) {
                                                userMessages.showShortMessage("操作失败: ${e.message}")
                                            }
                                        }
                                    },
                                    modifier = Modifier.testTag(PEOPLE_SCREEN_BLOCK_BUTTON_TAG),
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(if (viewModel.isBlockedInRecommendations) "取消屏蔽推荐" else "屏蔽推荐")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (viewModel.isBlockedInRecommendations) {
                                                Icons.Default.Visibility
                                            } else {
                                                Icons.Default.VisibilityOff
                                            },
                                            contentDescription = if (viewModel.isBlockedInRecommendations) {
                                                "取消屏蔽推荐"
                                            } else {
                                                "屏蔽推荐"
                                            },
                                        )
                                    },
                                    onClick = {
                                        showActions = false
                                        coroutineScope.launch {
                                            try {
                                                viewModel.toggleRecommendationBlock(paginationEnvironment)
                                                userMessages.showShortMessage(
                                                    if (viewModel.isBlockedInRecommendations) {
                                                        "已屏蔽推荐"
                                                    } else {
                                                        "已取消屏蔽推荐"
                                                    },
                                                )
                                            } catch (e: Exception) {
                                                userMessages.showShortMessage("操作失败: ${e.message}")
                                            }
                                        }
                                    },
                                    modifier = Modifier.testTag(PEOPLE_SCREEN_RECOMMENDATION_BLOCK_BUTTON_TAG),
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(if (viewModel.isBlockedAsQuestionAuthor) "取消屏蔽其提问" else "屏蔽其提问")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (viewModel.isBlockedAsQuestionAuthor) {
                                                Icons.Default.QuestionAnswer
                                            } else {
                                                Icons.Default.SpeakerNotesOff
                                            },
                                            contentDescription = if (viewModel.isBlockedAsQuestionAuthor) {
                                                "取消屏蔽其提问"
                                            } else {
                                                "屏蔽其提问"
                                            },
                                        )
                                    },
                                    onClick = {
                                        showActions = false
                                        coroutineScope.launch {
                                            try {
                                                viewModel.toggleQuestionAuthorBlock(paginationEnvironment)
                                                userMessages.showShortMessage(
                                                    if (viewModel.isBlockedAsQuestionAuthor) {
                                                        "已屏蔽其提问"
                                                    } else {
                                                        "已取消屏蔽其提问"
                                                    },
                                                )
                                            } catch (e: Exception) {
                                                userMessages.showShortMessage("操作失败: ${e.message}")
                                            }
                                        }
                                    },
                                    modifier = Modifier.testTag(PEOPLE_SCREEN_QUESTION_AUTHOR_BLOCK_BUTTON_TAG),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                FloatingActionButton(
                    modifier = Modifier.testTag(PEOPLE_SCREEN_ACTION_FAB_TAG),
                    onClick = { showActions = !showActions },
                    shape = CircleShape,
                    containerColor = FloatingActionButtonDefaults.containerColor.copy(
                        alpha = actionFabOpacity,
                    ),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = actionFabOpacity,
                    ),
                    elevation = if (actionFabOpacity < 1f) {
                        FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
                    } else {
                        FloatingActionButtonDefaults.elevation()
                    },
                ) {
                    Icon(
                        imageVector = if (showActions) Icons.Default.Close else Icons.Default.Shield,
                        contentDescription = if (showActions) "收起用户操作" else "用户关系与屏蔽操作",
                    )
                }
            }
        }
    }
}

@Composable
private fun FollowingSubscriptionsPage(
    viewModel: PersonViewModel,
    onLoadMore: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedPage by rememberSaveable { mutableIntStateOf(0) }
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(selectedPage) {
        onLoadMore(selectedPage)
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "订阅内容",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clickable { dropdownExpanded = true }
                        .testTag(PEOPLE_SCREEN_SUBSCRIPTION_DROPDOWN_TAG),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = PEOPLE_SCREEN_SUBSCRIPTION_TITLES[selectedPage],
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "选择订阅类型",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.width(152.dp),
                    ) {
                        PEOPLE_SCREEN_SUBSCRIPTION_TITLES.forEachIndexed { index, title ->
                            DropdownMenuItem(
                                text = { Text(title) },
                                leadingIcon = {
                                    if (selectedPage == index) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "当前选择",
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(24.dp))
                                    }
                                },
                                onClick = {
                                    selectedPage = index
                                    dropdownExpanded = false
                                },
                                modifier = Modifier.testTag("people_screen_subscription_option_$index"),
                            )
                        }
                    }
                }
            }
        }

        when (selectedPage) {
            0 -> PaginatedList(
                items = viewModel.followingColumnsFeedModel.allData,
                onLoadMore = { onLoadMore(0) },
                isEnd = { viewModel.followingColumnsFeedModel.isEnd },
                footer = ProgressIndicatorFooter,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(PEOPLE_SCREEN_SUBSCRIPTIONS_LIST_TAG),
                key = { it.id },
            ) { column ->
                ColumnListItem(
                    column = column,
                    itemTag = "people_screen_column_item_${column.id}",
                )
            }

            1 -> PaginatedList(
                items = viewModel.followingTopicsFeedModel.allData,
                onLoadMore = { onLoadMore(1) },
                isEnd = { viewModel.followingTopicsFeedModel.isEnd },
                footer = ProgressIndicatorFooter,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(PEOPLE_SCREEN_SUBSCRIPTIONS_LIST_TAG),
                key = { it.displayId },
            ) { topic ->
                FollowedTopicListItem(topic)
            }

            2 -> PaginatedList(
                items = viewModel.followingQuestionsFeedModel.allData,
                onLoadMore = { onLoadMore(2) },
                isEnd = { viewModel.followingQuestionsFeedModel.isEnd },
                footer = ProgressIndicatorFooter,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(PEOPLE_SCREEN_SUBSCRIPTIONS_LIST_TAG),
                key = { it.id },
            ) { question ->
                FollowedQuestionListItem(question)
            }

            3 -> PaginatedList(
                items = viewModel.followingCollectionsFeedModel.allData,
                onLoadMore = { onLoadMore(3) },
                isEnd = { viewModel.followingCollectionsFeedModel.isEnd },
                footer = ProgressIndicatorFooter,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(PEOPLE_SCREEN_SUBSCRIPTIONS_LIST_TAG),
                key = { it.id },
            ) { collection ->
                CollectionListItem(
                    collection = collection,
                    itemTag = "people_screen_collection_item_${collection.id}",
                )
            }
        }
    }
}

@Composable
private fun CollectionListItem(
    collection: DataHolder.Collection,
    itemTag: String? = null,
) {
    val navigator = LocalNavigator.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (itemTag != null) Modifier.testTag(itemTag) else Modifier)
            .clickable {
                navigator.onNavigate(CollectionContent(collection.id))
            }.padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        Text(
            text = collection.title,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "${collection.answerCount} 内容 · ${collection.followerCount} 关注",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun QuestionListItem(
    question: DataHolder.Question,
    itemTag: String? = null,
) {
    val navigator = LocalNavigator.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (itemTag != null) Modifier.testTag(itemTag) else Modifier)
            .clickable {
                navigator.onNavigate(Question(question.id, question.title))
            }.padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        Text(
            text = question.title,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "${question.answerCount} 回答 · ${question.followerCount} 关注",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun PinListItem(
    pin: DataHolder.Pin,
    itemTag: String? = null,
    readingQueueSourceId: String? = null,
) {
    val navigator = LocalNavigator.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (itemTag != null) Modifier.testTag(itemTag) else Modifier)
            .clickable {
                navigator.onNavigate(
                    Pin(
                        id = pin.id.toLong(),
                        authorName = pin.author.name,
                        readingQueueSourceId = readingQueueSourceId,
                    ),
                )
            }.padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        val text = remember { Ksoup.parse(pin.excerptTitle).text() }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "${pin.likeCount} 赞 · ${pin.commentCount} 评论",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun ColumnListItem(
    column: DataHolder.Column,
    itemTag: String? = null,
) {
    val openZhihuWebUrl = rememberZhihuWebUrlOpener()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (itemTag != null) Modifier.testTag(itemTag) else Modifier)
            .clickable {
                openZhihuWebUrl(column.webUrl())
            }.padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = column.title,
                style = MaterialTheme.typography.titleMedium,
            )
            if (column.description.isNotEmpty()) {
                Text(
                    text = column.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Text(
                text = "${column.articlesCount} 文章 · ${column.followerCount.coerceAtLeast(column.followers)} 关注",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun FollowedQuestionListItem(question: FollowedQuestion) {
    val navigator = LocalNavigator.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .testTag("people_screen_followed_question_item_${question.id}")
            .clickable {
                question.id.toLongOrNull()?.let {
                    navigator.onNavigate(Question(it, question.title))
                }
            },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Text(
            text = question.title,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun FollowedTopicListItem(topic: FollowedTopic) {
    val navigator = LocalNavigator.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("people_screen_followed_topic_item_${topic.displayId}")
            .clickable {
                navigator.onNavigate(
                    com.github.zly2006.zhihu.navigation
                        .Topic(topic.displayId, topic.displayName),
                )
            }.padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = topic.displayAvatarUrl,
            contentDescription = "话题头像",
            modifier = Modifier
                .padding(end = 12.dp)
                .size(40.dp)
                .clip(CircleShape),
        )
        Text(
            text = topic.displayName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun DataHolder.Column.webUrl(): String = when {
    url.contains("/api/v4/columns/") ->
        url
            .replace("http://", "https://")
            .replace("/api/v4/columns/", "/column/")

    url.startsWith("http") && !url.contains("/api/") -> url.replace("http://", "https://")
    else -> "https://www.zhihu.com/column/$id"
}

@Composable
private fun PeopleListItem(
    people: DataHolder.People,
    itemTag: String? = null,
    actionTag: String? = null,
) {
    val navigator = LocalNavigator.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (itemTag != null) Modifier.testTag(itemTag) else Modifier)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = people.avatarUrl,
            contentDescription = "用户头像",
            modifier = Modifier
                .padding(end = 12.dp)
                .size(48.dp)
                .clip(CircleShape),
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = people.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                val officialBadge = people.badgeV2.officialBadge()
                if (officialBadge?.isUsefulInList == true) {
                    AuthorBadge(
                        badge = officialBadge,
                        compact = true,
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }
            }
            if (people.headline.isNotEmpty()) {
                Text(
                    text = people.headline,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Text(
                    text = "${people.answerCount} 回答",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${people.articlesCount} 文章",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${people.followerCount} 粉丝",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        OutlinedButton(
            onClick = {
                navigator.onNavigate(
                    Person(
                        id = people.id,
                        name = people.name,
                        urlToken = people.urlToken ?: "",
                    ),
                )
            },
            modifier = if (actionTag != null) Modifier.testTag(actionTag) else Modifier,
        ) {
            Text("查看")
        }
    }
}

@Composable
private fun StatItem(label: String, value: Int, onClick: () -> Unit = {}, tag: String? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .then(if (tag != null) Modifier.testTag(tag) else Modifier)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
    ) {
        Text(text = value.toString(), style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun OfficialBadgeDetails(
    badges: List<OfficialBadge>,
    modifier: Modifier = Modifier,
) {
    if (badges.isEmpty()) return
    Column(modifier = modifier) {
        badges.forEach { badge ->
            Row(
                modifier = Modifier.padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (badge.iconUrl.isNotBlank()) {
                    if (badge.iconUrl == DataHolder.ZH_PLUS_AUTHOR_BADGE_ICON) {
                        Image(
                            painter = painterResource(Res.drawable.ic_zh_plus_author_badge),
                            contentDescription = badge.description,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(18.dp),
                        )
                    } else {
                        AsyncImage(
                            model = badge.iconUrl,
                            contentDescription = badge.description,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(18.dp),
                        )
                    }
                }
                Text(
                    text = "${badge.peopleDetailTitle}: ${badge.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private val OfficialBadge.peopleDetailTitle: String
    get() = when {
        title == "认证" || title == "已认证的个人" -> "认证信息"
        else -> title
    }

@Composable
private fun SortBar(
    currentSort: String,
    onSortChange: (String) -> Unit,
    hotTag: String? = null,
    timeTag: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(
            onClick = { onSortChange("voteups") },
            modifier = Modifier
                .weight(1f)
                .then(if (hotTag != null) Modifier.testTag(hotTag) else Modifier),
            shape = RoundedCornerShape(8.dp),
            colors = if (currentSort == "voteups") {
                ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                ButtonDefaults.outlinedButtonColors()
            },
        ) {
            Text("按热度")
        }
        OutlinedButton(
            onClick = { onSortChange("created") },
            modifier = Modifier
                .weight(1f)
                .then(if (timeTag != null) Modifier.testTag(timeTag) else Modifier),
            shape = RoundedCornerShape(8.dp),
            colors = if (currentSort == "created") {
                ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                ButtonDefaults.outlinedButtonColors()
            },
        ) {
            Text("按时间")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserInfoHeader(
    viewModel: PersonViewModel,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val openImagePreview = rememberImagePreviewOpener()
    val openExternalUrl = rememberExternalUrlOpener()
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = viewModel.avatar,
                contentDescription = "用户头像",
                modifier = Modifier
                    .testTag(PEOPLE_SCREEN_AVATAR_TAG)
                    .padding(end = 16.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable {
                        openImagePreview(viewModel.avatar.substringBefore("_") + ".jpg")
                    },
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        viewModel.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    viewModel.officialBadge?.let { badge ->
                        AuthorBadge(
                            badge = badge,
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .testTag(PEOPLE_SCREEN_OFFICIAL_BADGE_TAG),
                        )
                    }
                }
                Text(
                    viewModel.headline,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                OfficialBadgeDetails(
                    badges = viewModel.officialBadgeDetails,
                    modifier = Modifier.padding(top = 6.dp),
                )
                viewModel.githubSocial?.let { githubSocial ->
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .testTag(PEOPLE_SCREEN_GITHUB_STARS_TAG)
                            .clickable { openExternalUrl(githubSocial.profileUrl) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        githubSocial.iconUrl?.let { iconUrl ->
                            AsyncImage(
                                model = iconUrl,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        Text(
                            text = githubSocial.title,
                            modifier = Modifier.weight(1f, fill = false),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "· ${githubSocial.starCount} stars",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            StatItem("回答", viewModel.answerCount, onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }, tag = PEOPLE_SCREEN_ANSWER_COUNT_TAG)
            StatItem("文章", viewModel.articleCount, onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(1)
                }
            }, tag = PEOPLE_SCREEN_ARTICLE_COUNT_TAG)
            StatItem("粉丝", viewModel.followerCount, onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(7)
                }
            }, tag = PEOPLE_SCREEN_FOLLOWER_COUNT_TAG)
            StatItem("关注", viewModel.followingCount, onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(8)
                }
            }, tag = PEOPLE_SCREEN_FOLLOWING_COUNT_TAG)
        }
    }
}
