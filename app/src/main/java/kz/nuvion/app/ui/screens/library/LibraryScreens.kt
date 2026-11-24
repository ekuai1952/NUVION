package kz.nuvion.app.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.nuvion.app.R
import kz.nuvion.app.data.demo.DemoLibrary
import kz.nuvion.app.domain.model.LibraryMaterial
import kz.nuvion.app.ui.components.AtomIllustration
import kz.nuvion.app.ui.components.ErrorState
import kz.nuvion.app.ui.components.GradientHeader
import kz.nuvion.app.ui.components.IconBadge
import kz.nuvion.app.ui.components.LoadingState
import kz.nuvion.app.ui.components.NuvionCard
import kz.nuvion.app.ui.components.SectionLabel
import kz.nuvion.app.ui.components.color
import kz.nuvion.app.ui.components.imageVector
import kz.nuvion.app.ui.text
import kz.nuvion.app.ui.theme.LocalDimens
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.ui.theme.SystemBarIcons
import kz.nuvion.app.util.UiState
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.LibraryDetailViewModel
import kz.nuvion.app.viewmodel.LibraryViewModel

@Composable
fun LibraryRoute(
    onBack: () -> Unit,
    onOpenMaterial: (String) -> Unit,
    viewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LibraryScreen(state, onBack, onOpenMaterial, viewModel::retry)
}

@Composable
fun LibraryScreen(
    state: UiState<List<LibraryMaterial>>,
    onBack: () -> Unit,
    onOpenMaterial: (String) -> Unit,
    onRetry: () -> Unit
) {
    SystemBarIcons(darkIcons = false)
    val dimens = LocalDimens.current
    Column(
        Modifier
            .fillMaxSize()
            .background(NuvionColors.Background)
    ) {
        GradientHeader(
            title = stringResource(R.string.library_title),
            subtitle = stringResource(R.string.library_subtitle),
            onBack = onBack
        )
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            val columns = if (maxWidth >= 600.dp) 3 else 2
            when (state) {
                UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(state.kind, onRetry, onBack = onBack)
                is UiState.Success -> LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier
                        .widthIn(max = dimens.maxContentWidth)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = dimens.screenPadding,
                        end = dimens.screenPadding,
                        top = dimens.spaceL,
                        bottom = dimens.spaceL
                    ),
                    horizontalArrangement = Arrangement.spacedBy(dimens.spaceL),
                    verticalArrangement = Arrangement.spacedBy(dimens.spaceL)
                ) {
                    items(state.data, key = { it.id }) { material ->
                        MaterialCard(material) { onOpenMaterial(material.id) }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = stringResource(R.string.library_materials_available, state.data.count { it.available }),
                            style = MaterialTheme.typography.bodyMedium,
                            color = NuvionColors.TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(vertical = dimens.spaceS)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MaterialCard(material: LibraryMaterial, onClick: () -> Unit) {
    val color = material.icon.color()
    NuvionCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp),
        onClick = if (material.available) onClick else null,
        color = if (material.available) NuvionColors.Surface else NuvionColors.Background,
        elevation = if (material.available) 8.dp else 0.dp
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            IconBadge(material.icon.imageVector(), color, size = 60.dp)
            Spacer(Modifier.height(12.dp))
            Text(
                material.title.text(),
                style = MaterialTheme.typography.titleMedium,
                color = if (material.available) NuvionColors.TextPrimary else NuvionColors.TextDisabled,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun LibraryDetailRoute(
    onBack: () -> Unit,
    viewModel: LibraryDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LibraryDetailScreen(state, onBack, viewModel::retry)
}

@Composable
fun LibraryDetailScreen(
    state: UiState<LibraryMaterial>,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    SystemBarIcons(darkIcons = false)
    val dimens = LocalDimens.current
    val material = (state as? UiState.Success)?.data
    Column(
        Modifier
            .fillMaxSize()
            .background(NuvionColors.Background)
    ) {
        GradientHeader(
            title = material?.title?.text() ?: stringResource(R.string.library_title),
            subtitle = stringResource(R.string.library_title),
            onBack = onBack
        )
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            when (state) {
                UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(state.kind, onRetry, onBack = onBack)
                is UiState.Success -> Column(
                    Modifier
                        .widthIn(max = dimens.maxContentWidth)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(horizontal = dimens.screenPadding, vertical = dimens.spaceL),
                    verticalArrangement = Arrangement.spacedBy(dimens.spaceL)
                ) {
                    val m = state.data
                    NuvionCard(Modifier.fillMaxWidth()) {
                        AtomIllustration(
                            type = m.model,
                            contentDescription = m.title.text(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.5f)
                        )
                    }
                    NuvionCard(Modifier.fillMaxWidth()) {
                        SectionLabel(stringResource(R.string.library_definition))
                        Spacer(Modifier.height(8.dp))
                        Text(m.definition.text(), style = MaterialTheme.typography.bodyLarge, color = NuvionColors.TextPrimary)
                    }
                    if (m.characteristics.isNotEmpty()) {
                        NuvionCard(Modifier.fillMaxWidth()) {
                            SectionLabel(stringResource(R.string.library_characteristics))
                            Spacer(Modifier.height(8.dp))
                            m.characteristics.forEachIndexed { index, item ->
                                if (index > 0) HorizontalDivider(color = NuvionColors.Outline)
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        item.label.text(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NuvionColors.TextSecondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        item.value.text(),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = NuvionColors.TextPrimary,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    NuvionCard(Modifier.fillMaxWidth(), color = NuvionColors.YellowTint, elevation = 0.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = NuvionColors.YellowDark)
                            Spacer(Modifier.width(8.dp))
                            SectionLabel(stringResource(R.string.library_fact), color = NuvionColors.YellowDark)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(m.fact.text(), style = MaterialTheme.typography.bodyLarge, color = NuvionColors.TextPrimary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun LibraryScreenPreview() {
    NuvionTheme { LibraryScreen(UiState.Success(DemoLibrary.all), {}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun LibraryDetailPreview() {
    NuvionTheme { LibraryDetailScreen(UiState.Success(DemoLibrary.all.first()), {}, {}) }
}
