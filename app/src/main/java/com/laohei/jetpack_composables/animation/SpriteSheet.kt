package com.laohei.jetpack_composables.animation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.laohei.jetpack_composables.R

@Composable
fun SpriteSheet(
    @DrawableRes id: Int,
    frameCount: Int = 24, // 精灵图帧数，使用素材为24帧
    frameWidth: Float = 187f, // 4488/24，图片宽度除以帧数
    frameHeight: Float = 300f, // 单帧高度，一般等于图片高度
    duration: Int = 1500 // 动画播放时长，单位毫秒
) {
    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition()

    // 计算当前动画帧
    val frame by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = frameCount.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration),
            repeatMode = RepeatMode.Restart
        )
    )
    val image = ImageBitmap.imageResource(id)
    Canvas(
        modifier = Modifier.size( // 只显示一帧大小
            width = with(density) { frameWidth.toDp() },
            height = with(density) { frameHeight.toDp() }
        )
    ) {
        val currentFrame = frame.toInt() % frameCount
        val offsetX = currentFrame * frameWidth
        drawImage(
            image = image,
            srcOffset = IntOffset(offsetX.toInt(), 0),
            srcSize = IntSize(frameWidth.toInt(), frameHeight.toInt())
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SpriteSheetPreview() {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        SpriteSheet(id = R.drawable.one_coin_ani)
        SpriteSheet(id = R.drawable.two_coin_ani)
    }
}