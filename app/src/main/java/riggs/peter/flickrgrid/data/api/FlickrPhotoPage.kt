package riggs.peter.flickrgrid.data.api

/**
 * Represents part of the Flickr search response. An object which contains the Flickr photo elements
 * and paging information.
 */
data class FlickrPhotoPage(
    val page: Int,
    val pages: Int,
    val photo: List<FlickrPhotoInfo>
)
