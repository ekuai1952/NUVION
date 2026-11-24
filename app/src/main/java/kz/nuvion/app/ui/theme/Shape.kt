package kz.nuvion.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val NuvionShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

object NuvionShape {
    val Button = RoundedCornerShape(18.dp)
    val Card = RoundedCornerShape(24.dp)
    val Chip = RoundedCornerShape(50)
    val Header = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
    val IconBadge = RoundedCornerShape(16.dp)
    val Field = RoundedCornerShape(16.dp)
}
