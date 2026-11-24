package kz.nuvion.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.nuvion.app.R
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionGradients
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme

/**
 * Blue (or navy) gradient header with rounded bottom corners, decorative circles,
 * a back button and optional trailing content (e.g. an XP badge). Draws under the status bar.
 */
@Composable
fun GradientHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    brush: Brush = NuvionGradients.BlueHeader,
    trailing: @Composable RowScope.() -> Unit = {},
    bottomContent: @Composable ColumnScope.() -> Unit = {}
) {
    val dimens = LocalDimens.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(NuvionShape.Header)
            .background(brush)
            .drawBehind {
                val stroke = Stroke(width = 1.5.dp.toPx())
                drawCircle(Color.White.copy(alpha = 0.10f), radius = size.height * 0.55f, center = Offset(size.width * 0.92f, size.height * 0.1f))
                drawCircle(Color.White.copy(alpha = 0.18f), radius = size.height * 0.35f, center = Offset(size.width * 0.92f, size.height * 0.1f), style = stroke)
                drawCircle(Color.White.copy(alpha = 0.12f), radius = 28.dp.toPx(), center = Offset(size.width * 0.08f, size.height * 0.95f), style = stroke)
                drawCircle(Color(0xFFF2C94C).copy(alpha = 0.8f), radius = 3.dp.toPx(), center = Offset(size.width * 0.75f, size.height * 0.75f))
            }
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = dimens.screenPadding - 8.dp, end = dimens.screenPadding, top = 8.dp, bottom = dimens.spaceXl)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(Modifier.width(8.dp))
                }
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), content = trailing)
            }
            Spacer(Modifier.height(dimens.spaceM))
            Column(Modifier.padding(start = 8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
                bottomContent()
            }
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun GradientHeaderPreview() {
    NuvionTheme {
        GradientHeader(
            title = "Тақырыптар",
            subtitle = "Таңдаңыз",
            onBack = {},
            trailing = { XpBadge(text = "+50 XP") }
        )
    }
}

@Preview(widthDp = 360)
@Composable
private fun NavyHeaderPreview() {
    NuvionTheme {
        GradientHeader(title = "Тест", subtitle = "Атом модельдері", onBack = {}, brush = NuvionGradients.NavyHeader, modifier = Modifier.size(360.dp, 180.dp))
    }
}
