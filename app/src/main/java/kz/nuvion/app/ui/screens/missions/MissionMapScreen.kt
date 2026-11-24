package kz.nuvion.app.ui.screens.missions

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kz.nuvion.app.R
import kz.nuvion.app.data.demo.DemoTopics
import kz.nuvion.app.domain.model.Mission
import kz.nuvion.app.domain.model.MissionState
import kz.nuvion.app.domain.model.NextStep
import kz.nuvion.app.domain.model.TopicProgress
import kz.nuvion.app.ui.components.ErrorState
import kz.nuvion.app.ui.components.LoadingState
import kz.nuvion.app.ui.components.NuvionButton
import kz.nuvion.app.ui.components.NuvionButtonStyle
import kz.nuvion.app.ui.components.NuvionProgressBar
import kz.nuvion.app.ui.components.XpBadge
import kz.nuvion.app.ui.text
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionGradients
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.ui.theme.SystemBarIcons
import kz.nuvion.app.util.UiState
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.MissionMapData
import kz.nuvion.app.viewmodel.MissionMapViewModel
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun MissionMapRoute(
    onBack: () -> Unit,
    onOpenStep: (topicId: String, step: NextStep) -> Unit,
    viewModel: MissionMapViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MissionMapScreen(state, onBack, onOpenStep, viewModel::retry)
}

@Composable
fun MissionMapScreen(
    state: UiState<MissionMapData>,
    onBack: () -> Unit,
    onOpenStep: (topicId: String, step: NextStep) -> Unit,
    onRetry: () -> Unit
) {
    SystemBarIcons(darkIcons = false)
    val dimens = LocalDimens.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lockedMessage = stringResource(R.string.mission_locked_msg)

    Box(
        Modifier
            .fillMaxSize()
            .background(NuvionGradients.Space)
    ) {
        StarField(Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = dimens.screenPadding - 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back), tint = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    stringResource(R.string.mission_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                (state as? UiState.Success)?.data?.let { XpBadge(stringResource(R.string.xp_value, it.totalXp)) }
            }

            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                when (state) {
                    UiState.Loading -> LoadingState()
                    is UiState.Error -> ErrorState(state.kind, onRetry, onBack = onBack)
                    is UiState.Success -> MissionPath(
                        missions = state.data.missions,
                        onMissionClick = { mission ->
                            when (mission.state) {
                                MissionState.LOCKED -> scope.launch { snackbarHostState.showSnackbar(lockedMessage) }
                                MissionState.COMPLETED -> onOpenStep(mission.topicId, NextStep.LESSON)
                                MissionState.CURRENT -> onOpenStep(
                                    mission.topicId,
                                    state.data.nextStep.takeIf { it != NextStep.DONE } ?: NextStep.LESSON
                                )
                            }
                        }
                    )
                }
            }

            (state as? UiState.Success)?.data?.let { data ->
                ProgressCard(
                    data = data,
                    onContinue = {
                        val current = data.current
                        if (current != null) {
                            onOpenStep(current.topicId, data.nextStep.takeIf { it != NextStep.DONE } ?: NextStep.LESSON)
                        } else {
                            data.missions.lastOrNull()?.let { onOpenStep(it.topicId, NextStep.LESSON) }
                        }
                    }
                )
            }
        }
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter).padding(bottom = 180.dp))
    }
}

/** Twinkling stars and particles on the dark space background. */
@Composable
private fun StarField(modifier: Modifier = Modifier) {
    val stars = remember {
        val random = Random(7)
        List(70) { Triple(random.nextFloat(), random.nextFloat(), random.nextFloat()) }
    }
    val transition = rememberInfiniteTransition(label = "stars")
    val twinkle by transition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse),
        label = "twinkle"
    )
    Canvas(modifier) {
        stars.forEachIndexed { i, (x, y, s) ->
            val alpha = 0.3f + 0.6f * ((sin((twinkle * 6.28f) + i).toFloat() + 1f) / 2f) * s
            drawCircle(Color.White.copy(alpha = alpha), radius = (0.6f + s * 1.6f) * density, center = Offset(x * size.width, y * size.height))
        }
        drawCircle(NuvionColors.Blue.copy(alpha = 0.12f), radius = size.width * 0.5f, center = Offset(size.width * 0.9f, size.height * 0.25f))
        drawCircle(NuvionColors.Isotope.copy(alpha = 0.08f), radius = size.width * 0.4f, center = Offset(0f, size.height * 0.7f))
    }
}

