package kz.nuvion.app.ui.screens.result

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.nuvion.app.R
import kz.nuvion.app.data.demo.DemoTopics
import kz.nuvion.app.domain.model.NextStep
import kz.nuvion.app.domain.model.TopicProgress
import kz.nuvion.app.navigation.ResultType
import kz.nuvion.app.ui.components.ErrorState
import kz.nuvion.app.ui.components.LoadingState
import kz.nuvion.app.ui.components.NuvionButton
import kz.nuvion.app.ui.components.NuvionButtonStyle
import kz.nuvion.app.ui.components.NuvionCard
import kz.nuvion.app.ui.components.NuvionProgressBar
import kz.nuvion.app.ui.components.XpBadge
import kz.nuvion.app.ui.text
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionGradients
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.ui.theme.SystemBarIcons
import kz.nuvion.app.util.UiState
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.ResultData
import kz.nuvion.app.viewmodel.ResultViewModel
import kotlin.random.Random

@Composable
fun ResultRoute(
    onRetry: (ResultType, String) -> Unit,
    onNextStep: (String, NextStep) -> Unit,
    onTopics: () -> Unit,
    onMap: () -> Unit,
    viewModel: ResultViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ResultScreen(
        state = state,
        onRetryLoad = viewModel::retry,
        onRetry = { onRetry(viewModel.type, viewModel.topicId) },
        onNextStep = { step -> onNextStep(viewModel.topicId, step) },
        onTopics = onTopics,
        onMap = onMap
    )
}

@Composable
fun ResultScreen(
    state: UiState<ResultData>,
    onRetryLoad: () -> Unit,
    onRetry: () -> Unit,
    onNextStep: (NextStep) -> Unit,
    onTopics: () -> Unit,
    onMap: () -> Unit
) {
    SystemBarIcons(darkIcons = false)
    val dimens = LocalDimens.current
    Box(
        Modifier
            .fillMaxSize()
            .background(NuvionGradients.NavyHeader)
    ) {
        Confetti(Modifier.fillMaxSize())
        when (state) {
            UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(state.kind, onRetryLoad, onBack = onTopics)
            is UiState.Success -> BoxWithConstraints(Modifier.fillMaxSize()) {
                val minHeight = maxHeight
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = minHeight)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(dimens.screenPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ResultContent(state.data, onRetry, onNextStep, onTopics, onMap)
                }
            }
        }
    }
}

@Composable
private fun ResultContent(
    data: ResultData,
    onRetry: () -> Unit,
    onNextStep: (NextStep) -> Unit,
    onTopics: () -> Unit,
    onMap: () -> Unit
) {
    val dimens = LocalDimens.current
    val title = stringResource(
        when (data.type) {
            ResultType.QUIZ -> if (data.passed) R.string.result_quiz_passed else R.string.result_quiz_failed
            ResultType.PRACTICE -> R.string.result_practice_title
            ResultType.LESSON -> R.string.result_lesson_title
        }
    )
    Column(
        Modifier.widthIn(max = dimens.maxContentWidth),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(if (data.passed) NuvionColors.Yellow else Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = if (data.passed) NuvionColors.Navy else Color.White,
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(Modifier.height(dimens.spaceXl))
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() }
        )
        Spacer(Modifier.height(6.dp))
        Text(data.topic.title.text(), style = MaterialTheme.typography.titleSmall, color = Color.White.copy(alpha = 0.75f))
        Spacer(Modifier.height(dimens.spaceXl))

        NuvionCard(Modifier.fillMaxWidth()) {
            if (data.type != ResultType.LESSON) {
                Text(
                    stringResource(
                        if (data.type == ResultType.QUIZ) R.string.result_score else R.string.result_solved,
                        data.score,
                        data.total
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    color = NuvionColors.TextPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(12.dp))
                NuvionProgressBar(if (data.total == 0) 0f else data.score.toFloat() / data.total, color = if (data.passed) NuvionColors.Success else NuvionColors.Yellow)
                Spacer(Modifier.height(16.dp))
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (data.xpEarned > 0) {
                    XpBadge(stringResource(R.string.xp_reward, data.xpEarned))
                } else {
                    Text(
                        stringResource(if (data.type == ResultType.QUIZ && !data.passed) R.string.result_xp_need_pass else R.string.result_xp_already),
                        style = MaterialTheme.typography.bodyMedium,
                        color = NuvionColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.result_total_xp, data.totalXp),
                style = MaterialTheme.typography.bodySmall,
                color = NuvionColors.TextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            if (data.topicProgress.isComplete) {
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.result_topic_complete),
                    style = MaterialTheme.typography.titleSmall,
                    color = NuvionColors.Success,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(dimens.spaceXl))

        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(dimens.spaceM)) {
            val next = data.nextStep
            when {
                next == NextStep.DONE -> NuvionButton(
                    stringResource(R.string.result_open_map), onMap, Modifier.fillMaxWidth(),
                    style = NuvionButtonStyle.Secondary, icon = Icons.AutoMirrored.Filled.ArrowForward
                )
                else -> NuvionButton(
                    text = stringResource(
                        when (next) {
                            NextStep.LESSON -> R.string.result_next_lesson
                            NextStep.QUIZ -> R.string.result_next_quiz
                            else -> R.string.result_next_practice
                        }
                    ),
                    onClick = { onNextStep(next) },
                    modifier = Modifier.fillMaxWidth(),
                    style = NuvionButtonStyle.Secondary,
                    icon = Icons.AutoMirrored.Filled.ArrowForward
                )
            }
            if (data.type != ResultType.LESSON) {
                NuvionButton(
                    stringResource(R.string.result_retry), onRetry, Modifier.fillMaxWidth(),
                    style = NuvionButtonStyle.Tonal, icon = Icons.Outlined.Refresh
                )
            }
            NuvionButton(
                stringResource(R.string.result_to_topics), onTopics, Modifier.fillMaxWidth(),
                style = NuvionButtonStyle.Outline, icon = Icons.Outlined.Home
            )
        }
    }
}

@Composable
private fun Confetti(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val random = Random(3)
        val colors = listOf(NuvionColors.Yellow, NuvionColors.Blue, Color.White, NuvionColors.BlueLight)
        repeat(40) {
            drawCircle(
                colors[it % colors.size].copy(alpha = 0.35f),
                radius = (2 + random.nextFloat() * 4) * density,
                center = Offset(random.nextFloat() * size.width, random.nextFloat() * size.height * 0.17f)
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun ResultPreview() {
    NuvionTheme {
        ResultScreen(
            state = UiState.Success(
                ResultData(ResultType.QUIZ, DemoTopics.all.first(), 4, 5, true, 100, 250, TopicProgress(true, true, 0, 3))
            ),
            onRetryLoad = {}, onRetry = {}, onNextStep = {}, onTopics = {}, onMap = {}
        )
    }
}
