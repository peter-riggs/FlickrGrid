package riggs.peter.flickrgrid.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.glide.GlideImage
import riggs.peter.flickrgrid.R
import riggs.peter.flickrgrid.models.FlickrImage
import riggs.peter.flickrgrid.ui.theme.Grey3

@Composable
fun FlickrImageGridCell(flickrImage: FlickrImage) {
    Box(modifier = Modifier.height(150.dp)) {
        GlideImage(
            imageModel = flickrImage.url,
            contentScale = ContentScale.Crop,
            shimmerParams = ShimmerParams(
                baseColor = Grey3,
                highlightColor = Color.White,
                durationMillis = 500,
                dropOff = 0.65f,
                tilt = 20f
            ),
            error = ImageBitmap.imageResource(R.drawable.ic_download_failed),
        )
    }
}