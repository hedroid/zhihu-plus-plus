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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fleeksoft.ksoup.Ksoup
import com.github.zly2006.zhihu.data.DataHolder
import com.github.zly2006.zhihu.data.decodeQuestionContentDetail
import com.github.zly2006.zhihu.navigation.LocalNavigator
import com.github.zly2006.zhihu.navigation.Question
import com.github.zly2006.zhihu.navigation.Topic
import com.github.zly2006.zhihu.navigation.WriteAnswer
import com.github.zly2006.zhihu.platform.rememberSettingsStore
import com.github.zly2006.zhihu.platform.rememberUserMessageSink
import com.github.zly2006.zhihu.platform.rememberZhihuWebUrlOpener
import com.github.zly2006.zhihu.reading.RegisterReadingQueueSource
import com.github.zly2006.zhihu.ui.components.CommentScreenComponent
import com.github.zly2006.zhihu.ui.components.FeedCard
import com.github.zly2006.zhihu.ui.components.FeedPullToRefresh
import com.github.zly2006.zhihu.ui.components.PaginatedList
import com.github.zly2006.zhihu.ui.components.ProgressIndicatorFooter
import com.github.zly2006.zhihu.ui.components.ShareDialog
import com.github.zly2006.zhihu.ui.components.getShareText
import com.github.zly2006.zhihu.ui.components.handleShareAction
import com.github.zly2006.zhihu.ui.components.rememberShareActionExecutor
import com.github.zly2006.zhihu.util.formatCompactCount
import com.github.zly2006.zhihu.viewmodel.ContentLoadEnvironment
import com.github.zly2006.zhihu.viewmodel.addReadHistory
import com.github.zly2006.zhihu.viewmodel.feed.QuestionFeedViewModel
import com.github.zly2006.zhihu.viewmodel.rememberPaginationEnvironment
import kotlinx.coroutines.launch

const val QUESTION_SCREEN_LIST_TAG = "question_screen_list"
const val QUESTION_TITLE_TAG = "question_title"
const val QUESTION_DETAIL_TOGGLE_TAG = "question_detail_toggle"
const val QUESTION_DETAIL_CONTENT_TAG = "question_detail_content"
const val QUESTION_DETAIL_PREVIEW_TAG = "question_detail_preview"
const val QUESTION_SORT_DEFAULT_TAG = "question_sort_default"
const val QUESTION_SORT_UPDATED_TAG = "question_sort_updated"
const val QUESTION_FOLLOW_BUTTON_TAG = "question_follow_button"
const val QUESTION_VIEW_LOG_BUTTON_TAG = "question_view_log_button"
const val QUESTION_SHARE_BUTTON_TAG = "question_share_button"
const val QUESTION_WRITE_ANSWER_BUTTON_TAG = "question_write_answer_button"
const val QUESTION_COMMENTS_BUTTON_TAG = "question_comments_button"
const val QUESTION_STATS_TAG = "question_stats"

private suspend fun loadQuestion(
    environment: ContentLoadEnvironment,
    question: Question,
): DataHolder.Question? {
    environment.addReadHistory(question.questionId.toString(), "question")
    val include =
        "read_count,visit_count,answer_count,voteup_count,comment_count,follower_count,detail,excerpt,author,relationship.is_following,topics"
    val jsonObject =
        environment.fetchJson("https://www.zhihu.com/api/v4/questions/${question.questionId}", include)
            ?: return null
    val questionData = decodeQuestionContentDetail(jsonObject)
    environment.postHistoryDestination(Question(question.questionId, questionData.title))
    environment.recordContentOpenEvent(destination = question, questionId = question.questionId)
    return questionData
}

