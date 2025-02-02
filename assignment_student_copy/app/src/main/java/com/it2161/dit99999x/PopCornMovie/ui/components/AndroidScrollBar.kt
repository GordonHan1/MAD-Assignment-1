package com.it2161.dit99999x.PopCornMovie.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun Modifier.androidScrollbar(
    state: LazyListState,
    color: Color = Color(0xFF666666),
    minThumbHeight: Dp = 24.dp
): Modifier {
    val previousProgress = remember { mutableStateOf(0f) }
    val previousThumbHeight = remember { mutableStateOf(0f) }

    return drawWithContent {
        drawContent()

        if (state.layoutInfo.totalItemsCount > 0) {
            // Extract values to reduce redundant computations
            val layoutInfo = state.layoutInfo
            val firstVisibleItemIndex = state.firstVisibleItemIndex
            val visibleItemsCount = layoutInfo.visibleItemsInfo.size
            val totalItemsCount = layoutInfo.totalItemsCount

            if (visibleItemsCount == 0 || totalItemsCount <= visibleItemsCount) return@drawWithContent

            val firstVisibleItemOffset = state.firstVisibleItemScrollOffset
            val itemSize = layoutInfo.visibleItemsInfo.firstOrNull()?.size?.toFloat() ?: return@drawWithContent

            // Precompute constants
            val totalScrollableDistance = (totalItemsCount - visibleItemsCount).coerceAtLeast(1).toFloat()
            val normalizedOffset = firstVisibleItemOffset / itemSize
            val exactProgress = (firstVisibleItemIndex + normalizedOffset) / totalScrollableDistance

            // Smooth progress and avoid sudden jumps
            val smoothedProgress = lerp(previousProgress.value, exactProgress.coerceIn(0f, 1f), 0.15f)
            previousProgress.value = smoothedProgress

            // Compute raw scrollbar height and apply smoothing
            val rawThumbHeight = (visibleItemsCount.toFloat() / totalItemsCount.toFloat()) * size.height
            val smoothThumbHeight = lerp(previousThumbHeight.value, rawThumbHeight.coerceAtLeast(minThumbHeight.toPx()), 0.15f)
            previousThumbHeight.value = smoothThumbHeight

            // Compute final scrollbar position
            val scrollbarOffsetY = (size.height - smoothThumbHeight) * smoothedProgress

            // Draw scrollbar
            drawRect(
                color = color,
                topLeft = Offset(size.width - 4.dp.toPx(), scrollbarOffsetY),
                size = Size(4.dp.toPx(), smoothThumbHeight),
                alpha = if (state.isScrollInProgress) 0.8f else 0.6f
            )
        }
    }
}

// Linear interpolation function
fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}