package kz.nuvion.app.ui.screens.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kz.nuvion.app.R
import kz.nuvion.app.data.demo.DemoProblems
import kz.nuvion.app.data.demo.DemoTopics
import kz.nuvion.app.ui.components.ErrorState
import kz.nuvion.app.ui.components.GradientHeader
import kz.nuvion.app.ui.components.LoadingState
import kz.nuvion.app.ui.components.NuvionButton
import kz.nuvion.app.ui.components.NuvionButtonStyle
import kz.nuvion.app.ui.components.NuvionCard
import kz.nuvion.app.ui.components.NuvionProgressBar
import kz.nuvion.app.ui.components.SectionLabel
import kz.nuvion.app.ui.components.XpBadge
import kz.nuvion.app.ui.screens.lesson.FormulaCard
import kz.nuvion.app.ui.HapticOnChange
import kz.nuvion.app.ui.text
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.ui.theme.SystemBarIcons
import kz.nuvion.app.util.AnswerChecker
import kz.nuvion.app.util.UiState
import kz.nuvion.app.viewmodel.AnswerStatus
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.PracticeEvent
import kz.nuvion.app.viewmodel.PracticeScreenData
import kz.nuvion.app.viewmodel.PracticeSession
import kz.nuvion.app.viewmodel.PracticeViewModel

@Composable
fun PracticeRoute(
    onBack: () -> Unit,
    onFinished: (topicId: String) -> Unit,
    viewModel: PracticeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val saveError = stringResource(R.string.common_save_error)
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is PracticeEvent.Finished -> onFinished(event.topicId)
                PracticeEvent.SaveFailed -> snackbarHostState.showSnackbar(saveError)
            }
        }
    }
    PracticeScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        actions = PracticeActions(
            onBack = onBack,
            onRetryLoad = viewModel::retry,
            onInputChange = viewModel::onInputChange,
            onToggleHint = viewModel::toggleHint,
            onCheck = viewModel::check,
            onTryAgain = viewModel::tryAgain,
            onReveal = viewModel::revealSolution,
            onNext = viewModel::next
        )
    )
}

data class PracticeActions(
    val onBack: () -> Unit,
    val onRetryLoad: () -> Unit,
    val onInputChange: (String) -> Unit,
    val onToggleHint: () -> Unit,
    val onCheck: () -> Unit,
    val onTryAgain: () -> Unit,
    val onReveal: () -> Unit,
    val onNext: () -> Unit
)

@Composable
fun PracticeScreen(
    state: UiState<PracticeScreenData>,
    snackbarHostState: SnackbarHostState,
    actions: PracticeActions
) {
    SystemBarIcons(darkIcons = false)
    val dimens = LocalDimens.current
    val data = (state as? UiState.Success)?.data
    Box(
        Modifier
            .fillMaxSize()
            .background(NuvionColors.Background)
    ) {
        Column(Modifier.fillMaxSize().imePadding()) {
            GradientHeader(
                title = stringResource(R.string.practice_title),
                subtitle = data?.topic?.title?.text(),
                onBack = actions.onBack,
                trailing = {
                    if (data != null) XpBadge(stringResource(R.string.xp_reward, data.problem.xpReward))
                },
                bottomContent = {
                    if (data != null) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.practice_counter, data.session.index + 1, data.total),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        NuvionProgressBar(data.progressFraction, trackColor = Color.White.copy(alpha = 0.25f))
                    }
                }
            )
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                when (state) {
                    UiState.Loading -> LoadingState()
                    is UiState.Error -> ErrorState(state.kind, actions.onRetryLoad, onBack = actions.onBack)
                    is UiState.Success -> ProblemContent(state.data, actions)
                }
            }
            if (data != null) BottomBar(data, actions)
        }
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp))
    }
}

