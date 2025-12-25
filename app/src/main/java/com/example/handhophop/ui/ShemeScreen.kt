package com.example.handhophop.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.compose.AsyncImage
import com.example.handhophop.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import androidx.core.graphics.get
import androidx.core.graphics.createBitmap

@Composable
fun ShemeScreen(
    navController: NavHostController,
    selectedVm: SelectedSchemeViewModel
) {
    val bg = colorResource(R.color.bg_beige)
    val selectedUrl by selectedVm.selectedUrl.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        BackgroundPattern()

        Column(modifier = Modifier.fillMaxSize()) {
            TopBanner(title = stringResource(R.string.home_title_incomplete))

            CenterContentScheme(imageUrl = selectedUrl)

            BottomBar(navController = navController, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ColumnScope.CenterContentScheme(imageUrl: String?) {
    val sidePad = dimensionResource(id = R.dimen.screen_side_padding)

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = sidePad),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.center_block_gap))
        ) {
            SchemePreviewBlock(imageUrl = imageUrl)
            DownloadButton()

            ColorPaletteFromImage(imageUrl = imageUrl, fallbackDrawable = R.drawable.project_preview)
        }
    }
}

@Composable
private fun SchemePreviewBlock(imageUrl: String?) {
    val r = dimensionResource(R.dimen.block_radius)
    val elevation0 = dimensionResource(R.dimen.block_elevation)
    val pad = dimensionResource(R.dimen.preview_text_h_padding)
    val outerColor = colorResource(R.color.card_beige)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = pad),
        shape = RoundedCornerShape(r),
        colors = CardDefaults.cardColors(containerColor = outerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation0)
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            val painter = painterResource(R.drawable.project_preview)
            val intrinsic: Size = painter.intrinsicSize
            val aspect = if (intrinsic.width > 0f && intrinsic.height > 0f) {
                intrinsic.width / intrinsic.height
            } else 1f

            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspect)
            )
        }
    }
}

