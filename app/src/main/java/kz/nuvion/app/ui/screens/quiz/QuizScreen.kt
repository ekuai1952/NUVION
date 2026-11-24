package kz.nuvion.app.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kz.nuvion.app.R
import kz.nuvion.app.data.demo.DemoQuizzes
import kz.nuvion.app.data.demo.DemoTopics
import kz.nuvion.app.domain.model.AnswerOption
import kz.nuvion.app.ui.components.ErrorState
import kz.nuvion.app.ui.components.GradientHeader
import kz.nuvion.app.ui.components.LoadingState
import kz.nuvion.app.ui.components.NuvionButton
import kz.nuvion.app.ui.components.NuvionButtonStyle
import kz.nuvion.app.ui.components.NuvionCard
import kz.nuvion.app.ui.components.NuvionProgressBar
import kz.nuvion.app.ui.components.SectionLabel
import kz.nuvion.app.ui.components.XpBadge
import kz.nuvion.app.ui.HapticOnChange
import kz.nuvion.app.ui.text
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionGradients
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.ui.theme.SystemBarIcons
import kz.nuvion.app.util.UiState
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.QuizEvent
import kz.nuvion.app.viewmodel.QuizScreenData
import kz.nuvion.app.viewmodel.QuizSession
import kz.nuvion.app.viewmodel.QuizViewModel

@Composable
fun QuizRoute(
    onBack: () -> Unit,
    onFinished: (topicId: String) -> Unit,
    viewModel: QuizViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val saveError = stringResource(R.string.common_save_error)
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is QuizEvent.Finished -> onFinished(event.topicId)
                QuizEvent.SaveFailed -> scope.launch { snackbarHostState.showSnackbar(saveError) }
            }
        }
    }
    QuizScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onSelect = viewModel::selectOption,
        onCheck = viewModel::checkAnswer,
        onNext = viewModel::next
    )
}

@Composable
fun QuizScreen(
    state: UiState<QuizScreenData>,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onSelect: (String) -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit
) {
    SystemBarIcons(darkIcons = false)
    val dimens = LocalDimens.current
    val data = (state as? UiState.Success)?.data
    Box(Modifier.fillMaxSize().background(NuvionColors.Background)) {
        Column(Modifier.fillMaxSize()) {
            GradientHeader(
                title = stringResource(R.string.quiz_title),
                subtitle = data?.topic?.title?.text(),
                onBack = onBack,
                brush = NuvionGradients.NavyHeader,
                trailing = { if (data != null) XpBadge(stringResource(R.string.xp_reward, data.quiz.xpReward)) },
                bottomContent = {
                    if (data != null) {
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                stringResource(R.string.quiz_counter, data.session.index + 1, data.total),
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                stringResource(R.string.percent_value, data.percent),
                                style = MaterialTheme.typography.labelLarge,
                                color = NuvionColors.Yellow
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        NuvionProgressBar(
                            progress = data.percent / 100f,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            )
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                when (state) {
                    UiState.Loading -> LoadingState()
                    is UiState.Error -> ErrorState(state.kind, onRetry, onBack = onBack)
                    is UiState.Success -> QuestionContent(state.data, onSelect)
                }
            }
            if (data != null) {
                val s = data.session
                Box(
                    Modifier
                        .fillMaxWidth()
                        .shadow(12.dp)
                        .background(NuvionColors.Surface)
                        .navigationBarsPadding()
                        .padding(horizontal = dimens.screenPadding, vertical = dimens.spaceM),
                    contentAlignment = Alignment.Center
                ) {
                    val buttonModifier = Modifier.widthIn(max = dimens.maxContentWidth).fillMaxWidth()
                    if (!s.checked) {
                        NuvionButton(
                            text = stringResource(R.string.quiz_check),
                            onClick = onCheck,
                            modifier = buttonModifier,
                            enabled = s.selectedOptionId != null
                        )
                    } else {
                        NuvionButton(
                            text = stringResource(if (data.isLastQuestion) R.string.quiz_finish else R.string.quiz_next),
                            onClick = onNext,
                            modifier = buttonModifier,
                            style = if (data.isLastQuestion) NuvionButtonStyle.Secondary else NuvionButtonStyle.Navy,
                            loading = data.isSaving
                        )
                    }
                }
            }
        }
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp))
    }
}

