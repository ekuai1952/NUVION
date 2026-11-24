package kz.nuvion.app.ui.components

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kz.nuvion.app.R
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme
import kotlin.math.cos
import kotlin.math.sin

/** The decorative atom from the home screen: blue core, thin orbits and orbiting dots. */
@Composable
fun HeroAtom(size: Dp, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "hero")
    val angle by transition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "heroAngle"
    )
    val description = stringResource(R.string.cd_atom_illustration)
    Canvas(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = description }
    ) {
        val c = center
        val outer = this.size.minDimension / 2f * 0.96f
        val middle = outer * 0.78f
        val core = outer * 0.38f
        val thin = Stroke(1.2.dp.toPx())

        drawCircle(NuvionColors.Yellow.copy(alpha = 0.55f), radius = outer, center = c, style = thin)
        drawCircle(NuvionColors.Blue.copy(alpha = 0.35f), radius = middle, center = c, style = thin)
        drawCircle(NuvionColors.BlueTint, radius = core * 1.35f, center = c)

        // Blue core with soft glow
        drawCircle(NuvionColors.Blue.copy(alpha = 0.18f), radius = core * 1.15f, center = c)
        drawCircle(
            Brush.linearGradient(listOf(NuvionColors.BlueLight, NuvionColors.BlueDark), start = c - Offset(core, core), end = c + Offset(core, core)),
            radius = core,
            center = c
        )
        drawAtomSymbol(c, core * 0.62f, Color.White)

        // Orbiting dots
        val rad = Math.toRadians(angle.toDouble())
        drawCircle(NuvionColors.Yellow, radius = 5.dp.toPx(), center = c + Offset((outer * cos(rad - 1.7)).toFloat(), (outer * sin(rad - 1.7)).toFloat()))
        drawCircle(NuvionColors.Blue, radius = 6.dp.toPx(), center = c + Offset((middle * cos(-rad * 1.4 - 0.4)).toFloat(), (middle * sin(-rad * 1.4 - 0.4)).toFloat()))
    }
}

/** White atom pictogram: nucleus + three ellipses. */
internal fun DrawScope.drawAtomSymbol(c: Offset, r: Float, color: Color) {
    val stroke = Stroke(width = r * 0.11f, cap = StrokeCap.Round)
    listOf(0f, 60f, 120f).forEach { deg ->
        rotate(deg, c) {
            drawOval(color, topLeft = Offset(c.x - r, c.y - r * 0.38f), size = Size(r * 2, r * 0.76f), style = stroke)
        }
    }
    drawCircle(color, radius = r * 0.16f, center = c)
}

/**
 * Professor Proton — the app's helper character: a smiling proton with glasses and a name tag.
 * Tapping it toggles a speech bubble with a tip.
 */
@Composable
fun ProfessorProton(
    showBubble: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null
) {
    val name = stringResource(R.string.home_professor)
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (showBubble && message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = NuvionColors.Navy,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .shadow(6.dp, NuvionShape.Card, ambientColor = NuvionColors.Blue.copy(alpha = 0.2f), spotColor = NuvionColors.Blue.copy(alpha = 0.2f))
                    .clip(NuvionShape.Card)
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            )
            Spacer(Modifier.size(8.dp))
        }
        Row(
            modifier = Modifier
                .shadow(6.dp, NuvionShape.Chip, ambientColor = NuvionColors.Yellow, spotColor = NuvionColors.Yellow)
                .clip(NuvionShape.Chip)
                .background(Color.White)
                .border(1.5.dp, NuvionColors.Yellow, NuvionShape.Chip)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current,
                    role = Role.Button,
                    onClickLabel = name,
                    onClick = onClick
                )
                .padding(start = 6.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProtonFace(Modifier.size(34.dp))
            Spacer(Modifier.width(8.dp))
            Text(name, style = MaterialTheme.typography.labelMedium, color = NuvionColors.Navy)
        }
    }
}

@Composable
private fun ProtonFace(modifier: Modifier = Modifier) {
    Box(modifier) {
        Canvas(Modifier.size(34.dp)) {
            val r = size.minDimension / 2
            drawSphere(NuvionColors.Proton, center, r)
            val eyeY = center.y - r * 0.12f
            val eyeDx = r * 0.32f
            val glass = Stroke(r * 0.09f)
            // Glasses
            drawCircle(Color.White, radius = r * 0.22f, center = Offset(center.x - eyeDx, eyeY))
            drawCircle(Color.White, radius = r * 0.22f, center = Offset(center.x + eyeDx, eyeY))
            drawCircle(NuvionColors.Navy, radius = r * 0.22f, center = Offset(center.x - eyeDx, eyeY), style = glass)
            drawCircle(NuvionColors.Navy, radius = r * 0.22f, center = Offset(center.x + eyeDx, eyeY), style = glass)
            drawLine(NuvionColors.Navy, Offset(center.x - eyeDx + r * 0.22f, eyeY), Offset(center.x + eyeDx - r * 0.22f, eyeY), strokeWidth = r * 0.08f)
            drawCircle(NuvionColors.Navy, radius = r * 0.08f, center = Offset(center.x - eyeDx, eyeY))
            drawCircle(NuvionColors.Navy, radius = r * 0.08f, center = Offset(center.x + eyeDx, eyeY))
            // Smile
            drawArc(
                Color.White,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(center.x - r * 0.35f, center.y - r * 0.05f),
                size = Size(r * 0.7f, r * 0.5f),
                style = Stroke(r * 0.1f, cap = StrokeCap.Round)
            )
            // "+" charge
            drawLine(Color.White, Offset(center.x + r * 0.55f, center.y - r * 0.75f), Offset(center.x + r * 0.85f, center.y - r * 0.75f), strokeWidth = r * 0.09f)
            drawLine(Color.White, Offset(center.x + r * 0.7f, center.y - r * 0.9f), Offset(center.x + r * 0.7f, center.y - r * 0.6f), strokeWidth = r * 0.09f)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HeroAtomPreview() {
    NuvionTheme {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            HeroAtom(190.dp)
            ProfessorProton(showBubble = true, onClick = {}, message = "Сәлем!")
        }
    }
}