@Composable
private fun ProblemContent(data: PracticeScreenData, actions: PracticeActions) {
    val dimens = LocalDimens.current
    val problem = data.problem
    val session = data.session
    val focusManager = LocalFocusManager.current
    val locked = session.status == AnswerStatus.CORRECT || session.status == AnswerStatus.REVEALED
    HapticOnChange(if (session.status == AnswerStatus.NONE) null else "${session.index}-${session.status}")
    val scrollState = rememberScrollState()
    // New problem: start from the top.
    LaunchedEffect(session.index) { scrollState.scrollTo(0) }
    // After checking: hide the keyboard and bring the result banner / solution into view.
    LaunchedEffect(session.status) {
        if (session.status != AnswerStatus.NONE) {
            focusManager.clearFocus()
            delay(250) // let the banner and solution lay out first
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
    Column(
        Modifier
            .widthIn(max = dimens.maxContentWidth)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = dimens.screenPadding, vertical = dimens.spaceL),
        verticalArrangement = Arrangement.spacedBy(dimens.spaceL)
    ) {
        NuvionCard(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel(stringResource(R.string.practice_problem_label), Modifier.weight(1f))
                if (data.solvedBefore) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = NuvionColors.Success, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.practice_solved_badge), style = MaterialTheme.typography.labelSmall, color = NuvionColors.Success)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(problem.statement.text(), style = MaterialTheme.typography.bodyLarge, color = NuvionColors.TextPrimary)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(dimens.spaceM)) {
            InfoBlock(
                title = stringResource(R.string.practice_given),
                lines = problem.given.map { it.text() },
                background = NuvionColors.BlueTint,
                accent = NuvionColors.Blue,
                modifier = Modifier.weight(1f)
            )
            InfoBlock(
                title = stringResource(R.string.practice_find),
                lines = problem.find.map { it.text() },
                background = NuvionColors.YellowTint,
                accent = NuvionColors.YellowDark,
                modifier = Modifier.weight(1f)
            )
        }

        FormulaCard(formula = problem.formula.text(), caption = null)

        NuvionButton(
            text = stringResource(if (session.hintVisible) R.string.practice_hide_hint else R.string.practice_show_hint),
            onClick = actions.onToggleHint,
            modifier = Modifier.fillMaxWidth(),
            style = NuvionButtonStyle.Outline,
            icon = Icons.Outlined.Lightbulb
        )
        AnimatedVisibility(visible = session.hintVisible, enter = fadeIn() + expandVertically()) {
            NuvionCard(Modifier.fillMaxWidth(), color = NuvionColors.YellowTint, elevation = 0.dp) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = NuvionColors.YellowDark)
                    Spacer(Modifier.width(8.dp))
                    Text(problem.hint.text(), style = MaterialTheme.typography.bodyMedium, color = NuvionColors.TextPrimary)
                }
            }
        }

        val unit = problem.unit.text()
        OutlinedTextField(
            value = session.input,
            onValueChange = actions.onInputChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = !locked,
            label = { Text(stringResource(R.string.practice_answer_label)) },
            placeholder = { Text(stringResource(R.string.practice_answer_placeholder)) },
            suffix = if (unit.isNotBlank()) {
                { Text(unit, color = NuvionColors.TextSecondary) }
            } else {
                null
            },
            isError = session.inputError,
            supportingText = if (session.inputError) {
                { Text(stringResource(R.string.practice_invalid_number)) }
            } else {
                null
            },
            singleLine = true,
            shape = NuvionShape.Field,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                actions.onCheck()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NuvionColors.Blue,
                unfocusedBorderColor = NuvionColors.Outline,
                focusedContainerColor = NuvionColors.Surface,
                unfocusedContainerColor = NuvionColors.Surface,
                disabledContainerColor = NuvionColors.Surface
            )
        )

        when (session.status) {
            AnswerStatus.CORRECT -> ResultBanner(true, stringResource(R.string.practice_correct))
            AnswerStatus.WRONG -> ResultBanner(false, stringResource(R.string.practice_wrong))
            AnswerStatus.REVEALED -> ResultBanner(
                false,
                stringResource(R.string.practice_answer_is, AnswerChecker.format(problem.answer), unit)
            )
            AnswerStatus.NONE -> Unit
        }

        AnimatedVisibility(visible = data.showSolution, enter = fadeIn() + expandVertically()) {
            NuvionCard(Modifier.fillMaxWidth()) {
                SectionLabel(stringResource(R.string.practice_solution))
                Spacer(Modifier.height(8.dp))
                problem.solution.forEachIndexed { i, step ->
                    Row(Modifier.padding(vertical = 4.dp)) {
                        Text("${i + 1}.", style = MaterialTheme.typography.bodyLarge, color = NuvionColors.Blue)
                        Spacer(Modifier.width(8.dp))
                        Text(step.text(), style = MaterialTheme.typography.bodyLarge, color = NuvionColors.TextPrimary)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.practice_answer_is, AnswerChecker.format(problem.answer), unit),
                    style = MaterialTheme.typography.titleMedium,
                    color = NuvionColors.Navy
                )
            }
        }
    }
}

