package riggs.peter.flickrgrid.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import riggs.peter.flickrgrid.ui.viewmodels.FlickrSearchViewModel


/**
 * Creates the Flickr Image search UI, with a search box at the top and a scrollable gridview
 * underneath
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlickrSearchContainer(viewModel: FlickrSearchViewModel) {
    val flickrImages = viewModel.searchResults.observeAsState().value ?: listOf()
    val verticalGridState = rememberLazyListState()
    if (verticalGridState.isScrollInProgress) {
        LocalFocusManager.current.clearFocus()
    }
    viewModel.setFirstVisibleRowIndex(verticalGridState.firstVisibleItemIndex)
    Column(Modifier.padding(8.dp)) {
        SearchTextInput(onValueChange = { viewModel.onNewSearchTextEntered(it) })
        Spacer(Modifier.padding(4.dp))
        Box(Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                cells = GridCells.Fixed(3),
                state = verticalGridState
            ) {
                items(flickrImages) { FlickrImageGridCell(it) }
            }
        }
    }
}