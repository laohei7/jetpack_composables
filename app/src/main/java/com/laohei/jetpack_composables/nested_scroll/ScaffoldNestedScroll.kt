package com.laohei.jetpack_composables.nested_scroll

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laohei.jetpack_composables.R

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ScaffoldNestedScroll() {
    val nestedScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
//    val nestedScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
//    val nestedScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val density = LocalDensity.current
    var bannerHeight by remember { mutableStateOf(0.dp) }
    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollBehavior.nestedScrollConnection),
        topBar = {
            Box {
                Image(
                    painter = painterResource(R.drawable.banner),
                    contentDescription = "banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bannerHeight)
                )
                LargeTopAppBar(
                    modifier = Modifier.onSizeChanged {
                        with(density) {
                            bannerHeight = it.height.toDp()
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = Color.Yellow,
                        navigationIconContentColor = Color.Yellow,
                        actionIconContentColor = Color.Yellow
                    ),
                    scrollBehavior = nestedScrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = Icons.AutoMirrored.Default.ArrowBack.name,
                            )
                        }
                    },
                    title = { Text(text = "Title") },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = Icons.Default.Settings.name,
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
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

