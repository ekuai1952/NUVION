package kz.nuvion.app.ui.screens.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.nuvion.app.R
import kz.nuvion.app.domain.usecase.Achievement
import kz.nuvion.app.domain.usecase.AchievementState
import kz.nuvion.app.domain.usecase.ProfileStats
import kz.nuvion.app.ui.Localized
import kz.nuvion.app.ui.components.ErrorState
import kz.nuvion.app.ui.components.GradientHeader
import kz.nuvion.app.ui.components.LoadingState
import kz.nuvion.app.ui.components.NuvionButton
import kz.nuvion.app.ui.components.NuvionButtonStyle
import kz.nuvion.app.ui.components.NuvionCard
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.ui.theme.SystemBarIcons
import kz.nuvion.app.util.UiState
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.ProfileData
import kz.nuvion.app.viewmodel.ProfileViewModel
import java.time.LocalDate
import java.time.format.TextStyle as DateTextStyle

@Composable
fun ProfileRoute(
    onBack: () -> Unit,
    onContinueLearning: (topicId: String) -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::retry,
        onRename = viewModel::setUserName,
        onContinueLearning = onContinueLearning
    )
}

/** How far the profile card overlaps the blue header, as in the mock-up. */
private val CardOverlap = 56.dp

@Composable
fun ProfileScreen(
    state: UiState<ProfileData>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onRename: (String) -> Unit,
    onContinueLearning: (String) -> Unit
) {
    SystemBarIcons(darkIcons = false)
    val dimens = LocalDimens.current
    var editingName by rememberSaveable { mutableStateOf(false) }

    when (state) {
        UiState.Loading -> LoadingState(Modifier.background(NuvionColors.Background))
        is UiState.Error -> ErrorState(state.kind, onRetry, Modifier.background(NuvionColors.Background), onBack = onBack)
        is UiState.Success -> {
            val data = state.data
            val displayName = data.userName.ifBlank { stringResource(R.string.profile_default_name) }
            Column(
                Modifier
                    .fillMaxSize()
                    .background(NuvionColors.Background)
                    .verticalScroll(rememberScrollState())
            ) {
                GradientHeader(
                    title = stringResource(R.string.profile_title),
                    onBack = onBack,
                    bottomContent = { Spacer(Modifier.height(CardOverlap)) }
                )
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Column(
                        Modifier
                            .widthIn(max = dimens.maxContentWidth)
                            .fillMaxWidth()
                            .padding(horizontal = dimens.screenPadding)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(dimens.spaceL)
                    ) {
                        ProfileCard(
                            name = displayName,
                            stats = data.stats,
                            onEditName = { editingName = true },
                            // Pull the card up over the header without leaving a gap below it.
                            modifier = Modifier.overlapUp(CardOverlap)
                        )
                        WeeklyProgressCard(data.stats)
                        SectionTitle(stringResource(R.string.profile_achievements))
                        AchievementsGrid(data.stats.achievements)
                        StatisticsCard(data.stats)
                        data.continueTopicId?.let { topicId ->
                            NuvionButton(
                                text = stringResource(R.string.profile_continue_learning),
                                onClick = { onContinueLearning(topicId) },
                                modifier = Modifier.fillMaxWidth(),
                                style = NuvionButtonStyle.Secondary,
                                icon = Icons.AutoMirrored.Filled.ArrowForward
                            )
                        }
                        Spacer(Modifier.height(dimens.spaceL))
                    }
                }
            }
            if (editingName) {
                EditNameDialog(
                    initial = data.userName,
                    onSave = {
                        onRename(it)
                        editingName = false
                    },
                    onDismiss = { editingName = false }
                )
            }
        }
    }
}

/** Shifts content up by [amount] and shrinks its reported height by the same amount. */
private fun Modifier.overlapUp(amount: androidx.compose.ui.unit.Dp) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val shift = amount.roundToPx()
    layout(placeable.width, (placeable.height - shift).coerceAtLeast(0)) {
        placeable.place(0, -shift)
    }
}

