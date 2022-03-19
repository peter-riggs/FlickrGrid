package riggs.peter.flickrgrid

import org.junit.Assert
import org.junit.Test
import riggs.peter.flickrgrid.data.api.FlickrSearchRequest

class FlickrSearchRequestTests {

    @Test
    fun `given valid fields, when toSearchUrl called, then url is correct`() {
        val generatedUrl = FlickrSearchRequest(
            searchText = "kittens",
            apiKey = "abc123"
        ).toSearchUrl()
        Assert.assertEquals(
            "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=abc123&text=kittens&format=json&nojsoncallback=1&page=1",
            generatedUrl
        )
    }

    @Test
    fun `given custom page, when toSearchUrl called, then url has correct page`() {
        val generatedUrl = FlickrSearchRequest(
            searchText = "kittens",
            apiKey = "abc123",
            page=22
        ).toSearchUrl()
        Assert.assertEquals(
            "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=abc123&text=kittens&format=json&nojsoncallback=1&page=22",
            generatedUrl
        )
    }
}