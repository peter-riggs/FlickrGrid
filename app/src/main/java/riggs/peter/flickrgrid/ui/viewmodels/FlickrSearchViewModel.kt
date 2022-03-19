package riggs.peter.flickrgrid.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import riggs.peter.flickrgrid.models.FlickrImage
import riggs.peter.flickrgrid.repositories.FlickrImageRepository
import java.util.concurrent.TimeUnit


class FlickrSearchViewModel(private val repository: FlickrImageRepository): ViewModel() {
    private val _searchResults = MutableLiveData<List<FlickrImage>>(listOf())
    val searchResults: LiveData<List<FlickrImage>>
        get() = _searchResults

    private val searchTextSubject = PublishSubject.create<String>()

    init {
        respondToSearchTextChanges()
    }

    private fun respondToSearchTextChanges() {
        searchTextSubject
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribe {
                getNewImagesFromRepository(it)
            }
    }

    private fun getNewImagesFromRepository(searchText: String) {
        if (searchText.isBlank()) {
            _searchResults.value = listOf()
            return
        }
        repository.getFlickrImages(searchText)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
               _searchResults.value = it
            },{
                _searchResults.value = listOf()
            })
    }

    fun onNewSearchTextEntered(searchText: String) {
        // empty results
        _searchResults.value = listOf()
        searchTextSubject.onNext(searchText)
    }
}