@Composable
private fun QuestionContent(data: QuizScreenData, onSelect: (String) -> Unit) {
    val dimens = LocalDimens.current
    val question = data.question
    val session = data.session
    // Vibrate when an answer is checked.
    HapticOnChange(if (session.checked) "${session.index}-${data.isAnswerCorrect}" else null)
    Column(
        Modifier
            .widthIn(max = dimens.maxContentWidth)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimens.screenPadding, vertical = dimens.spaceL)
    ) {
        NuvionCard(Modifier.fillMaxWidth()) {
            SectionLabel(stringResource(R.string.quiz_question_label, session.index + 1))
            Spacer(Modifier.height(8.dp))
            Text(
                question.text.text(),
                style = MaterialTheme.typography.titleLarge,
                color = NuvionColors.TextPrimary,
                modifier = Modifier.semantics { heading() }
            )
        }
        Spacer(Modifier.height(dimens.spaceL))
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(dimens.spaceM)) {
            question.options.forEachIndexed { index, option ->
                AnswerOptionRow(
                    letter = ('A' + index).toString(),
                    option = option,
                    selected = session.selectedOptionId == option.id,
                    checked = session.checked,
                    onClick = { onSelect(option.id) }
                )
            }
        }
        AnimatedVisibility(visible = session.checked, enter = fadeIn() + expandVertically()) {
            Column {
                Spacer(Modifier.height(dimens.spaceL))
                FeedbackCard(correct = data.isAnswerCorrect, explanation = question.explanation.text())
            }
        }
        Spacer(Modifier.height(dimens.spaceL))
    }
}

private enum class OptionVisual { DEFAULT, SELECTED, CORRECT, WRONG, DIMMED }

@Composable
private fun AnswerOptionRow(
    letter: String,
    option: AnswerOption,
    selected: Boolean,
    checked: Boolean,
    onClick: () -> Unit
) {
    val visual = when {
        checked && option.isCorrect -> OptionVisual.CORRECT
        checked && selected -> OptionVisual.WRONG
        checked -> OptionVisual.DIMMED
        selected -> OptionVisual.SELECTED
        else -> OptionVisual.DEFAULT
    }
    val bg by animateColorAsState(
        when (visual) {
            OptionVisual.SELECTED -> NuvionColors.BlueTint
            OptionVisual.CORRECT -> NuvionColors.SuccessTint
            OptionVisual.WRONG -> NuvionColors.ErrorTint
            else -> NuvionColors.Surface
        },
        label = "optionBg"
    )
    val borderColor by animateColorAsState(
        when (visual) {
            OptionVisual.SELECTED -> NuvionColors.Blue
            OptionVisual.CORRECT -> NuvionColors.Success
            OptionVisual.WRONG -> NuvionColors.Error
            else -> NuvionColors.Outline
        },
        label = "optionBorder"
    )
    val badgeColor = when (visual) {
        OptionVisual.SELECTED -> NuvionColors.Blue
        OptionVisual.CORRECT -> NuvionColors.Success
        OptionVisual.WRONG -> NuvionColors.Error
        else -> NuvionColors.BlueTint
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(NuvionShape.Button)
            .background(bg)
            .border(1.5.dp, borderColor, NuvionShape.Button)
            .selectable(selected = selected, enabled = !checked, role = Role.RadioButton, onClick = onClick)
            .heightIn(min = 60.dp)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(badgeColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                letter,
                style = MaterialTheme.typography.labelMedium,
                color = if (visual == OptionVisual.DEFAULT || visual == OptionVisual.DIMMED) NuvionColors.Blue else Color.White
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            option.text.text(),
            style = MaterialTheme.typography.bodyLarge,
            color = if (visual == OptionVisual.DIMMED) NuvionColors.TextSecondary else NuvionColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        when (visual) {
            OptionVisual.CORRECT -> Icon(Icons.Filled.CheckCircle, stringResource(R.string.quiz_correct), tint = NuvionColors.Success)
            OptionVisual.WRONG -> Icon(Icons.Filled.Cancel, stringResource(R.string.quiz_wrong), tint = NuvionColors.Error)
            else -> Unit
        }
    }
}

@Composable
private fun FeedbackCard(correct: Boolean, explanation: String) {
    NuvionCard(Modifier.fillMaxWidth(), color = if (correct) NuvionColors.SuccessTint else NuvionColors.ErrorTint, elevation = 0.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (correct) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null,
                tint = if (correct) NuvionColors.Success else NuvionColors.Error
            )
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(if (correct) R.string.quiz_correct else R.string.quiz_wrong),
                style = MaterialTheme.typography.titleMedium,
                color = if (correct) NuvionColors.Success else NuvionColors.Error
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.Lightbulb, contentDescription = stringResource(R.string.quiz_explanation), tint = NuvionColors.YellowDark, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(explanation, style = MaterialTheme.typography.bodyMedium, color = NuvionColors.TextPrimary)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun QuizScreenPreview() {
    NuvionTheme {
        val quiz = DemoQuizzes.all.first()
        QuizScreen(
            state = UiState.Success(
                QuizScreenData(DemoTopics.all.first(), quiz, QuizSession(selectedOptionId = quiz.questions[0].options[1].id, checked = true), false)
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {}, onRetry = {}, onSelect = {}, onCheck = {}, onNext = {}
        )
    }
}
