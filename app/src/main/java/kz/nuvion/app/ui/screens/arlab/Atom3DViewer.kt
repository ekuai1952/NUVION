package kz.nuvion.app.ui.screens.arlab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import kz.nuvion.app.domain.model.ArModel
import kz.nuvion.app.ui.components.drawSphere
import kz.nuvion.app.ui.theme.NuvionColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Minimal 3D vector for the software renderer. */
private data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
    operator fun times(k: Float) = Vec3(x * k, y * k, z * k)
}

private data class Sphere(val pos: Vec3, val radius: Float, val color: Color, val label: String? = null, val alpha: Float = 1f)

private class Scene(val spheres: List<Sphere>, val rings: List<List<Vec3>>, val bonds: List<Pair<Vec3, Vec3>> = emptyList())

/**
 * Interactive 3D model used when AR is unavailable (and as a preview when it is):
 * drag to rotate, pinch to zoom, double-tap to reset. Rendered with a small perspective
 * projection on a Canvas, so no 3D engine or GLB file is required.
 */
@Composable
fun Atom3DViewer(
    model: ArModel,
    autoRotate: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var yaw by rememberSaveable { mutableFloatStateOf(0.6f) }
    var pitch by rememberSaveable { mutableFloatStateOf(-0.35f) }
    var zoom by rememberSaveable { mutableFloatStateOf(1f) }
    var timeNanos by remember { mutableLongStateOf(0L) }

    LaunchedEffect(autoRotate) {
        var last = withFrameNanos { it }
        while (true) {
            withFrameNanos { now ->
                val dt = (now - last) / 1_000_000_000f
                last = now
                timeNanos += (dt * 1_000_000_000L).toLong()
                if (autoRotate) yaw += dt * 0.5f
            }
        }
    }

    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, gestureZoom, _ ->
                    yaw += pan.x * 0.01f
                    pitch = (pitch + pan.y * 0.01f).coerceIn(-1.4f, 1.4f)
                    zoom = (zoom * gestureZoom).coerceIn(0.5f, 2.5f)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = {
                    yaw = 0.6f
                    pitch = -0.35f
                    zoom = 1f
                })
            }
    ) {
        val t = timeNanos / 1_000_000_000f
        val scene = buildScene(model, t)
        renderScene(scene, yaw, pitch, zoom, textMeasurer)
    }
}

// ---------- scene construction ----------

private fun ring(radius: Float, tiltX: Float, tiltZ: Float, segments: Int = 72): List<Vec3> =
    (0..segments).map { i -> orbitPoint(radius, tiltX, tiltZ, (2 * PI * i / segments).toFloat()) }

private fun orbitPoint(radius: Float, tiltX: Float, tiltZ: Float, angle: Float): Vec3 {
    // Circle in XZ plane, tilted around X then Z.
    val x = radius * cos(angle)
    val z = radius * sin(angle)
    val y1 = -z * sin(tiltX)
    val z1 = z * cos(tiltX)
    return Vec3(x * cos(tiltZ) - y1 * sin(tiltZ), x * sin(tiltZ) + y1 * cos(tiltZ), z1)
}

private fun nucleusCluster(count: Int, radius: Float, center: Vec3 = Vec3(0f, 0f, 0f), nucleon: Float = 0.13f): List<Sphere> {
    val golden = (PI * (3 - sqrt(5.0))).toFloat()
    return (0 until count).map { i ->
        val y = if (count == 1) 0f else 1 - (i / (count - 1f)) * 2
        val r = sqrt(1 - y * y)
        val theta = golden * i
        val pos = Vec3(cos(theta) * r, y, sin(theta) * r) * (radius * 0.8f) + center
        Sphere(pos, nucleon, if (i % 2 == 0) NuvionColors.Proton else NuvionColors.Neutron)
    }
}