@Composable
private fun DownloadButton() {
    val h = dimensionResource(R.dimen.stats_button_height)
    val r = dimensionResource(R.dimen.block_radius)
    val elevation0 = dimensionResource(R.dimen.block_elevation)

    val bg = colorResource(R.color.primary_brown)
    val textWhite = Color.White

    Button(
        onClick = { /* TODO */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(h),
        shape = RoundedCornerShape(r),
        colors = ButtonDefaults.buttonColors(containerColor = bg),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation0)
    ) {
        Text(
            text = stringResource(R.string.download_scheme),
            color = textWhite,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 *   Палитра из изображения:
 * - если imageUrl != null → грузим через Coil bitmap
 * - иначе → берём bitmap из drawable
 * - считаем 10 самых частых цветов (с квантованием)
 */
@Composable
private fun ColorPaletteFromImage(
    imageUrl: String?,
    @DrawableRes fallbackDrawable: Int
) {
    val titleColor = colorResource(R.color.text_dark)
    val gap = 8.dp
    val context = LocalContext.current

    val key = imageUrl ?: "drawable:$fallbackDrawable"

    var palette by remember(key) { mutableStateOf<List<Color>>(emptyList()) }
    var loading by remember(key) { mutableStateOf(true) }

    LaunchedEffect(key) {
        loading = true
        palette = emptyList()

        val bmp: Bitmap? = if (imageUrl != null) {
            loadBitmapFromUrl(context, imageUrl)
        } else {
            loadBitmapFromDrawable(context, fallbackDrawable)
        }

        palette = if (bmp != null) {
            // считаем 10 популярных
            extractTopColors(bmp, topN = 10)
        } else {
            // если вдруг не получилось — старый вариант
            (1..10).map { getColorForIndex(it) }
        }

        loading = false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(gap)
    ) {
        Text(
            text = stringResource(R.string.color_palette),
            color = titleColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (loading) {
            CircularProgressIndicator(color = titleColor)
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                palette.take(10).forEachIndexed { index, c ->
                    ColorSwatch(color = c, number = index + 1)
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(color: Color, number: Int) {
    val size = 32.dp
    val fontSize = 10.sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(color, RoundedCornerShape(4.dp))
        )
        Text(
            text = number.toString(),
            fontSize = fontSize,
            color = Color.Black.copy(alpha = 0.7f)
        )
    }
}

private suspend fun loadBitmapFromUrl(context: Context, url: String): Bitmap? = withContext(Dispatchers.IO) {
    try {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()

        val result = loader.execute(request)
        if (result is SuccessResult) {
            val drawable = result.drawable
            drawableToBitmap(drawable)
        } else null
    } catch (_: Exception) {
        null
    }
}

private suspend fun loadBitmapFromDrawable(context: Context, @DrawableRes resId: Int): Bitmap? =
    withContext(Dispatchers.IO) {
        try {
            BitmapFactory.decodeResource(context.resources, resId)
        } catch (_: Exception) {
            null
        }
    }

private fun drawableToBitmap(drawable: android.graphics.drawable.Drawable): Bitmap? {
    return try {
        if (drawable is android.graphics.drawable.BitmapDrawable) {
            drawable.bitmap
        } else {
            val w = max(1, drawable.intrinsicWidth)
            val h = max(1, drawable.intrinsicHeight)
            val bitmap = createBitmap(w, h)
            val canvas = android.graphics.Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    } catch (_: Exception) {
        null
    }
}

/**
 * Берём topN наиболее частых цветов.
 * Чтобы “популярность” была стабильной, делаем квантование цвета:
 * сжимаем RGB до шагов, тогда близкие оттенки считаются одинаковыми.
 */
private fun extractTopColors(source: Bitmap, topN: Int): List<Color> {
    val bmp = downscaleBitmap(source, maxSide = 240)

    val counts = HashMap<Int, Int>(4096)

    val w = bmp.width
    val h = bmp.height

    // шаг квантования
    val step = 32

    for (y in 0 until h) {
        for (x in 0 until w) {
            val c = bmp[x, y]

            val a = (c ushr 24) and 0xFF
            if (a < 40) continue

            val r = (c ushr 16) and 0xFF
            val g = (c ushr 8) and 0xFF
            val b = c and 0xFF

            val rq = quantize(r, step)
            val gq = quantize(g, step)
            val bq = quantize(b, step)

            val packed = (0xFF shl 24) or (rq shl 16) or (gq shl 8) or bq
            counts[packed] = (counts[packed] ?: 0) + 1
        }
    }

    val sorted = counts.entries
        .sortedByDescending { it.value }
        .take(topN)
        .map { Color(it.key) }

    return sorted.ifEmpty { (1..topN).map { getColorForIndex(it) } }
}

private fun quantize(v: Int, step: Int): Int {
    val q = (v / step) * step
    return min(255, max(0, q))
}

@SuppressLint("UseKtx")
private fun downscaleBitmap(bmp: Bitmap, maxSide: Int): Bitmap {
    val w = bmp.width
    val h = bmp.height
    val maxDim = max(w, h)
    if (maxDim <= maxSide) return bmp

    val scale = maxSide.toFloat() / maxDim.toFloat()
    val nw = max(1, (w * scale).toInt())
    val nh = max(1, (h * scale).toInt())
    return Bitmap.createScaledBitmap(bmp, nw, nh, true)
}


private fun getColorForIndex(index: Int): Color {
    return when (index) {
        1 -> Color(0xFFA5D6A7)
        2 -> Color(0xFF90CAF9)
        3 -> Color(0xFFCE93D8)
        4 -> Color(0xFFFFCC80)
        5 -> Color(0xFFEF9A9A)
        6 -> Color(0xFFBDBDBD)
        7 -> Color(0xFF64B5F6)
        8 -> Color(0xFF4DB6AC)
        9 -> Color(0xFF8D6E63)
        10 -> Color(0xFF795548)
        else -> Color.Gray
    }
}
