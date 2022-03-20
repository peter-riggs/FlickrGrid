package riggs.peter.flickrgrid.data.api

import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Basic interface for getting search results from the Flickr API.
 */
interface FlickrApiClient {

    /**
     * @param searchUrl generated from [FlickrSearchRequest.toSearchUrl]
     * @return a FlickrSearchResponse as a [Single]
     */
    @GET
    fun getSearchResults(@Url searchUrl: String): Single<FlickrSearchResponse>
}

/**
 * Uses the retrofit library to build an implementation of the API client for connecting to the
 * flickr API
 */
object FlickrApiBuilder {

    private const val BASE_URL = "https://api.flickr.com/"

    private fun getRetrofit(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * The API client implementation
     */
    val apiService: FlickrApiClient = getRetrofit().create(FlickrApiClient::class.java)
}