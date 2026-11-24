package kz.nuvion.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Palette taken from the NUVION Figma mock-up. */
object NuvionColors {
    val Blue = Color(0xFF2F80ED)
    val BlueDark = Color(0xFF1C5FC4)
    val BlueLight = Color(0xFF5B9DF2)
    val BlueTint = Color(0xFFEAF2FD)
    val BlueTintStrong = Color(0xFFD5E5FB)

    val Navy = Color(0xFF0A1F44)
    val NavyDeep = Color(0xFF0F1B33)
    val NavyIndigo = Color(0xFF1E3A8A)

    val Yellow = Color(0xFFF2C94C)
    val YellowDark = Color(0xFFB98A0F)
    val YellowTint = Color(0xFFFEF7E0)

    val Background = Color(0xFFF7F9FC)
    val Surface = Color(0xFFFFFFFF)
    val Outline = Color(0xFFE3E9F3)

    val TextPrimary = Color(0xFF0A1F44)
    val TextSecondary = Color(0xFF6B7A99)
    val TextDisabled = Color(0xFFA9B4C8)

    val Success = Color(0xFF27AE60)
    val SuccessTint = Color(0xFFE3F6EA)
    val Error = Color(0xFFEB5757)
    val ErrorTint = Color(0xFFFDECEC)

    val Disabled = Color(0xFFD7DEEA)
    val Locked = Color(0xFF8A94A8)

    val SpaceTop = Color(0xFF070D1F)
    val SpaceBottom = Color(0xFF14254F)

    val Proton = Color(0xFFEB5757)
    val Neutron = Color(0xFF8A94A8)
    val Electron = Color(0xFF2F80ED)
    val Photon = Color(0xFFF2C94C)
    val Isotope = Color(0xFF9B51E0)
    val Reactor = Color(0xFF27AE60)
}

object NuvionGradients {
    val BlueHeader: Brush = Brush.linearGradient(listOf(NuvionColors.Blue, NuvionColors.BlueDark))
    val BlueButton: Brush = Brush.horizontalGradient(listOf(NuvionColors.Blue, NuvionColors.BlueLight))
    val NavyHeader: Brush = Brush.linearGradient(
        listOf(NuvionColors.NavyDeep, NuvionColors.NavyIndigo, NuvionColors.NavyDeep)
    )
    val Space: Brush = Brush.verticalGradient(listOf(NuvionColors.SpaceTop, NuvionColors.SpaceBottom))
    val YellowButton: Brush = Brush.horizontalGradient(listOf(NuvionColors.Yellow, Color(0xFFF6D776)))
}
