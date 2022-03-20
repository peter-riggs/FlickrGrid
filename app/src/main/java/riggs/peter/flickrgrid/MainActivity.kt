package riggs.peter.flickrgrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import riggs.peter.flickrgrid.data.api.FlickrApiBuilder
import riggs.peter.flickrgrid.repositories.FlickrImageRepository
import riggs.peter.flickrgrid.ui.components.FlickrSearchContainer
import riggs.peter.flickrgrid.ui.theme.FlickrGridTheme
import riggs.peter.flickrgrid.ui.viewmodels.FlickrSearchViewModel
import riggs.peter.flickrgrid.ui.viewmodels.FlickrSearchViewModelFactory

class MainActivity : ComponentActivity() {

    // The FlickrImageSearch repository to be used throughout the app
    private val repository = FlickrImageRepository(
        apiKey = BuildConfig.FLICKR_API_KEY,
        apiClient = FlickrApiBuilder.apiService
    )
    // The FlickrSearchViewModel to be used by the FlickrSearchContainer
    private val flickrSearchViewModel: FlickrSearchViewModel by viewModels {
        FlickrSearchViewModelFactory(
            repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlickrGridTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    FlickrSearchContainer(flickrSearchViewModel)
                }
            }
        }
    }
}
