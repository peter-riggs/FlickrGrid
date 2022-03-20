package riggs.peter.flickrgrid.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.glide.GlideImage
import riggs.peter.flickrgrid.R
import riggs.peter.flickrgrid.models.FlickrImage
import riggs.peter.flickrgrid.ui.theme.Grey3

/**
 * Displays a box to be used as a grid cell which contains a FlickrImage
 *
 * This implementation uses Glide (via https://github.com/skydoves/landscapist) to render the image
 * and handle image loading and caching.
 */
@Composable
fun FlickrImageGridCell(flickrImage: FlickrImage) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .background(Grey3),
        contentAlignment = Alignment.Center
    ) {
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
            failure = {
                Image(
                    painter = painterResource(id = R.drawable.ic_download_failed),
                    contentDescription = "flickr-image",
                    modifier = Modifier.size(40.dp)
                )
            }
        )
    }
}