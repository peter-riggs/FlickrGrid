package riggs.peter.flickrgrid.data.api

/**
 * Contains all the fields from a Flickr Photo json response object required for generating the url
 * for downloading a Flickr Image.
 */
data class FlickrPhotoInfo(
    val id: String,
    val secret: String,
    val server: String,
    val farm: Int,
) {
    fun toImageUrl(): String {
        return "https://farm$farm.static.flickr.com/$server/${id}_$secret.jpg"
    }
}
