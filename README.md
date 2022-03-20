# FlickrGrid

A simple app to show you a grid of images from Flickr based on what you search for.

## Prerequisites

You will need to have a Flickr API key. See https://www.flickr.com/services/api/misc.api_keys.html.

Set the environment variable `FLICKR_API_KEY`, i.e.

```
export FLICKR_API_KEY=<your api key>
```

In order for the environment variable to be recognized in Android Studio, make sure you open Android Studio from a session where the environment variable is set, i.e:

```
open -a /Applications/Android\ Studio.app <path to project>
```

Alternatively, just change this line in the app's build.gradle file

```
buildConfigField "String", "FLICKR_API_KEY", "\"" + System.getenv("FLICKR_API_KEY") + "\""
```

to

```
buildConfigField "String", "FLICKR_API_KEY", "\"<your api key>\""
```

## Running the app

In Android Studio, run the `app` configuration.

## Running the Unit Tests

Run `./gradlew test` on the command line, or navigate to the tests in Android Studio and run them from within the IDE.

## Architecture Overview

This project aims to demonstrate an MVVM app architecture.

Model:

- `FlickrAPIClient` and request/response models. Used to fetch search data from the Flickr API.
- `FlickrImageRepository` which provides access to the API and in-memory cache

View Model:

- `FlickrSearchViewModel` for handling UI events and providing the UI with search results data fetched from the repository.

View:

- `FlickrSearchContainer`, `SearchTextInput` and `FlickrImageGridCell`: Jetpack Compose components responsible for rendering the UI and sending UI events to the ViewModel.
- `MainActivity` Entry point of the app where dependencies are constructed. This simply wraps the FlickrSearchContainer.

## Dependencies used

- JetPack Compose
- Android Lifecycle-aware components
- RetroFit
- Gson
- RxJava3
- Landscapist Glide (image library)

## Shortcuts and areas which can be improved

- There is no feedback in the UI if the Flickr search request fails (except you get an empty grid).
- The `ic_download_failed` icon has not been resized for different screen resolutions.
- There is no dark mode theming.
- There is no way to change the default page size of the Flickr search results.
- The pagination logic could have some unit tests.
- The pagination logic gets calculated on every scroll event, it could be optimized to only be called when the value of `verticalGridState.firstVisibleItemIndex` changes.
- There are no unique contentDescriptions for the image tiles in the grid, and the app does not include any accessibility features.
- The in-memory cache for search results has no expiry.
- There is no functionality to refresh the search results. The app could include pull-down to refresh functionality.
- The API client code is fragile, as it take an entire URL string and expects it to be able to convert the response to the `FlickrSearchResponse` model. There is nothing to force the usage of `FlickrSearchRequest.toSearchUrl`. It could be improved by being abstracted behind an interface which takes a `FlickrSearchRequest` object of a string url.
- Concurrent Access of the mutable in-memory cache in the `FlickrImageRepository` could cause problems if it is accessed from multiple threads. This can be solved by using a `Mutex`.
- Dependency injection is done manually, with dependencies built in the MainActivity. This can be improved by using a dependency injection framework such as Dagger.
- There is currently no navigation framework set up for this app, as it is a single screen app.