/**
 * 问题详情页。
 *
 * 顶部展示问题标题、描述、关注状态和统计信息，主体是该问题下回答的信息流列表。页面会记录内容打开来源和历史记录， 并复用文章/回答卡片、评论底部表单和分享入口；正文描述同样受
 * WebView/Markdown 渲染设置影响。
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    question: Question,
) {
    val readingPlayerOverlayPadding = LocalReadingPlayerOverlayPadding.current
    val settings = rememberSettingsStore()
    val executeShareAction = rememberShareActionExecutor()
    val openZhihuWebUrl = rememberZhihuWebUrlOpener()
    val navigator = LocalNavigator.current
    val viewModel: QuestionFeedViewModel = viewModel(key = "question_${question.questionId}") {
        QuestionFeedViewModel(question.questionId)
    }
    val answerReadingQueueSourceId = "question:${question.questionId}:answers:${viewModel.sortOrder}"
    RegisterReadingQueueSource(
        sourceId = answerReadingQueueSourceId,
        items = viewModel.displayItems,
    )
    val paginationEnvironment = rememberPaginationEnvironment(allowGuestAccess = false)
    val answerSwitchState = paginationEnvironment.articleAnswerSwitchState()
    val listState = rememberLazyListState()
    var questionContent by remember(question.questionId) { mutableStateOf("") }
    var answerCount by remember(question.questionId) { mutableIntStateOf(0) }
    var visitCount by remember(question.questionId) { mutableIntStateOf(0) }
    var commentCount by remember(question.questionId) { mutableIntStateOf(0) }
    var followerCount by remember(question.questionId) { mutableIntStateOf(0) }
    var title by remember(question.questionId, question.title) { mutableStateOf(question.title) }
    var showComments by rememberSaveable(question.questionId) { mutableStateOf(false) }
    var isFollowing by remember(question.questionId) { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    val userMessages = rememberUserMessageSink()
    var isQuestionDetailExpanded by rememberSaveable(question.questionId) { mutableStateOf(false) }
    var topics by remember(question.questionId) { mutableStateOf(emptyList<DataHolder.Topic>()) }
    val scope = rememberCoroutineScope()
    val questionContentPreview = remember(questionContent) {
        val document = Ksoup.parse(questionContent)
        document.text().trim().ifEmpty {
            "[图片]".takeIf { document.select("img").isNotEmpty() }.orEmpty()
        }
    }
    val shareText = getShareText(question, title)
    val topBarTitleThresholdPx = with(LocalDensity.current) { 160.dp.roundToPx() }
    val showTopBarTitle by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset >= topBarTitleThresholdPx
        }
    }

    // 加载问题详情和答案
    LaunchedEffect(question.questionId, viewModel) {
        if (viewModel.displayItems.isEmpty()) {
            launch { viewModel.refresh(paginationEnvironment) }
        }
        try {
            val questionData = loadQuestion(paginationEnvironment, question)
            if (questionData != null) {
                questionContent = questionData.detail
                title = questionData.title
                answerCount = questionData.answerCount
                visitCount = questionData.visitCount
                commentCount = questionData.commentCount
                followerCount = questionData.followerCount
                isFollowing = questionData.relationship.isFollowing
                topics = questionData.topics
            } else {
                userMessages.showShortMessage("获取问题详情失败")
            }
        } catch (e: Exception) {
            userMessages.showShortMessage("加载失败: ${e.message}")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            QuestionTopBar(
                title = title,
                showTitle = showTopBarTitle,
                onNavigateBack = navigator.onNavigateBack,
                onOpenLog = {
                    try {
                        openZhihuWebUrl("https://www.zhihu.com/question/${question.questionId}/log")
                    } catch (e: Exception) {
                        userMessages.showShortMessage("打开日志失败: ${e.message}")
                    }
                },
                onShare = {
                    if (shareText != null) {
                        handleShareAction(question, settings, executeShareAction) { showShareDialog = true }
                    }
                },
                canShare = shareText != null,
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 6.dp,
                    shadowElevation = 6.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = {
                                navigator.onNavigate(
                                    WriteAnswer(
                                        questionId = question.questionId,
                                        questionTitle = title,
                                        questionDetail = questionContent,
                                    ),
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 40.dp)
                                .testTag(QUESTION_WRITE_ANSWER_BUTTON_TAG),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(2.dp))
                            Text("写回答", maxLines = 1, style = MaterialTheme.typography.labelLarge)
                        }
                        FilledTonalButton(
                            onClick = {
                                scope.launch {
                                    val nextFollowing = !isFollowing
                                    viewModel.followQuestion(paginationEnvironment, nextFollowing)
                                    isFollowing = nextFollowing
                                    followerCount = (followerCount + if (isFollowing) 1 else -1).coerceAtLeast(0)
                                    userMessages.showShortMessage(if (isFollowing) "已关注问题" else "已取消关注问题")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 40.dp)
                                .testTag(QUESTION_FOLLOW_BUTTON_TAG)
                                .semantics { selected = isFollowing },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            colors = if (isFollowing) {
                                ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                )
                            } else {
                                ButtonDefaults.filledTonalButtonColors()
                            },
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = if (isFollowing) "取消关注" else "关注问题",
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(if (isFollowing) "已关注" else "关注问题", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        FeedPullToRefresh(
            viewModel,
            padding = PaddingValues(top = innerPadding.calculateTopPadding()),
        ) {
            PaginatedList(
                items = viewModel.displayItems,
                onLoadMore = { viewModel.loadMore(paginationEnvironment) },
                isEnd = { viewModel.isEnd },
                key = { it.stableKey },
                listState = listState,
                modifier = Modifier
                    .padding(innerPadding)
                    .testTag(QUESTION_SCREEN_LIST_TAG),
                contentPadding = PaddingValues(bottom = readingPlayerOverlayPadding),
                footer = ProgressIndicatorFooter,
                topContent = {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            QuestionHeaderSection(
                                title = title,
                                visitCount = visitCount,
                                commentCount = commentCount,
                                followerCount = followerCount,
                                onShowComments = { showComments = true },
                            )
                            if (questionContent.isNotEmpty() || topics.isNotEmpty()) {
                                Column {
                                    AnimatedVisibility(
                                        visible = isQuestionDetailExpanded,
                                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
                                    ) {
                                        Column(modifier = Modifier.testTag(QUESTION_DETAIL_CONTENT_TAG)) {
                                            QuestionDetailContent(
                                                questionId = question.questionId,
                                                html = questionContent,
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Start,
                                            ) {
                                                TextButton(
                                                    onClick = { isQuestionDetailExpanded = false },
                                                    modifier = Modifier.testTag(QUESTION_DETAIL_TOGGLE_TAG),
                                                ) {
                                                    Icon(Icons.Filled.ExpandLess, contentDescription = null)
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("收起")
                                                }
                                            }
                                        }
                                    }
                                    AnimatedVisibility(
                                        visible = !isQuestionDetailExpanded && questionContentPreview.isNotEmpty(),
                                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { isQuestionDetailExpanded = true }
                                                .testTag(QUESTION_DETAIL_TOGGLE_TAG),
                                        ) {
                                            Text(
                                                text = questionContentPreview,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag(QUESTION_DETAIL_PREVIEW_TAG),
                                                style = MaterialTheme.typography.bodyLarge,
                                                maxLines = 3,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                    if (topics.isNotEmpty()) {
                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            topics.forEach { topic ->
                                                Surface(
                                                    shape = RoundedCornerShape(50),
                                                    color = MaterialTheme.colorScheme.surface,
                                                    border = BorderStroke(
                                                        1.dp,
                                                        MaterialTheme.colorScheme.outlineVariant,
                                                    ),
                                                    onClick = {
                                                        navigator.onNavigate(Topic(topic.id, topic.name))
                                                    },
                                                ) {
                                                    Text(
                                                        text = topic.name,
                                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    stickyHeader(key = "question_sort_header") {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 0.dp,
                        ) {
                            Column {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    TextButton(
                                        onClick = {
                                            viewModel.updateSortOrder("default")
                                            viewModel.refresh(paginationEnvironment)
                                        },
                                        modifier = Modifier
                                            .testTag(QUESTION_SORT_DEFAULT_TAG)
                                            .semantics { selected = viewModel.sortOrder == "default" },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = if (viewModel.sortOrder == "default") {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                        ),
                                    ) {
                                        Text(
                                            "默认",
                                            fontWeight = if (viewModel.sortOrder == "default") {
                                                FontWeight.Bold
                                            } else {
                                                FontWeight.Normal
                                            },
                                        )
                                    }
                                    TextButton(
                                        onClick = {
                                            viewModel.updateSortOrder("updated")
                                            viewModel.refresh(paginationEnvironment)
                                        },
                                        modifier = Modifier
                                            .testTag(QUESTION_SORT_UPDATED_TAG)
                                            .semantics { selected = viewModel.sortOrder == "updated" },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = if (viewModel.sortOrder == "updated") {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                        ),
                                    ) {
                                        Text(
                                            "最新",
                                            fontWeight = if (viewModel.sortOrder == "updated") {
                                                FontWeight.Bold
                                            } else {
                                                FontWeight.Normal
                                            },
                                        )
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        "全部内容 ${formatCompactCount(answerCount)}",
                                        modifier = Modifier.padding(end = 8.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                                )
                            }
                        }
                    }
                },
            ) { item ->
                FeedCard(
                    item = item,
                    readingQueueSourceId = answerReadingQueueSourceId,
                    modifier = Modifier.testTag("question_feed_item_${item.stableKey}"),
                ) { _, destination ->
                    answerSwitchState?.pendingNavigator = viewModel.createAnswerNavigatorFor(item, paginationEnvironment)
                    destination?.let(navigator.onNavigate)
                }
            }
        }
    }
    CommentScreenComponent(
        showComments = showComments,
        onDismiss = { showComments = false },
        content = question,
    )

    // 分享对话框
    if (shareText != null) {
        ShareDialog(
            content = question,
            shareText = shareText,
            showDialog = showShareDialog,
            onDismissRequest = { showShareDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionTopBar(
    title: String,
    showTitle: Boolean,
    onNavigateBack: () -> Unit,
    onOpenLog: () -> Unit,
    onShare: () -> Unit,
    canShare: Boolean,
) {
    TopAppBar(
        title = {
            AnimatedContent(
                targetState = showTitle,
                transitionSpec = {
                    (fadeIn() + slideInVertically(initialOffsetY = { it / 2 })) togetherWith
                        (fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }))
                },
                label = "question_top_bar_title",
            ) { shouldShowTitle ->
                Text(
                    text = if (shouldShowTitle) title else "问题",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
            }
        },
        actions = {
            IconButton(onClick = onOpenLog, modifier = Modifier.testTag(QUESTION_VIEW_LOG_BUTTON_TAG)) {
                Icon(Icons.Filled.History, contentDescription = "日志")
            }
            IconButton(
                onClick = onShare,
                enabled = canShare,
                modifier = Modifier.testTag(QUESTION_SHARE_BUTTON_TAG),
            ) {
                Icon(Icons.Filled.Share, contentDescription = "分享")
            }
        },
    )
}

@Composable
private fun QuestionHeaderSection(
    title: String,
    visitCount: Int,
    commentCount: Int,
    followerCount: Int,
    onShowComments: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SelectionContainer(modifier = Modifier.questionSelectionWorkaround()) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag(QUESTION_TITLE_TAG),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FlowRow(
                modifier = Modifier.weight(1f).testTag(QUESTION_STATS_TAG),
                horizontalArrangement = Arrangement.spacedBy(16.dp), // 水平间距
                verticalArrangement = Arrangement.spacedBy(8.dp), // 垂直间距
            ) {
                StatItem(icon = Icons.Outlined.Visibility, text = "${formatCompactCount(visitCount)} 浏览")
                StatItem(icon = Icons.Outlined.ChatBubbleOutline, text = "${formatCompactCount(commentCount)} 评论")
                StatItem(icon = Icons.Outlined.FavoriteBorder, text = "${formatCompactCount(followerCount)} 关注")
            }
            OutlinedButton(
                onClick = onShowComments,
                modifier = Modifier.testTag(QUESTION_COMMENTS_BUTTON_TAG),
            ) {
                Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = "评论")
                Spacer(Modifier.width(8.dp))
                Text("$commentCount")
            }
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = null, // 装饰性图标不需要无障碍描述
            modifier = Modifier.size(16.dp), // 图标稍微小一点，匹配辅助文字
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(4.dp)) // 图标和文字的间距
        Text(
            text = text,
            // 辅助信息通常使用更小一号的字重，比如 bodySmall 或 labelMedium
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