private fun buildScene(model: ArModel, t: Float): Scene = when (model) {
    ArModel.BOHR_ATOM -> {
        val orbits = listOf(Triple(0.55f, 0.3f, 0f), Triple(0.9f, -0.5f, 0.6f), Triple(1.25f, 1.1f, -0.4f))
        val electrons = orbits.mapIndexed { i, (r, tx, tz) ->
            Sphere(orbitPoint(r, tx, tz, t * (1.6f - i * 0.35f) + i * 2f), 0.07f, NuvionColors.Electron)
        }
        Scene(
            spheres = nucleusCluster(6, 0.2f, nucleon = 0.11f) + electrons,
            rings = orbits.map { (r, tx, tz) -> ring(r, tx, tz) }
        )
    }
    ArModel.PROTON, ArModel.NEUTRON -> {
        val labels = if (model == ArModel.PROTON) listOf("u", "u", "d") else listOf("u", "d", "d")
        val colors = listOf(Color(0xFFEB5757), Color(0xFF27AE60), Color(0xFF2F80ED))
        val quarks = (0 until 3).map { i ->
            val a = t * 0.8f + i * 2f * PI.toFloat() / 3f
            val pos = Vec3(0.5f * cos(a), 0.18f * sin(t * 1.3f + i), 0.5f * sin(a))
            Sphere(pos, 0.2f, colors[i], labels[i])
        }
        val shellColor = if (model == ArModel.PROTON) NuvionColors.Proton else NuvionColors.Neutron
        Scene(
            spheres = quarks + Sphere(Vec3(0f, 0f, 0f), 1.0f, shellColor, alpha = 0.16f),
            rings = listOf(ring(1.0f, 0f, 0f), ring(1.0f, PI.toFloat() / 2, 0f)),
            bonds = listOf(quarks[0].pos to quarks[1].pos, quarks[1].pos to quarks[2].pos, quarks[2].pos to quarks[0].pos)
        )
    }
    ArModel.NUCLEAR_REACTION -> {
        val cycle = 4f
        val k = (t % cycle) / cycle
        if (k < 0.4f) {
            val neutronX = -1.45f + (k / 0.4f) * 1.05f
            Scene(
                spheres = nucleusCluster(22, 0.45f) + Sphere(Vec3(neutronX, 0f, 0f), 0.11f, NuvionColors.Neutron),
                rings = emptyList()
            )
        } else {
            val s = (k - 0.4f) / 0.6f
            val sep = 0.25f + s * 0.6f
            val neutrons = listOf(Vec3(0.2f, 1f, 0.3f), Vec3(-0.4f, -0.9f, 0.5f), Vec3(0.1f, 0.2f, -1f)).map { dir ->
                Sphere(dir * (0.3f + s * 0.95f), 0.11f, NuvionColors.Neutron)
            }
            val flash = Sphere(Vec3(0f, 0f, 0f), 0.4f + s * 0.9f, NuvionColors.Yellow, alpha = (1f - s) * 0.35f)
            Scene(
                spheres = nucleusCluster(12, 0.36f, Vec3(-sep, 0.15f * sep, 0f)) +
                    nucleusCluster(10, 0.32f, Vec3(sep, -0.15f * sep, 0f)) + neutrons + flash,
                rings = emptyList()
            )
        }
    }
}

// ---------- rendering ----------

private fun DrawScope.renderScene(scene: Scene, yaw: Float, pitch: Float, zoom: Float, textMeasurer: TextMeasurer) {
    val unit = size.minDimension * 0.32f * zoom
    val cy = cos(yaw)
    val sy = sin(yaw)
    val cosP = cos(pitch)
    val sinP = sin(pitch)
    val focal = 4f

    fun project(v: Vec3): Triple<Offset, Float, Float> {
        val x1 = v.x * cy + v.z * sy
        val z1 = -v.x * sy + v.z * cy
        val y2 = v.y * cosP - z1 * sinP
        val z2 = v.y * sinP + z1 * cosP
        val s = focal / (focal + z2)
        return Triple(Offset(center.x + x1 * s * unit, center.y + y2 * s * unit), s, z2)
    }

    // Floor shadow / grid for depth cue
    drawOval(
        Color.Black.copy(alpha = 0.06f),
        topLeft = Offset(center.x - unit * 1.2f, center.y + unit * 1.35f),
        size = androidx.compose.ui.geometry.Size(unit * 2.4f, unit * 0.35f)
    )

    scene.rings.forEach { points ->
        val path = Path()
        points.forEachIndexed { i, p ->
            val (o, _, _) = project(p)
            if (i == 0) path.moveTo(o.x, o.y) else path.lineTo(o.x, o.y)
        }
        drawPath(path, NuvionColors.Blue.copy(alpha = 0.55f), style = Stroke(1.6f * density))
    }

    scene.bonds.forEach { (a, b) ->
        drawLine(
            NuvionColors.Yellow,
            project(a).first,
            project(b).first,
            strokeWidth = 2f * density,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
        )
    }

    // Painter's algorithm: far spheres first.
    scene.spheres
        .map { sphere -> sphere to project(sphere.pos) }
        .sortedByDescending { (_, proj) -> proj.third }
        .forEach { (sphere, proj) ->
            val (offset, scale, _) = proj
            val r = sphere.radius * scale * unit
            if (sphere.alpha < 1f) {
                drawCircle(sphere.color.copy(alpha = sphere.alpha), radius = r, center = offset)
            } else {
                drawSphere(sphere.color, offset, r)
            }
            sphere.label?.let { label ->
                val layout = textMeasurer.measure(label, TextStyle(color = Color.White, fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold))
                drawText(layout, topLeft = offset - Offset(layout.size.width / 2f, layout.size.height / 2f))
            }
        }
}
