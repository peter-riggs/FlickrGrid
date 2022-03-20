package riggs.peter.flickrgrid.mocks

import io.reactivex.rxjava3.core.Single
import riggs.peter.flickrgrid.data.api.FlickrApiClient
import riggs.peter.flickrgrid.data.api.FlickrSearchResponse

/**
 * Mock API client to be used for tests. Fake search results can be passed in the constructor
 * to fake a successful response from the Flickr search API. It also keeps a count of the amount
 * of requests which have been triggered.
 */
class MockFlickrApiClient(private val fakeSearchResults: FlickrSearchResponse): FlickrApiClient {
    private var _searchRequestCount = 0
    val searchRequestCount
        get() = _searchRequestCount
    override fun getSearchResults(searchUrl: String): Single<FlickrSearchResponse> {
        _searchRequestCount++
        return Single.just(fakeSearchResults)
    }
}