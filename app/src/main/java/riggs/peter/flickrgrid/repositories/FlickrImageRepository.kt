package riggs.peter.flickrgrid.repositories

import io.reactivex.rxjava3.core.Single
import riggs.peter.flickrgrid.data.api.FlickrApiClient
import riggs.peter.flickrgrid.data.api.FlickrSearchRequest
import riggs.peter.flickrgrid.models.FlickrImage

class FlickrImageRepository(private val apiKey: String, private val apiClient: FlickrApiClient) {

    private val cachedSearchRequests = mutableMapOf<String, List<FlickrImage>>()

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