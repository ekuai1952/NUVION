package kz.nuvion.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.nuvion.app.domain.model.AtomModelType
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Animated 2D illustration of an atomic/nuclear model, drawn on a Canvas so it needs no
 * image assets and scales to any screen.
 */
@Composable
fun AtomIllustration(
    type: AtomModelType,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    animated: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "atom")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 6000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )
    val t = if (animated) phase else 0.15f
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = modifier.semantics { if (contentDescription != null) this.contentDescription = contentDescription }
    ) {
        when (type) {
            AtomModelType.THOMSON -> drawThomson()
            AtomModelType.RUTHERFORD -> drawRutherford(t)
            AtomModelType.BOHR -> drawBohr(t)
            AtomModelType.PROTON -> drawHadron(t, listOf("u", "u", "d"), NuvionColors.Proton, textMeasurer)
            AtomModelType.NEUTRON -> drawHadron(t, listOf("u", "d", "d"), NuvionColors.Neutron, textMeasurer)
            AtomModelType.NUCLEUS -> drawDecayingNucleus(t)
            AtomModelType.FISSION -> drawFission(t)
            AtomModelType.REACTOR -> drawChainReaction(t)
            AtomModelType.PHOTON -> drawPhoton(t)
            AtomModelType.ISOTOPE -> drawIsotopes(textMeasurer)
        }
    }
}

// ---------- drawing helpers ----------

private val Highlight = Color.White.copy(alpha = 0.85f)

internal fun DrawScope.drawSphere(color: Color, center: Offset, radius: Float, alpha: Float = 1f) {
    if (radius <= 0f) return
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(lerp(color, Color.White, 0.55f), color, lerp(color, Color.Black, 0.25f)),
            center = center + Offset(-radius * 0.35f, -radius * 0.35f),
            radius = radius * 1.6f
        ),
        radius = radius,
        center = center,
        alpha = alpha
    )
}

/** Nucleons arranged on a golden-angle spiral; protons red, neutrons grey. */
internal fun DrawScope.drawNucleus(center: Offset, radius: Float, count: Int, wobble: Float = 0f) {
    val nucleon = radius / sqrt(count.toFloat()) * 1.05f
    val golden = PI * (3 - sqrt(5.0))
    for (i in count - 1 downTo 0) {
        val r = radius * sqrt(i / count.toFloat()) * 0.85f
        val a = i * golden + wobble
        val pos = center + Offset((r * cos(a)).toFloat(), (r * sin(a)).toFloat())
        drawSphere(if (i % 2 == 0) NuvionColors.Proton else NuvionColors.Neutron, pos, nucleon)
    }
}

private fun DrawScope.orbitPoint(center: Offset, rx: Float, ry: Float, tiltDeg: Float, angle: Float): Offset {
    val x = rx * cos(angle)
    val y = ry * sin(angle)
    val tilt = Math.toRadians(tiltDeg.toDouble())
    return center + Offset(
        (x * cos(tilt) - y * sin(tilt)).toFloat(),
        (x * sin(tilt) + y * cos(tilt)).toFloat()
    )
}

private fun DrawScope.drawThomson() {
    val c = center
    val r = size.minDimension * 0.42f
    drawCircle(
        Brush.radialGradient(listOf(Color(0xFFFFD6D6), Color(0xFFF7A1A1)), center = c, radius = r),
        radius = r,
        center = c
    )
    val positions = listOf(
        Offset(-0.45f, -0.3f), Offset(0.1f, -0.55f), Offset(0.5f, -0.15f), Offset(-0.15f, 0.05f),
        Offset(0.35f, 0.4f), Offset(-0.5f, 0.35f), Offset(0.05f, 0.6f)
    )
    positions.forEach { p -> drawSphere(NuvionColors.Electron, c + Offset(p.x * r, p.y * r), r * 0.09f) }
}

private fun DrawScope.drawRutherford(t: Float) {
    val c = center
    val r = size.minDimension * 0.46f
    val tilts = listOf(0f, 60f, -60f)
    tilts.forEach { tilt ->
        rotate(tilt, c) {
            drawOval(
                NuvionColors.Blue.copy(alpha = 0.45f),
                topLeft = Offset(c.x - r, c.y - r * 0.36f),
                size = Size(r * 2, r * 0.72f),
                style = Stroke(2.dp.toPx())
            )
        }
    }
    drawNucleus(c, r * 0.16f, 7)
    tilts.forEachIndexed { i, tilt ->
        val angle = (t * 2 * PI * (1 + i * 0.3) + i * 2.1).toFloat()
        drawSphere(NuvionColors.Electron, orbitPoint(c, r, r * 0.36f, tilt, angle), r * 0.06f)
    }
}

