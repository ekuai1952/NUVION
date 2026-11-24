package kz.nuvion.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The mock-up uses Inter. The system sans-serif (Roboto) is metrically close; to use Inter itself,
 * put inter_regular.ttf, inter_medium.ttf, inter_semibold.ttf, inter_bold.ttf into res/font and
 * replace [NuvionFontFamily] with FontFamily(Font(R.font.inter_regular), ...).
 */
val NuvionFontFamily: FontFamily = FontFamily.SansSerif

private fun style(size: Int, line: Int, weight: FontWeight, spacing: Double = 0.0) = TextStyle(
    fontFamily = NuvionFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = spacing.sp
)

val NuvionTypography = Typography(
    displayLarge = style(44, 52, FontWeight.Medium, 2.0),
    displayMedium = style(36, 44, FontWeight.Medium, 1.0),
    headlineLarge = style(28, 36, FontWeight.Bold),
    headlineMedium = style(24, 32, FontWeight.Bold),
    headlineSmall = style(20, 28, FontWeight.SemiBold),
    titleLarge = style(20, 28, FontWeight.SemiBold),
    titleMedium = style(17, 24, FontWeight.SemiBold),
    titleSmall = style(15, 22, FontWeight.Medium),
    bodyLarge = style(16, 24, FontWeight.Normal),
    bodyMedium = style(14, 21, FontWeight.Normal),
    bodySmall = style(12, 17, FontWeight.Normal),
    labelLarge = style(16, 22, FontWeight.SemiBold),
    labelMedium = style(13, 18, FontWeight.Medium),
    labelSmall = style(11, 15, FontWeight.Medium, 0.4)
)
