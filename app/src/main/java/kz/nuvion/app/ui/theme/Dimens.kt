package kz.nuvion.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Spacing and sizes; a larger set is chosen on wide screens (tablets, landscape). */
@Immutable
data class NuvionDimens(
    val screenPadding: Dp,
    val cardPadding: Dp,
    val heroAtom: Dp,
    val spaceXs: Dp = 4.dp,
    val spaceS: Dp = 8.dp,
    val spaceM: Dp = 12.dp,
    val spaceL: Dp = 16.dp,
    val spaceXl: Dp = 24.dp,
    val spaceXxl: Dp = 32.dp,
    val buttonMinHeight: Dp = 56.dp,
    val iconBadge: Dp = 52.dp,
    val maxContentWidth: Dp = 640.dp
)

val CompactDimens = NuvionDimens(screenPadding = 20.dp, cardPadding = 16.dp, heroAtom = 190.dp)
val ExpandedDimens = NuvionDimens(screenPadding = 32.dp, cardPadding = 20.dp, heroAtom = 240.dp)

val LocalDimens = staticCompositionLocalOf { CompactDimens }
