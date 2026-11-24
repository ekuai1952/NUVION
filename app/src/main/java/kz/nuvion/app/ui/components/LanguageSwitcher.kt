package kz.nuvion.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.nuvion.app.R
import kz.nuvion.app.domain.model.AppLanguage
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme

/** KK / RU / EN segmented switch from the top of the home screen. */
@Composable
fun LanguageSwitcher(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val description = stringResource(R.string.cd_language_switcher)
    Row(
        modifier = modifier
            .shadow(6.dp, NuvionShape.Chip, ambientColor = NuvionColors.Blue.copy(alpha = 0.2f), spotColor = NuvionColors.Blue.copy(alpha = 0.2f))
            .clip(NuvionShape.Chip)
            .background(Color.White)
            .padding(4.dp)
            .semantics { contentDescription = description }
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        AppLanguage.entries.forEach { language ->
            val isSelected = language == selected
            val bg by animateColorAsState(if (isSelected) NuvionColors.Blue else Color.Transparent, label = "langBg")
            val fg by animateColorAsState(if (isSelected) Color.White else NuvionColors.TextSecondary, label = "langFg")
            Box(
                modifier = Modifier
                    .clip(NuvionShape.Chip)
                    .background(bg)
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(language) })
                    .sizeIn(minWidth = 44.dp, minHeight = 36.dp)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(language.label, style = MaterialTheme.typography.labelMedium, color = fg)
            }
        }
    }
}

@Preview
@Composable
private fun LanguageSwitcherPreview() {
    NuvionTheme { LanguageSwitcher(AppLanguage.KK, {}) }
}
