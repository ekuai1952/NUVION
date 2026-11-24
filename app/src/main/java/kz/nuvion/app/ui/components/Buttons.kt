package kz.nuvion.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ViewInAr
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionGradients
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme

enum class NuvionButtonStyle { Primary, Secondary, Outline, Tonal, Navy }

/**
 * Large rounded button from the mock-up. Has pressed (scale + ripple), disabled and loading
 * states; grows in height with the system font size instead of clipping text.
 */
@Composable
fun NuvionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: NuvionButtonStyle = NuvionButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && enabled) 0.97f else 1f, label = "buttonScale")
    val clickable = enabled && !loading

    val background: Brush = when {
        !enabled -> SolidColor(NuvionColors.Disabled)
        style == NuvionButtonStyle.Primary -> NuvionGradients.BlueButton
        style == NuvionButtonStyle.Secondary -> NuvionGradients.YellowButton
        style == NuvionButtonStyle.Navy -> SolidColor(NuvionColors.Navy)
        style == NuvionButtonStyle.Tonal -> SolidColor(NuvionColors.BlueTint)
        else -> SolidColor(NuvionColors.Surface)
    }
    val contentColor: Color = when {
        !enabled -> NuvionColors.TextDisabled
        style == NuvionButtonStyle.Primary || style == NuvionButtonStyle.Navy -> Color.White
        style == NuvionButtonStyle.Secondary -> NuvionColors.Navy
        else -> NuvionColors.Blue
    }
    val elevation = if (enabled && (style == NuvionButtonStyle.Primary || style == NuvionButtonStyle.Secondary)) 8.dp else 0.dp
    val shadowColor = if (style == NuvionButtonStyle.Secondary) NuvionColors.Yellow else NuvionColors.Blue

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(elevation, NuvionShape.Button, ambientColor = shadowColor, spotColor = shadowColor)
            .clip(NuvionShape.Button)
            .background(background)
            .then(
                if (style == NuvionButtonStyle.Outline) {
                    Modifier.border(BorderStroke(1.5.dp, if (enabled) NuvionColors.BlueTintStrong else NuvionColors.Disabled), NuvionShape.Button)
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = clickable,
                role = Role.Button,
                onClick = onClick
            )
            .heightIn(min = 56.dp)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = contentColor, strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
            } else if (icon != null) {
                Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Small pill button used on topic cards ("Тест", "Есептер"). */
@Composable
fun PillButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    yellow: Boolean = false,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "pillScale")
    val bg = if (yellow) NuvionColors.YellowTint else NuvionColors.BlueTint
    val fg = when {
        !enabled -> NuvionColors.TextDisabled
        yellow -> NuvionColors.YellowDark
        else -> NuvionColors.Blue
    }
    Row(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(NuvionShape.Chip)
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .heightIn(min = 40.dp)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = fg)
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ButtonsPreview() {
    NuvionTheme {
        Column(Modifier.padding(16.dp)) {
            NuvionButton("Оқуды бастау", {}, Modifier.fillMaxWidth(), icon = Icons.Filled.PlayArrow)
            Spacer(Modifier.width(8.dp).heightIn(min = 12.dp))
            NuvionButton("Жалғастыру", {}, Modifier.fillMaxWidth(), style = NuvionButtonStyle.Secondary)
            Spacer(Modifier.heightIn(min = 12.dp))
            NuvionButton("AR Зертхана", {}, Modifier.fillMaxWidth(), style = NuvionButtonStyle.Outline, icon = Icons.Outlined.ViewInAr)
            Spacer(Modifier.heightIn(min = 12.dp))
            NuvionButton("Тексеру", {}, Modifier.fillMaxWidth(), enabled = false)
        }
    }
}
