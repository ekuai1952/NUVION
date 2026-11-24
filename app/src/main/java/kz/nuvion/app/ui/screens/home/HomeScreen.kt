package kz.nuvion.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.nuvion.app.R
import kz.nuvion.app.domain.model.AppLanguage
import kz.nuvion.app.ui.Localized
import kz.nuvion.app.ui.components.CenteredContent
import kz.nuvion.app.ui.components.HeroAtom
import kz.nuvion.app.ui.components.LanguageSwitcher
import kz.nuvion.app.ui.components.NuvionButton
import kz.nuvion.app.ui.components.NuvionButtonStyle
import kz.nuvion.app.ui.components.ProfessorProton
import kz.nuvion.app.ui.components.XpBadge
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.ui.theme.SystemBarIcons
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.HomeUiState
import kz.nuvion.app.viewmodel.HomeViewModel

@Composable
fun HomeRoute(
    onStart: () -> Unit,
    onContinue: () -> Unit,
    onTopics: () -> Unit,
    onArLab: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onLanguageSelect = viewModel::setLanguage,
        onStart = onStart,
        onContinue = onContinue,
        onTopics = onTopics,
        onArLab = onArLab,
        onDismissWelcome = viewModel::dismissWelcome,
        onHapticsChange = viewModel::setHaptics,
        onResetProgress = viewModel::resetProgress
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onLanguageSelect: (AppLanguage) -> Unit,
    onStart: () -> Unit,
    onContinue: () -> Unit,
    onTopics: () -> Unit,
    onArLab: () -> Unit,
    onDismissWelcome: () -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onResetProgress: () -> Unit
) {
    SystemBarIcons(darkIcons = true)
    val dimens = LocalDimens.current
    var showBubble by rememberSaveable { mutableStateOf(false) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    val compactHeight = LocalConfiguration.current.screenHeightDp < 640

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NuvionColors.Background)
            .drawBehind {
                // Thin decorative circles and dots from the mock-up.
                val stroke = Stroke(1.2.dp.toPx())
                drawCircle(NuvionColors.Blue.copy(alpha = 0.18f), radius = 34.dp.toPx(), center = Offset(size.width * 0.14f, size.height * 0.13f), style = stroke)
                drawCircle(NuvionColors.Yellow.copy(alpha = 0.45f), radius = 24.dp.toPx(), center = Offset(size.width * 0.82f, size.height * 0.2f), style = stroke)
                drawCircle(NuvionColors.Blue.copy(alpha = 0.6f), radius = 3.5.dp.toPx(), center = Offset(size.width * 0.9f, size.height * 0.15f))
                drawCircle(NuvionColors.Yellow.copy(alpha = 0.7f), radius = 3.dp.toPx(), center = Offset(size.width * 0.1f, size.height * 0.62f))
                drawCircle(NuvionColors.Blue.copy(alpha = 0.08f), radius = size.width * 0.5f, center = Offset(size.width * 1.05f, size.height * 0.98f))
            }
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
        val viewportHeight = maxHeight
        CenteredContent {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    // Fill the screen when content is short (so buttons sit at the bottom),
                    // scroll when it is taller (small screens, large font).
                    .heightIn(min = viewportHeight)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = dimens.screenPadding, vertical = dimens.spaceL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    if (state.totalXp > 0) XpBadge(stringResource(R.string.xp_value, state.totalXp))
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.settings_title), tint = NuvionColors.TextSecondary)
                    }
                    LanguageSwitcher(selected = state.language, onSelect = onLanguageSelect)
                }

                Spacer(Modifier.height(if (compactHeight) dimens.spaceL else dimens.spaceXxl))
                Logo()
                Spacer(Modifier.height(dimens.spaceS))
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.titleSmall,
                    color = NuvionColors.TextSecondary
                )
                Spacer(Modifier.height(dimens.spaceXl))

                Box(contentAlignment = Alignment.BottomCenter) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        HeroAtom(size = if (compactHeight) dimens.heroAtom * 0.8f else dimens.heroAtom)
                        Spacer(Modifier.height(20.dp))
                    }
                    ProfessorProton(
                        showBubble = showBubble,
                        onClick = { showBubble = !showBubble },
                        message = stringResource(R.string.home_professor_greeting)
                    )
                }

                Spacer(Modifier.height(dimens.spaceXl))
                Spacer(Modifier.weight(1f))

                Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceM)) {
                    NuvionButton(
                        text = stringResource(R.string.home_start),
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Filled.PlayArrow
                    )
                    NuvionButton(
                        text = stringResource(R.string.home_continue),
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth(),
                        style = NuvionButtonStyle.Secondary,
                        enabled = !state.isLoading,
                        icon = Icons.AutoMirrored.Filled.ArrowForward
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(dimens.spaceM)) {
                        NuvionButton(
                            text = stringResource(R.string.home_topics),
                            onClick = onTopics,
                            modifier = Modifier.weight(1f),
                            style = NuvionButtonStyle.Outline,
                            icon = Icons.Outlined.Apps
                        )
                        NuvionButton(
                            text = stringResource(R.string.home_ar_lab),
                            onClick = onArLab,
                            modifier = Modifier.weight(1f),
                            style = NuvionButtonStyle.Outline,
                            icon = Icons.Outlined.ViewInAr
                        )
                    }
                }
            }
        }
        }
    }

    if (state.showWelcome && !state.isLoading) {
        AlertDialog(
            onDismissRequest = onDismissWelcome,
            title = { Localized { Text(stringResource(R.string.welcome_title)) } },
            text = { Localized { Text(stringResource(R.string.welcome_text)) } },
            confirmButton = {
                Localized {
                    TextButton(onClick = onDismissWelcome) { Text(stringResource(R.string.welcome_ok)) }
                }
            },
            containerColor = NuvionColors.Surface
        )
    }

    if (showSettings) {
        SettingsDialog(
            hapticsEnabled = state.hapticsEnabled,
            onHapticsChange = onHapticsChange,
            onReset = {
                onResetProgress()
                showSettings = false
            },
            onDismiss = { showSettings = false }
        )
    }
}

