package riggs.peter.flickrgrid.data.api

/**
 * The response from the Flickr API when making a search request
 */
data class FlickrSearchResponse(
    val photos: FlickrPhotoPage
)