private fun DrawScope.drawBohr(t: Float) {
    val c = center
    val maxR = size.minDimension * 0.47f
    val radii = listOf(0.3f, 0.62f, 1f).map { it * maxR }
    radii.forEachIndexed { i, rr ->
        drawCircle(
            NuvionColors.Blue.copy(alpha = 0.35f + 0.1f * i),
            radius = rr,
            center = c,
            style = Stroke(width = 2.dp.toPx(), pathEffect = if (i == 2) PathEffect.dashPathEffect(floatArrayOf(10f, 8f)) else null)
        )
    }
    drawNucleus(c, maxR * 0.14f, 5)
    radii.forEachIndexed { i, rr ->
        val angle = (t * 2 * PI / (i + 1) * 2 + i * 1.7).toFloat()
        drawSphere(NuvionColors.Electron, c + Offset(rr * cos(angle), rr * sin(angle)), maxR * 0.055f)
    }
    // Photon emitted by a quantum jump (yellow wave).
    val wave = Path()
    val start = c + Offset(radii[1] * 0.75f, -radii[1] * 0.75f)
    val len = maxR * 0.55f
    val steps = 40
    for (s in 0..steps) {
        val k = s / steps.toFloat()
        val p = start + Offset(k * len * 0.7f, -k * len * 0.7f) +
            Offset(sin(k * 6 * PI + t * 12 * PI).toFloat() * 6.dp.toPx() * 0.7f, sin(k * 6 * PI + t * 12 * PI).toFloat() * 6.dp.toPx() * 0.7f)
        if (s == 0) wave.moveTo(p.x, p.y) else wave.lineTo(p.x, p.y)
    }
    drawPath(wave, NuvionColors.Yellow, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round))
}

private fun DrawScope.drawHadron(t: Float, quarks: List<String>, shell: Color, textMeasurer: TextMeasurer) {
    val c = center
    val r = size.minDimension * 0.44f
    drawCircle(
        Brush.radialGradient(listOf(shell.copy(alpha = 0.10f), shell.copy(alpha = 0.35f)), center = c, radius = r),
        radius = r,
        center = c
    )
    drawCircle(shell.copy(alpha = 0.6f), radius = r, center = c, style = Stroke(2.dp.toPx()))
    val colors = listOf(Color(0xFFEB5757), Color(0xFF27AE60), Color(0xFF2F80ED))
    val points = (0 until 3).map { i ->
        val a = (t * 2 * PI + i * 2 * PI / 3).toFloat()
        c + Offset(r * 0.5f * cos(a), r * 0.5f * sin(a))
    }
    // Gluon bonds
    for (i in 0 until 3) {
        drawLine(
            NuvionColors.Yellow,
            points[i],
            points[(i + 1) % 3],
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
        )
    }
    points.forEachIndexed { i, p ->
        drawSphere(colors[i], p, r * 0.2f)
        val label = textMeasurer.measure(quarks[i], TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold))
        drawText(label, topLeft = p - Offset(label.size.width / 2f, label.size.height / 2f))
    }
}

private fun DrawScope.drawDecayingNucleus(t: Float) {
    val c = center - Offset(size.minDimension * 0.12f, 0f)
    val r = size.minDimension * 0.28f
    drawNucleus(c, r, 18, wobble = sin(t * 2 * PI).toFloat() * 0.05f)
    // Alpha particle (2p + 2n) flying away
    val d = r * 1.2f + t * size.minDimension * 0.4f
    val a = c + Offset(d, -d * 0.3f)
    val small = r * 0.14f
    drawSphere(NuvionColors.Proton, a + Offset(-small * 0.7f, -small * 0.7f), small)
    drawSphere(NuvionColors.Neutron, a + Offset(small * 0.7f, -small * 0.7f), small)
    drawSphere(NuvionColors.Neutron, a + Offset(-small * 0.7f, small * 0.7f), small)
    drawSphere(NuvionColors.Proton, a + Offset(small * 0.7f, small * 0.7f), small)
}

