package riggs.peter.flickrgrid.data.api

/**
 * Represents a search request made to the Flickr API
 */
data class FlickrSearchRequest(
    val searchText: String,
    val apiKey: String,
    val page: Int = 1
) {
    /**
     * Converts the search request object to the url used to fetch the search results.
     */
    fun toSearchUrl(): String {
        return "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=$apiKey&text=$searchText&format=json&nojsoncallback=1&page=$page"
    }

    /**
     * Generates a key for defining a unique search request
     */
    fun toKey(): String {
        return "search:$searchText-page:$page"
    }
}