@Composable
private fun Logo() {
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = NuvionColors.Navy)) { append("NU") }
        withStyle(SpanStyle(color = NuvionColors.Blue, fontWeight = FontWeight.SemiBold)) { append("VI") }
        withStyle(SpanStyle(color = NuvionColors.Navy)) { append("ON") }
    }
    Text(
        text = text,
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.semantics { heading() }
    )
}

@Composable
private fun SettingsDialog(
    hapticsEnabled: Boolean,
    onHapticsChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmReset by rememberSaveable { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Localized { Text(stringResource(R.string.settings_title)) } },
        text = { Localized {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.settings_haptics),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = onHapticsChange,
                        colors = SwitchDefaults.colors(checkedTrackColor = NuvionColors.Blue)
                    )
                }
                if (confirmReset) {
                    Text(stringResource(R.string.settings_reset_confirm), color = NuvionColors.Error, style = MaterialTheme.typography.bodyMedium)
                }
                TextButton(onClick = { if (confirmReset) onReset() else confirmReset = true }) {
                    Text(
                        stringResource(if (confirmReset) R.string.settings_reset_yes else R.string.settings_reset),
                        color = NuvionColors.Error
                    )
                }
            }
        } },
        confirmButton = {
            Localized { TextButton(onClick = onDismiss) { Text(stringResource(R.string.settings_close)) } }
        },
        containerColor = NuvionColors.Surface
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun HomeScreenPreview() {
    NuvionTheme {
        HomeScreen(
            state = HomeUiState(isLoading = false, totalXp = 120, continueTopicId = "atom_models"),
            onLanguageSelect = {}, onStart = {}, onContinue = {}, onTopics = {}, onArLab = {},
            onDismissWelcome = {}, onHapticsChange = {}, onResetProgress = {}
        )
    }
}
