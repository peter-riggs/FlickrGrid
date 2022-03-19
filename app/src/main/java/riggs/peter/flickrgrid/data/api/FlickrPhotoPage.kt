package riggs.peter.flickrgrid.data.api

data class FlickrPhotoPage(
    val page: Int,
    val pages: Int,
    val photo: List<FlickrPhotoInfo>
)