private fun DrawScope.drawFission(t: Float) {
    val c = center
    val r = size.minDimension * 0.2f
    val nr = r * 0.16f
    if (t < 0.4f) {
        val k = t / 0.4f
        drawSphere(NuvionColors.Neutron, Offset(c.x - size.width * 0.45f + k * (size.width * 0.45f - r), c.y), nr)
        drawNucleus(c, r, 20)
    } else {
        val k = (t - 0.4f) / 0.6f
        val sep = r * 0.2f + k * size.minDimension * 0.25f
        drawNucleus(c + Offset(-sep, -sep * 0.4f), r * 0.75f, 11)
        drawNucleus(c + Offset(sep, sep * 0.4f), r * 0.68f, 9)
        // Flash
        drawCircle(NuvionColors.Yellow.copy(alpha = (1f - k).coerceIn(0f, 1f) * 0.6f), radius = r * (1 + k * 2), center = c)
        // Three new neutrons
        listOf(-60f, 90f, 200f).forEach { deg ->
            val a = Math.toRadians(deg.toDouble())
            val d = r * 0.5f + k * size.minDimension * 0.42f
            drawSphere(NuvionColors.Neutron, c + Offset((d * cos(a)).toFloat(), (d * sin(a)).toFloat()), nr)
        }
    }
}

private fun DrawScope.drawChainReaction(t: Float) {
    val levels = 3
    val w = size.width
    val h = size.height
    val r = size.minDimension * 0.07f
    val stroke = Stroke(1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
    for (level in 0 until levels) {
        val count = 1 shl level
        val y = h * (0.18f + level * 0.32f)
        for (i in 0 until count) {
            val x = w * (i + 0.5f) / count
            val active = t * levels >= level
            drawNucleus(Offset(x, y), r, 6)
            if (level < levels - 1) {
                listOf(i * 2, i * 2 + 1).forEach { child ->
                    val cx = w * (child + 0.5f) / (count * 2)
                    val cy = h * (0.18f + (level + 1) * 0.32f)
                    val line = Path().apply { moveTo(x, y + r); lineTo(cx, cy - r) }
                    drawPath(line, NuvionColors.Blue.copy(alpha = if (active) 0.6f else 0.2f), style = stroke)
                    if (active) {
                        val k = ((t * levels - level).coerceIn(0f, 1f))
                        drawSphere(NuvionColors.Yellow, Offset(x + (cx - x) * k, y + r + (cy - r - y - r) * k), r * 0.3f)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawPhoton(t: Float) {
    val path = Path()
    val y0 = center.y
    val amp = size.height * 0.2f
    val steps = 80
    for (s in 0..steps) {
        val k = s / steps.toFloat()
        val x = size.width * (0.05f + 0.9f * k)
        val y = y0 + amp * sin(k * 4 * PI - t * 2 * PI).toFloat()
        if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    drawPath(path, NuvionColors.Yellow, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
    val px = size.width * (0.05f + 0.9f * t)
    val py = y0 + amp * sin(t * 4 * PI - t * 2 * PI).toFloat()
    drawCircle(NuvionColors.Yellow.copy(alpha = 0.3f), radius = size.minDimension * 0.1f, center = Offset(px, py))
    drawSphere(NuvionColors.Photon, Offset(px, py), size.minDimension * 0.05f)
}

private fun DrawScope.drawIsotopes(textMeasurer: TextMeasurer) {
    val labels = listOf("¹H", "²H", "³H")
    val r = size.minDimension * 0.14f
    labels.forEachIndexed { i, label ->
        val c = Offset(size.width * (i + 0.5f) / 3f, size.height * 0.42f)
        drawSphere(NuvionColors.Proton, c + Offset(-r * 0.3f * i, 0f), r * 0.45f)
        for (n in 0 until i) {
            drawSphere(NuvionColors.Neutron, c + Offset(r * 0.35f * (n + 1), (if (n % 2 == 0) -1 else 1) * r * 0.25f), r * 0.45f)
        }
        drawCircle(NuvionColors.Blue.copy(alpha = 0.3f), radius = r * 1.6f, center = c, style = Stroke(1.5.dp.toPx()))
        val text = textMeasurer.measure(label, TextStyle(color = NuvionColors.Navy, fontSize = 15.sp, fontWeight = FontWeight.SemiBold))
        drawText(text, topLeft = Offset(c.x - text.size.width / 2f, size.height * 0.82f))
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun AtomIllustrationPreview() {
    NuvionTheme {
        Column(Modifier.padding(16.dp)) {
            AtomIllustration(AtomModelType.BOHR, Modifier.size(200.dp), animated = false)
            AtomIllustration(AtomModelType.FISSION, Modifier.fillMaxWidth().aspectRatio(1.6f), animated = false)
        }
    }
}
