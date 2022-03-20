package riggs.peter.flickrgrid.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

/**
 * The text field used to search for Flickr images.
 */
@Composable
fun SearchTextInput(onValueChange: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    TextField(
        value = text,
        onValueChange = {
            text = it
            onValueChange(it)
        },
        label = { Text("Search Flickr") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        singleLine = true
    )
}