@Composable
private fun InfoBlock(
    title: String,
    lines: List<String>,
    background: Color,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .shadow(0.dp, NuvionShape.Card)
            .background(background, NuvionShape.Card)
            .padding(14.dp)
    ) {
        SectionLabel(title, color = accent)
        Spacer(Modifier.height(6.dp))
        lines.forEach { line ->
            Text(line, style = MaterialTheme.typography.bodyLarge, color = NuvionColors.TextPrimary)
        }
    }
}

@Composable
private fun ResultBanner(success: Boolean, text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (success) NuvionColors.SuccessTint else NuvionColors.ErrorTint, NuvionShape.Card)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (success) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            contentDescription = null,
            tint = if (success) NuvionColors.Success else NuvionColors.Error
        )
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = NuvionColors.TextPrimary)
    }
}

@Composable
private fun BottomBar(data: PracticeScreenData, actions: PracticeActions) {
    val dimens = LocalDimens.current
    Box(
        Modifier
            .fillMaxWidth()
            .shadow(12.dp)
            .background(NuvionColors.Surface)
            .navigationBarsPadding()
            .padding(horizontal = dimens.screenPadding, vertical = dimens.spaceM),
        contentAlignment = Alignment.Center
    ) {
        val modifier = Modifier.widthIn(max = dimens.maxContentWidth).fillMaxWidth()
        when (data.session.status) {
            AnswerStatus.NONE -> NuvionButton(
                text = stringResource(R.string.practice_check),
                onClick = actions.onCheck,
                modifier = modifier,
                enabled = data.session.input.isNotBlank(),
                loading = data.isBusy
            )
            AnswerStatus.WRONG -> Row(modifier, horizontalArrangement = Arrangement.spacedBy(dimens.spaceM)) {
                NuvionButton(
                    text = stringResource(R.string.practice_try_again),
                    onClick = actions.onTryAgain,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Replay
                )
                NuvionButton(
                    text = stringResource(R.string.practice_show_solution),
                    onClick = actions.onReveal,
                    modifier = Modifier.weight(1f),
                    style = NuvionButtonStyle.Outline,
                    icon = Icons.Outlined.Visibility
                )
            }
            AnswerStatus.CORRECT, AnswerStatus.REVEALED -> NuvionButton(
                text = stringResource(if (data.isLast) R.string.practice_finish else R.string.practice_next),
                onClick = actions.onNext,
                modifier = modifier,
                style = if (data.isLast) NuvionButtonStyle.Secondary else NuvionButtonStyle.Primary,
                loading = data.isBusy
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun PracticeScreenPreview() {
    NuvionTheme {
        val problems = DemoProblems.all.filter { it.topicId == DemoTopics.all[1].id }
        PracticeScreen(
            state = UiState.Success(
                PracticeScreenData(
                    DemoTopics.all[1], problems,
                    PracticeSession(input = "2.12", hintVisible = true, status = AnswerStatus.CORRECT),
                    solvedBefore = true, isBusy = false
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            actions = PracticeActions({}, {}, {}, {}, {}, {}, {}, {})
        )
    }
}
