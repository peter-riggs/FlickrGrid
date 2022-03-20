package riggs.peter.flickrgrid

import org.junit.Assert
import org.junit.Test
import riggs.peter.flickrgrid.data.api.FlickrPhotoInfo
import riggs.peter.flickrgrid.data.api.FlickrPhotoPage
import riggs.peter.flickrgrid.data.api.FlickrSearchResponse
import riggs.peter.flickrgrid.mocks.MockFlickrApiClient
import riggs.peter.flickrgrid.models.FlickrImage
import riggs.peter.flickrgrid.repositories.FlickrImageRepository

/**
 * Tests for the [FlickrImageRepository] class
 */
class FlickrImageRepositoryTests {

    @Test
    fun `given search request, when successful, then FlickrImage list correct`() {
        // given
        val fakeResults = FlickrSearchResponse(
            photos = FlickrPhotoPage(
                1, 100, photo = listOf(
                    FlickrPhotoInfo("testId1", "testSecret1", "testServer1", 1),
                    FlickrPhotoInfo("testId2", "testSecret2", "testServer1", 1),
                    FlickrPhotoInfo("testId3", "testSecret3", "testServer1", 1),
                )
            )
        )
        // when
        val results = FlickrImageRepository("abc123", MockFlickrApiClient(fakeResults))
            .getFlickrImages("test")
            .blockingGet()
        // then
        Assert.assertEquals(
            listOf(
                FlickrImage("https://farm1.static.flickr.com/testServer1/testId1_testSecret1.jpg"),
                FlickrImage("https://farm1.static.flickr.com/testServer1/testId2_testSecret2.jpg"),
                FlickrImage("https://farm1.static.flickr.com/testServer1/testId3_testSecret3.jpg"),
            ), results
        )
    }

    @Test
    fun `given multiple requests, when requests are the same, then API call only happens once`() {
        // given
        val fakeResults = FlickrSearchResponse(
            photos = FlickrPhotoPage(
                1, 100, photo = listOf(
                    FlickrPhotoInfo("testId1", "testSecret1", "testServer1", 1),
                    FlickrPhotoInfo("testId2", "testSecret2", "testServer1", 1),
                    FlickrPhotoInfo("testId3", "testSecret3", "testServer1", 1),
                )
            )
        )
        val mockClient = MockFlickrApiClient(fakeResults)
        //when
        val imageRepository = FlickrImageRepository("abc123", mockClient)
        imageRepository
            .getFlickrImages("test")
            .blockingGet()
        imageRepository
            .getFlickrImages("test")
            .blockingGet()
        // then
        Assert.assertEquals(1, mockClient.searchRequestCount)
    }
}