@Composable
private fun ProfileCard(
    name: String,
    stats: ProfileStats,
    onEditName: () -> Unit,
    modifier: Modifier = Modifier
) {
    NuvionCard(modifier.fillMaxWidth(), elevation = 12.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(NuvionColors.Blue, NuvionColors.Yellow))),
                contentAlignment = Alignment.Center
            ) {
                Text(initials(name), style = MaterialTheme.typography.headlineSmall, color = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleLarge,
                    color = NuvionColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.MilitaryTech, contentDescription = null, tint = NuvionColors.Blue, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.profile_level, stats.level), style = MaterialTheme.typography.labelMedium, color = NuvionColors.Blue)
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Filled.Bolt, contentDescription = null, tint = NuvionColors.YellowDark, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(stringResource(R.string.xp_value, stats.totalXp), style = MaterialTheme.typography.labelMedium, color = NuvionColors.YellowDark)
                }
            }
            IconButton(onClick = onEditName) {
                Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.profile_edit_name), tint = NuvionColors.TextSecondary)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row {
            Text(
                stringResource(R.string.profile_next_level),
                style = MaterialTheme.typography.bodySmall,
                color = NuvionColors.TextSecondary,
                modifier = Modifier.weight(1f)
            )
            Text(stringResource(R.string.xp_value, stats.xpToNextLevel), style = MaterialTheme.typography.bodySmall, color = NuvionColors.TextSecondary)
        }
        Spacer(Modifier.height(8.dp))
        GradientProgressBar(stats.levelFraction)
    }
}

@Composable
private fun GradientProgressBar(progress: Float) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), label = "levelProgress")
    Box(
        Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(NuvionShape.Chip)
            .background(NuvionColors.BlueTint)
    ) {
        Box(
            Modifier
                .fillMaxWidth(animated)
                .fillMaxHeight()
                .clip(NuvionShape.Chip)
                .background(Brush.horizontalGradient(listOf(NuvionColors.Blue, NuvionColors.Yellow)))
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = NuvionColors.TextPrimary,
        modifier = Modifier.semantics { heading() }
    )
}

