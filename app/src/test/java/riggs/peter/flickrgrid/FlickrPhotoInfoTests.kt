package riggs.peter.flickrgrid

import org.junit.Test

import org.junit.Assert.*
import riggs.peter.flickrgrid.data.api.FlickrPhotoInfo

/**
 * Tests for the FlickrPhotoInfo class
 */
class FlickrPhotoInfoTests {
    @Test
    fun `given valid fields, when toImageUrl called, then url is correct`() {
        val generatedUrl = FlickrPhotoInfo(
            id = "39593986652",
            secret = "0ec416669f",
            server = "4740",
            farm = 5
        ).toImageUrl()
        assertEquals("https://farm5.static.flickr.com/4740/39593986652_0ec416669f.jpg", generatedUrl)
    }
}