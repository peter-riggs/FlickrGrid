package riggs.peter.flickrgrid.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import riggs.peter.flickrgrid.models.FlickrImage
import riggs.peter.flickrgrid.repositories.FlickrImageRepository
import java.util.concurrent.TimeUnit

data class SearchData(val searchText: String, val currentPage: Int)

class FlickrSearchViewModel(private val repository: FlickrImageRepository) : ViewModel() {
    private val _searchResults = MutableLiveData<List<FlickrImage>>(listOf())
    val searchResults: LiveData<List<FlickrImage>>
        get() = _searchResults
    private val searchTextSubject = PublishSubject.create<String>()
    private var searchTextDisposable: Disposable? = null
    private var searchData: SearchData? = null

    init {
        respondToSearchTextChanges()
    }

    private fun respondToSearchTextChanges() {
        searchTextDisposable = searchTextSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .toFlowable(BackpressureStrategy.LATEST)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getImagesForNewSearch(it)
            }, {
                handleError(it)
            })
    }

    private fun getImagesForNewSearch(searchText: String) {
        if (searchText.isBlank()) {
            _searchResults.value = listOf()
            searchData = null
            return
        }
        val newSearchData = SearchData(searchText, 1)
        updateSearchResultsFromRepository(newSearchData) {
            _searchResults.value = it
        }
    }

    private fun updateSearchResultsFromRepository(
        newSearchData: SearchData,
        onNewImagesFetched: (List<FlickrImage>) -> Unit
    ) {
        searchData = newSearchData
        repository.getFlickrImages(newSearchData.searchText, newSearchData.currentPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onNewImagesFetched(it)
            }, {
                _searchResults.value = listOf()
            })
    }

    fun onNewSearchTextEntered(searchText: String) {
        // empty results
        _searchResults.value = listOf()
        searchTextSubject.onNext(searchText)
    }

    fun setFirstVisibleRowIndex(newRowIndex: Int) {
        val currentSearchData = searchData ?: return
        if (hasScrolledHalfwayPastLastPage(newRowIndex, currentSearchData)) {
            val newSearchData =
                SearchData(currentSearchData.searchText, currentSearchData.currentPage + 1)
            updateSearchResultsFromRepository(newSearchData) { nextImageList ->
                val newList = _searchResults.value?.let { currentImageList ->
                    currentImageList + nextImageList
                } ?: return@updateSearchResultsFromRepository
                _searchResults.value = newList
            }
        }
    }

    private fun hasScrolledHalfwayPastLastPage(
        newRowIndex: Int,
        currentSearchData: SearchData
    ): Boolean {
        val page = currentSearchData.currentPage
        val cellsPerRow = 3
        val imagesPerFetch = 100
        val rowsPerPage = imagesPerFetch / cellsPerRow
        val halfwayPointOfCurrentPage = rowsPerPage * (page - 1) + rowsPerPage / 2
        return newRowIndex > halfwayPointOfCurrentPage
    }

    private fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
        _searchResults.value = listOf()
    }

    override fun onCleared() {
        super.onCleared()
        searchTextDisposable?.dispose()
        searchTextDisposable = null
    }
}