package com.laohei.jetpack_composables.nested_scroll

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.fastForEachIndexed
import com.laohei.jetpack_composables.R
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CustomNestedScroll() {
    val tabs = remember { List(3) { "Tab $it" } }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val minOffset = 65.dp
    val maxOffset = 180.dp
    val minOffsetPx = with(density) { minOffset.toPx() }
    val maxOffsetPx = with(density) { maxOffset.toPx() }
    var rawAppBarAlpha by remember { mutableFloatStateOf(1f) }
    var rawAppBarOffset by remember { mutableIntStateOf(0) }
    val appBarAlpha by animateFloatAsState(targetValue = rawAppBarAlpha, label = "alpha")
    val appBarOffset by animateIntAsState(targetValue = rawAppBarOffset, label = "offset")
    var currentAppBarOffset by remember { mutableFloatStateOf(maxOffsetPx) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y == 0f) {
                    return available
                }
                if (source == NestedScrollSource.UserInput) {
                    val diffH = when {
                        available.y < 0 -> minOffsetPx - currentAppBarOffset
                        else -> maxOffsetPx - currentAppBarOffset
                    }
                    rawAppBarAlpha = when {
                        available.y < 0 -> abs(diffH) / (maxOffsetPx - minOffsetPx)
                        else -> 1f - abs(diffH) / (maxOffsetPx - minOffsetPx)
                    }
                    rawAppBarOffset = with(density) {
                        lerp(-minOffset, 0.dp, rawAppBarAlpha).toPx().toInt()
                    }
                    currentAppBarOffset += when {
                        (available.y < 0 && available.y > diffH) ||
                                (available.y > 0 && available.y < diffH) -> available.y

                        else -> diffH
                    }
                }
                return Offset.Zero
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.offset { IntOffset(0, appBarOffset) }
            ) {
                TopAppBar(
                    modifier = Modifier.graphicsLayer { alpha = appBarAlpha },
                    title = {
                        Image(
                            painter = painterResource(R.drawable.logo_light),
                            contentDescription = "logo",
                            modifier = Modifier.height(40.dp)
                        )
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = Icons.Default.Settings.name,
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = Icons.Default.Notifications.name,
                            )
                        }
                    }
                )
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex
                ) {
                    tabs.fastForEachIndexed { index, tab ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = { selectedTabIndex = index }
                        ) {
                            Text(text = tab, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding() + with(density) { appBarOffset.toDp() }
            )
        ) {
            items(30) {
                ListItem(
                    headlineContent = {
                        Text("Item $it")
                    }
                )
            }
        }
    }
}