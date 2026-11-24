package kz.nuvion.app.ui.screens.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.nuvion.app.R
import kz.nuvion.app.data.demo.DemoLessons
import kz.nuvion.app.data.demo.DemoTopics
import kz.nuvion.app.domain.model.AtomModelType
import kz.nuvion.app.domain.model.LessonBlock
import kz.nuvion.app.ui.components.AtomIllustration
import kz.nuvion.app.ui.components.ErrorState
import kz.nuvion.app.ui.components.GradientHeader
import kz.nuvion.app.ui.components.LoadingState
import kz.nuvion.app.ui.components.NuvionButton
import kz.nuvion.app.ui.components.NuvionButtonStyle
import kz.nuvion.app.ui.components.NuvionCard
import kz.nuvion.app.ui.components.SectionLabel
import kz.nuvion.app.ui.components.XpBadge
import kz.nuvion.app.ui.text
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.ui.theme.SystemBarIcons
import kz.nuvion.app.util.UiState
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.LessonContent
import kz.nuvion.app.viewmodel.LessonViewModel

@Composable
fun LessonRoute(
    onBack: () -> Unit,
    onOpenAr: (AtomModelType) -> Unit,
    onOpenQuiz: (String) -> Unit,
    viewModel: LessonViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val earnedXp by viewModel.earnedXp.collectAsStateWithLifecycle()
    val isCompleting by viewModel.isCompleting.collectAsStateWithLifecycle()
    LessonScreen(
        state = state,
        earnedXp = earnedXp,
        isCompleting = isCompleting,
        onBack = onBack,
        onRetry = viewModel::retry,
        onComplete = viewModel::completeLesson,
        onOpenAr = onOpenAr,
        onOpenQuiz = { onOpenQuiz(viewModel.topicId) }
    )
}

/** Theory blocks grouped for display: text goes into one card, formulas/illustrations get their own. */
private sealed interface LessonSection {
    data class Theory(val blocks: List<LessonBlock>) : LessonSection
    data class Formula(val block: LessonBlock.Formula) : LessonSection
    data class Illustration(val block: LessonBlock.Illustration) : LessonSection
}

private fun List<LessonBlock>.toSections(): List<LessonSection> {
    val result = mutableListOf<LessonSection>()
    val buffer = mutableListOf<LessonBlock>()
    fun flush() {
        if (buffer.isNotEmpty()) {
            result += LessonSection.Theory(buffer.toList())
            buffer.clear()
        }
    }
    forEach { block ->
        when (block) {
            is LessonBlock.Formula -> { flush(); result += LessonSection.Formula(block) }
            is LessonBlock.Illustration -> { flush(); result += LessonSection.Illustration(block) }
            else -> buffer += block
        }
    }
    flush()
    return result
}

@Composable
fun LessonScreen(
    state: UiState<LessonContent>,
    earnedXp: Int?,
    isCompleting: Boolean,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onComplete: () -> Unit,
    onOpenAr: (AtomModelType) -> Unit,
    onOpenQuiz: () -> Unit
) {
    SystemBarIcons(darkIcons = false)
    val dimens = LocalDimens.current
    val listState = rememberLazyListState()
    Column(
        Modifier
            .fillMaxSize()
            .background(NuvionColors.Background)
    ) {
        val content = (state as? UiState.Success)?.data
        GradientHeader(
            title = content?.topic?.title?.text() ?: "",
            subtitle = content?.lesson?.title?.text(),
            onBack = onBack,
            trailing = {
                if (content != null) XpBadge(stringResource(R.string.xp_reward, content.lesson.xpReward))
            }
        )
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            when (state) {
                UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(state.kind, onRetry, onBack = onBack)
                is UiState.Success -> {
                    val sections = state.data.lesson.blocks.toSections()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .widthIn(max = dimens.maxContentWidth)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = dimens.screenPadding, vertical = dimens.spaceL),
                        verticalArrangement = Arrangement.spacedBy(dimens.spaceL)
                    ) {
                        itemsIndexed(sections) { _, section ->
                            when (section) {
                                is LessonSection.Theory -> TheoryCard(section.blocks)
                                is LessonSection.Formula -> FormulaCard(section.block.formula.text(), section.block.caption?.text())
                                is LessonSection.Illustration -> IllustrationCard(section.block.model, section.block.caption.text())
                            }
                        }
                        item {
                            ModelCard(
                                model = state.data.lesson.modelType,
                                onClick = { onOpenAr(state.data.lesson.modelType) }
                            )
                        }
                        item {
                            CompletionCard(
                                completed = state.data.completed,
                                xpReward = state.data.lesson.xpReward,
                                earnedXp = earnedXp,
                                isCompleting = isCompleting,
                                onComplete = onComplete,
                                onOpenQuiz = onOpenQuiz
                            )
                        }
                    }
                }
            }
        }
        if (content != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .shadow(12.dp)
                    .background(NuvionColors.Surface)
                    .navigationBarsPadding()
                    .padding(horizontal = dimens.screenPadding, vertical = dimens.spaceM),
                contentAlignment = Alignment.Center
            ) {
                NuvionButton(
                    text = stringResource(R.string.lesson_ar_view),
                    onClick = { onOpenAr(content.lesson.modelType) },
                    modifier = Modifier
                        .widthIn(max = dimens.maxContentWidth)
                        .fillMaxWidth(),
                    icon = Icons.Outlined.ViewInAr
                )
            }
        }
    }
}

