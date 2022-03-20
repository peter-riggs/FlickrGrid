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

/**
 * Represents the current state of the Flickr image search.
 */
data class SearchData(val searchText: String, val currentPage: Int)

/**
 * View model for the Flickr search screen.
 */
class FlickrSearchViewModel(private val repository: FlickrImageRepository) : ViewModel() {
    // LiveData containing the search results as a list of FlickrImages
    private val _searchResults = MutableLiveData<List<FlickrImage>>(listOf())
    val searchResults: LiveData<List<FlickrImage>>
        get() = _searchResults

    // A PublishSubject, used to convert the search text input to an observable stream which can
    // be used to trigger queries to the repository
    private val searchTextSubject = PublishSubject.create<String>()
    // disposable for the searchTextSubject, so we can clean up and avoid memory leaks when the
    // view model has been destroyed
    private var searchTextDisposable: Disposable? = null
    // represents the current state of the users search input
    private var searchData: SearchData? = null

    init {
        respondToSearchTextChanges()
    }

    /**
     * Sets up the searchTextSubject observable flow for handling new search inputs.
     * This is responsible for triggering a fetch of Flickr image search data when the search text
     * changes, and it ensures that requests aren't made too frequently and that backpressure is
     * handled appropriately.
     */
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

    /**
     * Sets a new image list based on the new search text input
     * @param searchText the new search text value
     */
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

    /**
     * Fetches the image data from the repository based on search input.
     * @param newSearchData The search text and page to fetch
     * @param onNewImagesFetched callback to be invoked on a successful fetch of image data
     */
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
                handleError(it)
            })
    }

    /**
     * To be called from the UI, whenever the search input text value changes
     * @param searchText the new search text input value
     */
    fun onNewSearchTextEntered(searchText: String) {
        // empty results
        _searchResults.value = listOf()
        searchTextSubject.onNext(searchText)
    }

    /**
     * To be called from the UI during scrolling to keep track of the current scroll position.
     * This is used to trigger pagination.
     */
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

    /**
     * Calculation to figure out if the current scroll position of a grid has gone past the halfway
     * point of the last page. This could be moved into a public utility function and be unit
     * tested. Currently it contains hardcoded values for row length (grid cells) and page size.
     */
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

    /**
     * Simply prints the error stacktrace and empties the string. This could be improved to provide
     * an error UI data to be consumed by the UI.
     */
    private fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
        _searchResults.value = listOf()
    }

    /**
     * Clears the disposable subscribed to the search text subject when the view model is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        searchTextDisposable?.dispose()
        searchTextDisposable = null
    }
}