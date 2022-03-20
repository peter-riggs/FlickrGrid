package riggs.peter.flickrgrid.repositories

import io.reactivex.rxjava3.core.Single
import riggs.peter.flickrgrid.data.api.FlickrApiClient
import riggs.peter.flickrgrid.data.api.FlickrSearchRequest
import riggs.peter.flickrgrid.models.FlickrImage

/**
 * Provides a way to access Flickr image data, either from the Flickr API or from an in-memory cache
 */
class FlickrImageRepository(private val apiKey: String, private val apiClient: FlickrApiClient) {

    private val cachedSearchRequests = mutableMapOf<String, List<FlickrImage>>()

    /**
     * Gets flicker images for a search phrase and page. Will return a cached copy of the image data
     * response if it has already been fetched for this session.
     * @param searchText the search phrase to use when querying the Flickr API
     * @param page the page to fetch (default = 1)
     */
    fun getFlickrImages(searchText: String, page: Int = 1): Single<List<FlickrImage>> {
        val searchRequest = FlickrSearchRequest(searchText, apiKey, page)
        val searchRequestKey = searchRequest.toKey()
        return cachedSearchRequests[searchRequestKey]?.let { Single.just(it) }
            ?: apiClient.getSearchResults(searchRequest.toSearchUrl())
                .map { response ->
                    val imageList = response.photos.photo.map { photoInfo ->
                        FlickrImage(photoInfo.toImageUrl())
                    }
                    cachedSearchRequests[searchRequestKey] = imageList
                    imageList
                }
    }
}