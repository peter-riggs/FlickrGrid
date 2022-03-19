package riggs.peter.flickrgrid.mocks

import io.reactivex.rxjava3.core.Single
import riggs.peter.flickrgrid.data.api.FlickrApiClient
import riggs.peter.flickrgrid.data.api.FlickrSearchResponse

class MockFlickrApiClient(private val fakeSearchResults: FlickrSearchResponse): FlickrApiClient {
    private var _searchRequestCount = 0
    val searchRequestCount
        get() = _searchRequestCount
    override fun getSearchResults(searchUrl: String): Single<FlickrSearchResponse> {
        _searchRequestCount++
        return Single.just(fakeSearchResults)
    }
}