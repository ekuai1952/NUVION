package kz.nuvion.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kz.nuvion.app.domain.model.LibraryIcon
import kz.nuvion.app.domain.model.TopicIcon
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionShape

fun TopicIcon.imageVector(): ImageVector = when (this) {
    TopicIcon.ATOM -> Icons.Outlined.Science
    TopicIcon.WAVES -> Icons.Outlined.Waves
    TopicIcon.RADIATION -> Icons.Outlined.Bolt
    TopicIcon.REACTION -> Icons.Outlined.Whatshot
    TopicIcon.REACTOR -> Icons.Outlined.Power
}

fun LibraryIcon.imageVector(): ImageVector = when (this) {
    LibraryIcon.PROTON -> Icons.Outlined.AddCircleOutline
    LibraryIcon.NEUTRON -> Icons.Outlined.RadioButtonChecked
    LibraryIcon.PHOTON -> Icons.Outlined.WbSunny
    LibraryIcon.ISOTOPE -> Icons.Outlined.Layers
    LibraryIcon.REACTOR -> Icons.Outlined.Power
    LibraryIcon.COMING_SOON -> Icons.Outlined.HourglassEmpty
}

fun LibraryIcon.color(): Color = when (this) {
    LibraryIcon.PROTON -> NuvionColors.Proton
    LibraryIcon.NEUTRON -> NuvionColors.Neutron
    LibraryIcon.PHOTON -> NuvionColors.YellowDark
    LibraryIcon.ISOTOPE -> NuvionColors.Isotope
    LibraryIcon.REACTOR -> NuvionColors.Reactor
    LibraryIcon.COMING_SOON -> NuvionColors.TextDisabled
}

/** Rounded square with a tinted background and an icon — used on topic and library cards. */
@Composable
fun IconBadge(
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(NuvionShape.IconBadge)
            .background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}