/** Vertical winding route; the first mission is at the bottom, like a journey upwards. */
@Composable
private fun MissionPath(missions: List<Mission>, onMissionClick: (Mission) -> Unit) {
    val dimens = LocalDimens.current
    val nodeSize = 64.dp
    val stepHeight = 150.dp
    val ordered = missions.sortedByDescending { it.order }
    val density = LocalDensity.current
    BoxWithConstraints(
        Modifier
            .widthIn(max = dimens.maxContentWidth)
            .fillMaxSize()
            .verticalScroll(rememberScrollState(Int.MAX_VALUE))
    ) {
        val width = maxWidth
        val totalHeight = stepHeight * ordered.size + 40.dp
        val xFractions = listOf(0.5f, 0.25f, 0.6f, 0.78f, 0.4f, 0.2f)
        fun nodeX(i: Int): Dp = width * xFractions[i % xFractions.size]
        fun nodeY(i: Int): Dp = stepHeight * i + stepHeight / 2

        Box(Modifier.fillMaxWidth().height(totalHeight)) {
            Canvas(Modifier.fillMaxSize()) {
                val path = Path()
                ordered.indices.forEach { i ->
                    val p = with(density) { Offset(nodeX(i).toPx(), nodeY(i).toPx()) }
                    if (i == 0) {
                        path.moveTo(p.x, p.y)
                    } else {
                        val prev = with(density) { Offset(nodeX(i - 1).toPx(), nodeY(i - 1).toPx()) }
                        val midY = (prev.y + p.y) / 2
                        path.cubicTo(prev.x, midY, p.x, midY, p.x, p.y)
                    }
                }
                drawPath(path, Color.White.copy(alpha = 0.08f), style = Stroke(18.dp.toPx(), cap = StrokeCap.Round))
                drawPath(
                    path,
                    Color.White.copy(alpha = 0.45f),
                    style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f)))
                )
            }
            ordered.forEachIndexed { i, mission ->
                MissionNode(
                    mission = mission,
                    size = nodeSize,
                    modifier = Modifier.offset(x = nodeX(i) - nodeSize / 2, y = nodeY(i) - nodeSize / 2),
                    onClick = { onMissionClick(mission) }
                )
            }
        }
    }
}

@Composable
private fun MissionNode(mission: Mission, size: Dp, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val (bg, border) = when (mission.state) {
        MissionState.COMPLETED -> NuvionColors.Success to NuvionColors.SuccessTint
        MissionState.CURRENT -> NuvionColors.Blue to NuvionColors.Yellow
        MissionState.LOCKED -> NuvionColors.Locked to Color.White.copy(alpha = 0.2f)
    }
    val stateText = stringResource(
        when (mission.state) {
            MissionState.COMPLETED -> R.string.mission_state_completed
            MissionState.CURRENT -> R.string.mission_state_current
            MissionState.LOCKED -> R.string.mission_state_locked
        }
    )
    val title = mission.title.text()
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(1f, 1.35f, infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "pulseScale")

    Column(
        modifier = modifier.width(size * 2.2f).offset(x = -(size * 0.6f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (mission.state == MissionState.CURRENT) {
                Box(
                    Modifier
                        .size(size * pulse)
                        .clip(CircleShape)
                        .background(NuvionColors.Blue.copy(alpha = 0.25f))
                )
            }
            Box(
                Modifier
                    .size(size)
                    .shadow(if (mission.state == MissionState.LOCKED) 0.dp else 12.dp, CircleShape, spotColor = bg, ambientColor = bg)
                    .clip(CircleShape)
                    .background(bg)
                    .border(3.dp, border, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        role = Role.Button,
                        onClick = onClick
                    )
                    .semantics { contentDescription = "$title — $stateText" },
                contentAlignment = Alignment.Center
            ) {
                when (mission.state) {
                    MissionState.COMPLETED -> Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    MissionState.CURRENT -> Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    MissionState.LOCKED -> Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(26.dp))
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${mission.order + 1}. $title",
            style = MaterialTheme.typography.labelMedium,
            color = if (mission.state == MissionState.LOCKED) Color.White.copy(alpha = 0.55f) else Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun ProgressCard(data: MissionMapData, onContinue: () -> Unit) {
    val dimens = LocalDimens.current
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .widthIn(max = dimens.maxContentWidth)
                .fillMaxWidth()
                .padding(horizontal = dimens.screenPadding)
                .navigationBarsPadding()
                .padding(bottom = dimens.spaceL)
                .clip(NuvionShape.Card)
                .background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), NuvionShape.Card)
                .padding(dimens.cardPadding)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.mission_progress, data.completedCount, data.missions.size),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    stringResource(R.string.percent_value, (data.fraction * 100).toInt()),
                    style = MaterialTheme.typography.titleSmall,
                    color = NuvionColors.Yellow
                )
            }
            Spacer(Modifier.height(8.dp))
            NuvionProgressBar(data.fraction, trackColor = Color.White.copy(alpha = 0.15f))
            data.current?.let { current ->
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.mission_current, current.title.text()),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
            Spacer(Modifier.height(12.dp))
            NuvionButton(
                text = stringResource(if (data.current == null) R.string.mission_all_done else R.string.mission_continue),
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                style = NuvionButtonStyle.Secondary,
                icon = Icons.AutoMirrored.Filled.ArrowForward
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun MissionMapPreview() {
    NuvionTheme {
        val missions = DemoTopics.all.mapIndexed { i, t ->
            Mission(
                t.id, t.order, t.title,
                when (i) { 0 -> MissionState.COMPLETED; 1 -> MissionState.CURRENT; else -> MissionState.LOCKED },
                TopicProgress(i == 0, i == 0, if (i == 0) 3 else 0, 3)
            )
        }
        MissionMapScreen(UiState.Success(MissionMapData(missions, 280)), {}, { _, _ -> }, {})
    }
}
