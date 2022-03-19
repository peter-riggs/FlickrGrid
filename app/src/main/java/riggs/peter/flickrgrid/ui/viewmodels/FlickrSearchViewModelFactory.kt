package riggs.peter.flickrgrid.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import riggs.peter.flickrgrid.repositories.FlickrImageRepository

class FlickrSearchViewModelFactory(private val repository: FlickrImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FlickrSearchViewModel(repository) as T
    }
}