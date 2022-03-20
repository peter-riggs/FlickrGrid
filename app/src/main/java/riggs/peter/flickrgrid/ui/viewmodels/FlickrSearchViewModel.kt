package riggs.peter.flickrgrid.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Single
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

    // Disposable for the searchTextSubject
    private var searchTextDisposable: Disposable? = null

    // Disposable for the search results fetching
    private var searchResultsFetchDisposable: Disposable? = null

    // Represents the current state of the users search input
    private var searchData: SearchData? = null

    init {
        respondToSearchTextChanges()
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
            fetchAndUpdateNewPage(currentSearchData)
        }
    }

    /**
     * Helper to fetch a new page of data for the current search term and append the results to the
     * LiveData list.
     */
    private fun fetchAndUpdateNewPage(currentSearchData: SearchData) {
        val newSearchData =
            SearchData(currentSearchData.searchText, currentSearchData.currentPage + 1)
        searchData = newSearchData
        searchResultsFetchDisposable =
            repository.getFlickrImages(newSearchData.searchText, newSearchData.currentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ nextImageList ->
                    val newList = _searchResults.value?.let { currentImageList ->
                        currentImageList + nextImageList
                    } ?: return@subscribe
                    _searchResults.value = newList
                }, {
                    handleError(it)
                })
    }

    /**
     * Sets up the searchTextSubject observable flow for handling new search inputs.
     * This is responsible for triggering a fetch of Flickr image search data when the search text
     * changes. It ensures that requests aren't made too frequently and that outdated requests
     * are cancelled.
     */
    private fun respondToSearchTextChanges() {
        searchTextDisposable = searchTextSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .debounce(200, TimeUnit.MILLISECONDS)
            .toFlowable(BackpressureStrategy.LATEST)
            .switchMap { input ->
                getUpdatedResultsForNewSearch(input).toFlowable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _searchResults.value = it
            }, {
                handleError(it)
            })
    }

    /**
     * Helper to get the list of new search results from the repository based off the new search
     * text input.
     * Also handles empty search strings and resetting the [searchData] object.
     * @param searchText the new search input
     */
    private fun getUpdatedResultsForNewSearch(
        searchText: String
    ): Single<List<FlickrImage>> {
        if (searchText.isBlank()) {
            searchData = null
            return Single.just(listOf())
        }
        val newSearchData = SearchData(searchText, 1)
        searchData = newSearchData
        return repository.getFlickrImages(newSearchData.searchText, newSearchData.currentPage)
    }

    /**
     * Calculation to figure out if the current scroll position of a grid has gone past the halfway
     * point of the last page.
     * todo: This could be moved into a public utility function and be unit tested.
     * Currently it contains hardcoded values for row length (grid cells) and page size.
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
     * Clears the disposables subscribed to the search text subject and search fetch observable when
     * the view model is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        searchResultsFetchDisposable?.dispose()
        searchResultsFetchDisposable = null
        searchTextDisposable?.dispose()
        searchTextDisposable = null
    }
}