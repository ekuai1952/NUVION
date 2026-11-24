package kz.nuvion.app.ui.screens.topics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.nuvion.app.R
import kz.nuvion.app.data.demo.DemoTopics
import kz.nuvion.app.domain.model.TopicProgress
import kz.nuvion.app.ui.components.ErrorState
import kz.nuvion.app.ui.components.IconBadge
import kz.nuvion.app.ui.components.LoadingState
import kz.nuvion.app.ui.components.NuvionButton
import kz.nuvion.app.ui.components.NuvionButtonStyle
import kz.nuvion.app.ui.components.NuvionCard
import kz.nuvion.app.ui.components.GradientHeader
import kz.nuvion.app.ui.components.PillButton
import kz.nuvion.app.ui.components.ProgressRing
import kz.nuvion.app.ui.components.imageVector
import kz.nuvion.app.ui.text
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.ui.theme.SystemBarIcons
import kz.nuvion.app.util.UiState
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.TopicItem
import kz.nuvion.app.viewmodel.TopicsViewModel

@Composable
fun TopicsRoute(
    onBack: () -> Unit,
    onOpenLesson: (String) -> Unit,
    onOpenQuiz: (String) -> Unit,
    onOpenPractice: (String) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenMap: () -> Unit,
    viewModel: TopicsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TopicsScreen(state, onBack, onOpenLesson, onOpenQuiz, onOpenPractice, onOpenLibrary, onOpenMap, viewModel::retry)
}

@Composable
fun TopicsScreen(
    state: UiState<List<TopicItem>>,
    onBack: () -> Unit,
    onOpenLesson: (String) -> Unit,
    onOpenQuiz: (String) -> Unit,
    onOpenPractice: (String) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenMap: () -> Unit,
    onRetry: () -> Unit
) {
    SystemBarIcons(darkIcons = false)
    val dimens = LocalDimens.current
    val listState = rememberLazyListState()
    Column(
        Modifier
            .fillMaxSize()
            .background(NuvionColors.Background)
    ) {
        GradientHeader(
            title = stringResource(R.string.topics_title),
            subtitle = stringResource(R.string.topics_subtitle),
            onBack = onBack
        )
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            when (state) {
                UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(state.kind, onRetry, onBack = onBack)
                is UiState.Success -> LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .widthIn(max = dimens.maxContentWidth)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = dimens.screenPadding, vertical = dimens.spaceL),
                    verticalArrangement = Arrangement.spacedBy(dimens.spaceL)
                ) {
                    items(state.data, key = { it.topic.id }) { item ->
                        TopicCard(
                            item = item,
                            onOpen = { onOpenLesson(item.topic.id) },
                            onQuiz = { onOpenQuiz(item.topic.id) },
                            onPractice = { onOpenPractice(item.topic.id) }
                        )
                    }
                }
            }
        }
        BottomNav(onOpenLibrary, onOpenMap)
    }
}

@Composable
private fun TopicCard(
    item: TopicItem,
    onOpen: () -> Unit,
    onQuiz: () -> Unit,
    onPractice: () -> Unit
) {
    val dimens = LocalDimens.current
    NuvionCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpen,
        onClickLabel = stringResource(R.string.topic_open_lesson)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(item.topic.icon.imageVector(), NuvionColors.Blue, size = dimens.iconBadge)
            Spacer(Modifier.width(dimens.spaceM))
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.topic.title.text(),
                    style = MaterialTheme.typography.titleMedium,
                    color = NuvionColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = item.topic.subtitle.text(),
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvionColors.TextSecondary
                )
            }
            Spacer(Modifier.width(dimens.spaceS))
            ProgressRing(progress = item.progress.fraction, completed = item.progress.isComplete)
        }
        Spacer(Modifier.height(dimens.spaceM))
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.spaceS)) {
            PillButton(
                text = stringResource(R.string.topic_test),
                icon = Icons.Outlined.Description,
                onClick = onQuiz,
                modifier = Modifier.weight(1f)
            )
            PillButton(
                text = stringResource(R.string.topic_problems),
                icon = Icons.Outlined.Edit,
                onClick = onPractice,
                modifier = Modifier.weight(1f),
                yellow = true
            )
        }
    }
}

@Composable
private fun BottomNav(onOpenLibrary: () -> Unit, onOpenMap: () -> Unit) {
    val dimens = LocalDimens.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp)
            .background(NuvionColors.Surface)
            .navigationBarsPadding()
            .padding(horizontal = dimens.screenPadding, vertical = dimens.spaceM),
        horizontalArrangement = Arrangement.spacedBy(dimens.spaceM)
    ) {
        NuvionButton(
            text = stringResource(R.string.nav_library),
            onClick = onOpenLibrary,
            modifier = Modifier.weight(1f),
            style = NuvionButtonStyle.Tonal,
            icon = Icons.Outlined.LocalLibrary
        )
        NuvionButton(
            text = stringResource(R.string.nav_map),
            onClick = onOpenMap,
            modifier = Modifier.weight(1f),
            style = NuvionButtonStyle.Secondary,
            icon = Icons.Outlined.Map
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun TopicsScreenPreview() {
    NuvionTheme {
        TopicsScreen(
            state = UiState.Success(
                DemoTopics.all.mapIndexed { i, topic ->
                    TopicItem(topic, TopicProgress(i < 2, i < 1, if (i < 1) 3 else 0, 3))
                }
            ),
            onBack = {}, onOpenLesson = {}, onOpenQuiz = {}, onOpenPractice = {},
            onOpenLibrary = {}, onOpenMap = {}, onRetry = {}
        )
    }
}
