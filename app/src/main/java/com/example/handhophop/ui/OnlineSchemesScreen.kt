package com.example.handhophop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.handhophop.R
import com.example.handhophop.data.ImageItem

@Composable
fun OnlineSchemesScreen(
    navController: NavHostController,
    selectedVm: SelectedSchemeViewModel
) {
    val bg = colorResource(R.color.bg_beige)
    val vm: OnlineSchemesViewModel = viewModel()
    val state by vm.state.collectAsState()

    Box(Modifier.fillMaxSize().background(bg)) {
        BackgroundPattern()

        Column(Modifier.fillMaxSize()) {
            TopBanner(title = stringResource(R.string.online_title))

            OnlineSchemesMasonry(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                items = state.items,
                isLoading = state.isLoading,
                reachedEnd = state.reachedEnd,
                error = state.error,
                onNeedMore = { vm.loadMore() },
                onClickItem = { item ->
                    selectedVm.select(item.imageUrl)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )

            BottomBar(navController = navController, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun OnlineSchemesMasonry(
    modifier: Modifier,
    items: List<ImageItem>,
    isLoading: Boolean,
    reachedEnd: Boolean,
    error: String?,
    onNeedMore: () -> Unit,
    onClickItem: (ImageItem) -> Unit
) {
    val spacing = dimensionResource(R.dimen.online_grid_item_spacing)
    val padH = dimensionResource(R.dimen.online_grid_padding_h)
    val padV = dimensionResource(R.dimen.online_grid_padding_v)

    val cardColor = colorResource(R.color.card_beige)
    val r = dimensionResource(R.dimen.block_radius)
    val elevation0 = dimensionResource(R.dimen.block_elevation)

    Box(modifier = modifier) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalItemSpacing = spacing,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            contentPadding = PaddingValues(start = padH, end = padH, top = padV, bottom = padV)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClickItem(item) },
                    shape = RoundedCornerShape(r),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = elevation0)
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                SideEffect {
                    if (!isLoading && !reachedEnd && items.isNotEmpty()) onNeedMore()
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when {
                        isLoading -> CircularProgressIndicator(color = colorResource(R.color.text_dark))
                        error != null -> Text(
                            text = error,
                            color = colorResource(R.color.text_dark),
                            style = MaterialTheme.typography.bodySmall
                        )
                        else -> Spacer(Modifier.height(0.dp)) // reachedEnd — ничего не показываем
                    }
                }
            }
        }
    }
}