@Composable
private fun WeeklyProgressCard(stats: ProfileStats) {
    val locale = LocalConfiguration.current.locales[0]
    val maxXp = (stats.weekXp.maxOrNull() ?: 0).coerceAtLeast(1)
    val chartHeight = 120.dp
    NuvionCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = NuvionColors.Blue)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.profile_progress), style = MaterialTheme.typography.titleMedium, color = NuvionColors.TextPrimary)
            Spacer(Modifier.weight(1f))
            Text(
                stringResource(R.string.profile_week_total, stats.weekXp.sum()),
                style = MaterialTheme.typography.labelMedium,
                color = NuvionColors.YellowDark
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            stats.weekXp.forEachIndexed { index, xp ->
                val date: LocalDate = stats.weekStart.plusDays(index.toLong())
                val dayLabel = date.dayOfWeek.getDisplayName(DateTextStyle.SHORT, locale)
                val isToday = index == stats.todayIndex
                val description = stringResource(R.string.profile_day_xp_cd, dayLabel, xp)
                Column(
                    Modifier
                        .weight(1f)
                        .clearAndSetSemantics { contentDescription = description },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (xp > 0) "$xp" else "",
                        style = MaterialTheme.typography.labelSmall,
                        color = NuvionColors.TextSecondary,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(Modifier.height(chartHeight), contentAlignment = Alignment.BottomCenter) {
                        val fraction = if (xp == 0) 0f else (xp.toFloat() / maxXp).coerceIn(0.06f, 1f)
                        Box(
                            Modifier
                                .width(18.dp)
                                .fillMaxHeight(fraction.coerceAtLeast(0.03f))
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 3.dp, bottomEnd = 3.dp))
                                .background(
                                    when {
                                        xp == 0 -> NuvionColors.BlueTint
                                        isToday -> NuvionColors.Yellow
                                        else -> NuvionColors.Blue
                                    }
                                )
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        dayLabel.replaceFirstChar { it.titlecase(locale) },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) NuvionColors.Blue else NuvionColors.TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }
        if (stats.weekXp.all { it == 0 }) {
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.profile_week_empty),
                style = MaterialTheme.typography.bodySmall,
                color = NuvionColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private data class AchievementStyle(val icon: ImageVector, val color: Color, val title: Int, val description: Int)

private fun Achievement.style(): AchievementStyle = when (this) {
    Achievement.ATOM_EXPLORER -> AchievementStyle(Icons.Filled.Star, NuvionColors.Yellow, R.string.ach_atom_explorer, R.string.ach_atom_explorer_desc)
    Achievement.QUEST_MASTER -> AchievementStyle(Icons.Filled.EmojiEvents, NuvionColors.Blue, R.string.ach_quest_master, R.string.ach_quest_master_desc)
    Achievement.PROBLEM_SOLVER -> AchievementStyle(Icons.Filled.Edit, NuvionColors.Success, R.string.ach_problem_solver, R.string.ach_problem_solver_desc)
    Achievement.PERFECT_SCORE -> AchievementStyle(Icons.Filled.CheckCircle, NuvionColors.Isotope, R.string.ach_perfect_score, R.string.ach_perfect_score_desc)
    Achievement.MISSION_PILOT -> AchievementStyle(Icons.Filled.Flag, NuvionColors.Error, R.string.ach_mission_pilot, R.string.ach_mission_pilot_desc)
    Achievement.NUCLEAR_MASTER -> AchievementStyle(Icons.Filled.Whatshot, NuvionColors.NavyIndigo, R.string.ach_nuclear_master, R.string.ach_nuclear_master_desc)
}

@Composable
private fun AchievementsGrid(achievements: List<AchievementState>) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns = if (maxWidth >= 480.dp) 6 else 3
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            achievements.chunked(columns).forEach { row ->
                // IntrinsicSize.Min + fillMaxHeight keeps all tiles in a row equally tall.
                Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { AchievementTile(it, Modifier.weight(1f).fillMaxHeight()) }
                    repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun AchievementTile(state: AchievementState, modifier: Modifier = Modifier) {
    val style = state.achievement.style()
    val title = stringResource(style.title)
    val condition = stringResource(style.description)
    val status = stringResource(if (state.unlocked) R.string.ach_unlocked else R.string.ach_locked)
    Column(
        modifier
            .clip(NuvionShape.Card)
            .background(if (state.unlocked) style.color.copy(alpha = 0.12f) else NuvionColors.Outline.copy(alpha = 0.5f))
            .heightIn(min = 120.dp)
            .padding(horizontal = 8.dp, vertical = 14.dp)
            .clearAndSetSemantics { contentDescription = "$title — $status. $condition" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (state.unlocked) style.color else NuvionColors.Disabled),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (state.unlocked) style.icon else Icons.Filled.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = if (state.unlocked) NuvionColors.TextPrimary else NuvionColors.TextDisabled,
            textAlign = TextAlign.Center
        )
        if (!state.unlocked) {
            Spacer(Modifier.height(2.dp))
            Text(
                condition,
                style = MaterialTheme.typography.labelSmall,
                color = NuvionColors.TextDisabled,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatisticsCard(stats: ProfileStats) {
    NuvionCard(Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.profile_statistics),
            style = MaterialTheme.typography.titleMedium,
            color = NuvionColors.TextPrimary,
            modifier = Modifier.semantics { heading() }
        )
        Spacer(Modifier.height(8.dp))
        StatRow(
            stringResource(R.string.profile_stat_topics),
            stringResource(R.string.profile_stat_topics_value, stats.topicsCompleted, stats.topicsTotal),
            NuvionColors.Blue
        )
        StatRow(
            stringResource(R.string.profile_stat_accuracy),
            stats.accuracyPercent?.let { stringResource(R.string.percent_value, it) } ?: "—",
            NuvionColors.Success
        )
        StatRow(
            stringResource(R.string.profile_stat_streak),
            pluralStringResource(R.plurals.profile_streak_days, stats.streakDays, stats.streakDays) +
                if (stats.streakDays > 0) " 🔥" else "",
            NuvionColors.YellowDark
        )
    }
}

@Composable
private fun StatRow(label: String, value: String, color: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = NuvionColors.TextPrimary, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.titleSmall, color = color)
    }
}

@Composable
private fun EditNameDialog(initial: String, onSave: (String) -> Unit, onDismiss: () -> Unit) {
    var text by rememberSaveable { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Localized { Text(stringResource(R.string.profile_edit_name)) } },
        text = {
            Localized {
                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.length <= 40) text = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.profile_name_label)) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                    shape = NuvionShape.Field
                )
            }
        },
        confirmButton = {
            Localized {
                TextButton(onClick = { onSave(text) }, enabled = text.isNotBlank()) {
                    Text(stringResource(R.string.profile_save))
                }
            }
        },
        dismissButton = {
            Localized { TextButton(onClick = onDismiss) { Text(stringResource(R.string.profile_cancel)) } }
        },
        containerColor = NuvionColors.Surface
    )
}

/** "Айдос Кенес" -> "АК"; a single word gives one letter. */
internal fun initials(name: String): String =
    name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

@Preview(showBackground = true, widthDp = 360, heightDp = 1400)
@Composable
private fun ProfilePreview() {
    NuvionTheme {
        val stats = ProfileStats(
            totalXp = 2450, level = 13, xpInLevel = 50, xpPerLevel = 200,
            weekXp = listOf(80, 0, 150, 30, 240, 0, 0), weekStart = LocalDate.of(2026, 11, 23), todayIndex = 4,
            streakDays = 3, accuracyPercent = 92, topicsCompleted = 3, topicsTotal = 5,
            achievements = Achievement.entries.mapIndexed { i, a -> AchievementState(a, i < 2) }
        )
        ProfileScreen(UiState.Success(ProfileData("Айдос Кенес", stats, "bohr_postulates")), {}, {}, {}, {})
    }
}