@Composable
private fun TheoryCard(blocks: List<LessonBlock>) {
    NuvionCard(Modifier.fillMaxWidth()) {
        blocks.forEachIndexed { index, block ->
            if (index > 0) Spacer(Modifier.height(10.dp))
            when (block) {
                is LessonBlock.Heading -> Text(
                    text = block.text.text(),
                    style = MaterialTheme.typography.titleLarge,
                    color = NuvionColors.TextPrimary,
                    modifier = Modifier.semantics { heading() }
                )
                is LessonBlock.Paragraph -> Text(
                    text = block.text.text(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = NuvionColors.TextPrimary.copy(alpha = 0.85f)
                )
                is LessonBlock.NumberedList -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    block.items.forEachIndexed { i, item -> NumberedItem(i + 1, item.text()) }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun NumberedItem(number: Int, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(NuvionColors.Blue),
            contentAlignment = Alignment.Center
        ) {
            Text("$number", style = MaterialTheme.typography.labelMedium, color = NuvionColors.Surface)
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = NuvionColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FormulaCard(formula: String, caption: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(NuvionShape.Card)
            .background(NuvionColors.BlueTint)
            .border(1.dp, NuvionColors.BlueTintStrong, NuvionShape.Card)
            .padding(LocalDimens.current.cardPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionLabel(stringResource(R.string.practice_formula))
        Spacer(Modifier.height(8.dp))
        Text(
            text = formula,
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium),
            color = NuvionColors.Navy,
            textAlign = TextAlign.Center
        )
        if (!caption.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(caption, style = MaterialTheme.typography.bodySmall, color = NuvionColors.TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun IllustrationCard(model: AtomModelType, caption: String) {
    NuvionCard(Modifier.fillMaxWidth()) {
        AtomIllustration(
            type = model,
            contentDescription = caption,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.4f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            caption,
            style = MaterialTheme.typography.bodySmall,
            color = NuvionColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ModelCard(model: AtomModelType, onClick: () -> Unit) {
    NuvionCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        color = NuvionColors.Navy,
        onClickLabel = stringResource(R.string.lesson_ar_view)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(72.dp)
                    .clip(NuvionShape.IconBadge)
                    .background(NuvionColors.NavyIndigo)
            ) {
                AtomIllustration(model, Modifier.fillMaxSize().padding(6.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.lesson_3d_model), style = MaterialTheme.typography.titleMedium, color = NuvionColors.Surface)
                Spacer(Modifier.height(2.dp))
                Text(
                    stringResource(R.string.lesson_3d_model_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvionColors.Surface.copy(alpha = 0.75f)
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = NuvionColors.Yellow)
        }
    }
}

@Composable
private fun CompletionCard(
    completed: Boolean,
    xpReward: Int,
    earnedXp: Int?,
    isCompleting: Boolean,
    onComplete: () -> Unit,
    onOpenQuiz: () -> Unit
) {
    NuvionCard(Modifier.fillMaxWidth(), color = if (completed) NuvionColors.SuccessTint else NuvionColors.YellowTint) {
        if (completed) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = NuvionColors.Success, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.lesson_completed), style = MaterialTheme.typography.titleMedium, color = NuvionColors.TextPrimary)
                    if (earnedXp != null && earnedXp > 0) {
                        Text(stringResource(R.string.lesson_xp_earned, earnedXp), style = MaterialTheme.typography.bodyMedium, color = NuvionColors.Success)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            NuvionButton(stringResource(R.string.lesson_go_test), onOpenQuiz, Modifier.fillMaxWidth(), style = NuvionButtonStyle.Navy)
        } else {
            Text(stringResource(R.string.lesson_complete_hint), style = MaterialTheme.typography.bodyMedium, color = NuvionColors.TextPrimary)
            Spacer(Modifier.height(12.dp))
            NuvionButton(
                text = stringResource(R.string.lesson_complete, xpReward),
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth(),
                style = NuvionButtonStyle.Secondary,
                loading = isCompleting,
                icon = Icons.Filled.CheckCircle
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun LessonScreenPreview() {
    NuvionTheme {
        LessonScreen(
            state = UiState.Success(LessonContent(DemoTopics.all[1], DemoLessons.all[1], completed = false)),
            earnedXp = null, isCompleting = false,
            onBack = {}, onRetry = {}, onComplete = {}, onOpenAr = {}, onOpenQuiz = {}
        )
    }
}
