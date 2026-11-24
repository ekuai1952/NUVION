package kz.nuvion.app.ui.screens.arlab

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.nuvion.app.R
import kz.nuvion.app.domain.model.ArModel
import kz.nuvion.app.ui.components.GradientHeader
import kz.nuvion.app.ui.components.NuvionCard
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionGradients
import kz.nuvion.app.ui.theme.NuvionShape
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.ui.theme.SystemBarIcons
import kz.nuvion.app.util.ArSupport
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.ArLabUiState
import kz.nuvion.app.viewmodel.ArLabViewModel

@Composable
fun ArLabRoute(
    onBack: () -> Unit,
    viewModel: ArLabViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ArLabScreen(state, onBack, viewModel::selectModel, viewModel::setAutoRotate)
}

@Composable
fun ArModel.label(): String = stringResource(
    when (this) {
        ArModel.BOHR_ATOM -> R.string.ar_model_bohr
        ArModel.PROTON -> R.string.ar_model_proton
        ArModel.NEUTRON -> R.string.ar_model_neutron
        ArModel.NUCLEAR_REACTION -> R.string.ar_model_reaction
    }
)

/**
 * AR lab. The 3D viewer below works on every device. When ARCore is supported and a GLB file
 * is bundled, the status card says so; placing the model in the camera view is the integration
 * point described in app/src/main/assets/models/README.md (SceneView/ARCore).
 */
@Composable
fun ArLabScreen(
    state: ArLabUiState,
    onBack: () -> Unit,
    onSelectModel: (ArModel) -> Unit,
    onAutoRotateChange: (Boolean) -> Unit
) {
    SystemBarIcons(darkIcons = false)
    val dimens = LocalDimens.current
    Column(
        Modifier
            .fillMaxSize()
            .background(NuvionColors.Background)
    ) {
        GradientHeader(
            title = stringResource(R.string.ar_title),
            subtitle = stringResource(R.string.ar_subtitle),
            onBack = onBack,
            brush = NuvionGradients.NavyHeader
        )
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                Modifier
                    .widthIn(max = dimens.maxContentWidth)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = dimens.screenPadding, vertical = dimens.spaceL),
                verticalArrangement = Arrangement.spacedBy(dimens.spaceL)
            ) {
                ArStatusCard(state)

                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.spaceS)
                ) {
                    ArModel.entries.forEach { model ->
                        ModelChip(model.label(), model == state.selectedModel) { onSelectModel(model) }
                    }
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.95f)
                        .clip(NuvionShape.Card)
                        .background(Brush.verticalGradient(listOf(NuvionColors.Surface, NuvionColors.BlueTint)))
                ) {
                    Atom3DViewer(
                        model = state.selectedModel,
                        autoRotate = state.autoRotate,
                        contentDescription = state.selectedModel.label(),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                NuvionCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.TouchApp, contentDescription = null, tint = NuvionColors.Blue)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            stringResource(R.string.ar_hint_gestures),
                            style = MaterialTheme.typography.bodyMedium,
                            color = NuvionColors.TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.ar_auto_rotate),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NuvionColors.TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = state.autoRotate,
                            onCheckedChange = onAutoRotateChange,
                            colors = SwitchDefaults.colors(checkedTrackColor = NuvionColors.Blue)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArStatusCard(state: ArLabUiState) {
    val (color, text) = when (state.arSupport) {
        ArSupport.CHECKING -> NuvionColors.BlueTint to stringResource(R.string.ar_checking)
        ArSupport.UNSUPPORTED -> NuvionColors.YellowTint to stringResource(R.string.ar_unavailable)
        ArSupport.SUPPORTED -> NuvionColors.SuccessTint to stringResource(
            if (state.selectedHasGlb) R.string.ar_supported_glb else R.string.ar_supported_no_glb,
            state.selectedModel.glbFile
        )
    }
    NuvionCard(Modifier.fillMaxWidth(), color = color, elevation = 0.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            when (state.arSupport) {
                ArSupport.CHECKING -> CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = NuvionColors.Blue)
                ArSupport.UNSUPPORTED -> Icon(Icons.Outlined.Info, contentDescription = null, tint = NuvionColors.YellowDark)
                ArSupport.SUPPORTED -> Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = NuvionColors.Success)
            }
            Spacer(Modifier.width(10.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, color = NuvionColors.TextPrimary)
        }
    }
}

@Composable
private fun ModelChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(NuvionShape.Chip)
            .background(if (selected) NuvionColors.Blue else NuvionColors.Surface)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .heightIn(min = 40.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) NuvionColors.Surface else NuvionColors.TextPrimary
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 860)
@Composable
private fun ArLabPreview() {
    NuvionTheme {
        ArLabScreen(ArLabUiState(arSupport = ArSupport.UNSUPPORTED), {}, {}, {})
    }
}
