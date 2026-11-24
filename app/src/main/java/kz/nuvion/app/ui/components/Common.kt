package kz.nuvion.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kz.nuvion.app.R
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.util.ErrorKind

/** White rounded card with a soft blue-tinted shadow. Clickable when [onClick] is set. */
@Composable
fun NuvionCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    color: Color = NuvionColors.Surface,
    elevation: Dp = 8.dp,
    contentPadding: Dp = LocalDimens.current.cardPadding,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(elevation, NuvionShape.Card, ambientColor = NuvionColors.Blue.copy(alpha = 0.25f), spotColor = NuvionColors.Navy.copy(alpha = 0.18f))
            .clip(NuvionShape.Card)
            .background(color)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClickLabel = onClickLabel,
                        role = Role.Button,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(contentPadding),
        content = content
    )
}

/** Yellow XP pill, e.g. "+50 XP". */
@Composable
fun XpBadge(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(NuvionShape.Chip)
            .background(NuvionColors.Yellow)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Star, contentDescription = null, tint = NuvionColors.Navy, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = NuvionColors.Navy)
    }
}

/** Rounded linear progress bar; announces its value to TalkBack. */
@Composable
fun NuvionProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = NuvionColors.Yellow,
    trackColor: Color = NuvionColors.BlueTint,
    height: Dp = 8.dp
) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), label = "progress")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(NuvionShape.Chip)
            .background(trackColor)
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo(progress.coerceIn(0f, 1f), 0f..1f) }
    ) {
        Box(
            Modifier
                .fillMaxWidth(animated)
                .height(height)
                .clip(NuvionShape.Chip)
                .background(color)
        )
    }
}

/** Circular indicator on topic cards; turns into a green check when the topic is done. */
@Composable
fun ProgressRing(
    progress: Float,
    completed: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val description = if (completed) {
        stringResource(R.string.topic_completed)
    } else {
        stringResource(R.string.topic_progress_cd, (progress * 100).toInt())
    }
    Box(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        if (completed) {
            Box(
                Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(NuvionColors.Success),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.55f))
            }
        } else {
            Canvas(Modifier.size(size)) {
                val stroke = 4.dp.toPx()
                val inset = stroke / 2
                val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
                drawCircle(NuvionColors.YellowTint, radius = this.size.minDimension / 2 - inset, style = Stroke(stroke))
                drawArc(
                    color = NuvionColors.Yellow,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
            }
            Text(
                text = "${(progress * 100).toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = NuvionColors.TextSecondary
            )
        }
    }
}

/** Centres content and limits its width on tablets / landscape. */
@Composable
fun CenteredContent(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.widthIn(max = LocalDimens.current.maxContentWidth).fillMaxWidth(), content = content)
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    val label = stringResource(R.string.common_loading)
    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = label },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = NuvionColors.Blue)
        Spacer(Modifier.height(12.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = NuvionColors.TextSecondary)
    }
}

@Composable
fun ErrorState(
    kind: ErrorKind,
    onRetry: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (kind == ErrorKind.NOT_FOUND) Icons.Outlined.ErrorOutline else Icons.Outlined.CloudOff,
            contentDescription = null,
            tint = NuvionColors.TextSecondary,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(if (kind == ErrorKind.NOT_FOUND) R.string.common_not_found else R.string.common_error),
            style = MaterialTheme.typography.titleMedium,
            color = NuvionColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        if (onRetry != null && kind == ErrorKind.UNKNOWN) {
            NuvionButton(stringResource(R.string.common_retry), onRetry, Modifier.widthIn(min = 200.dp))
            Spacer(Modifier.height(12.dp))
        }
        if (onBack != null) {
            NuvionButton(stringResource(R.string.cd_back), onBack, Modifier.widthIn(min = 200.dp), style = NuvionButtonStyle.Outline)
        }
    }
}

/** Section label with an icon, used inside cards ("Берілгені", "Формула"...). */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier, color: Color = NuvionColors.Blue) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier.heightIn(min = 16.dp)
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CommonPreview() {
    NuvionTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            NuvionCard(Modifier.fillMaxWidth()) {
                Text("Card", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                NuvionProgressBar(0.4f)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                XpBadge("+50 XP")
                ProgressRing(0.66f, completed = false)
                ProgressRing(1f, completed = true)
            }
        }
    }
}
