package riggs.peter.flickrgrid.data.api

data class FlickrSearchRequest(
    val searchText: String,
    val apiKey: String
) {
    fun toSearchUrl(): String {
        return "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=$apiKey&text=$searchText&format=json&nojsoncallback=1"
    }
}
