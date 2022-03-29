package course.examples.networking.get.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    /** Modifiable live data for internal use only. */
    private val _liveData = MutableLiveData<String>()

    /** Immutable LiveData for external publishing/observing. */
    val liveData: LiveData<String>
        get() = _liveData

    /** GSon instance used to make JSON string more readable. */
    val gson = GsonBuilder().setPrettyPrinting().create()

    /** Keep track of coroutine so that duplicate requests are ignored. */
    var job: Job? = null

    /**
     * Non-blocking function called by the UI (Fragment) when the
     * user clicks the send button. This function starts a coroutine
     * that sends a network request and then posts the JSON result
     * on the LiveData feed that can be observed by the calling
     * Fragment.
     */
    fun onSendButtonClicked() {
        // Ignore button click if a request is still active.
        if (job?.isActive == true) {
            return
        }

        // Immediately post a busy string to the LiveData feed.
        _liveData.postValue("Performing GET request ...")

        // Launch a new coroutine to run network request in the background.
        job = viewModelScope.launch {
            try {
                // 1. Run the suspending network request.
                val rawJson = makeNetworkCall(URL)

                // 2. Post the returned JSON string to the LiveData feed.
                _liveData.postValue(rawJson.prettyPrint())
            } catch (e: Exception) {
                // Something went wrong ... post error to LiveData feed.
                _liveData.postValue("Network request failed: ${e.message}")
            }
        }
    }

    /**
     * String extension function that converts receiver
     * String to a more readable JSON string.
     */
    private fun String.prettyPrint(): String =
        gson.toJson(JsonParser.parseString(this))

    /**
     * Suspending helper function that performs the network request
     * specified by the passed [url] and then posts the JSON String
     * result to the LiveData feed that is observed by the Fragment.
     */
    private suspend fun makeNetworkCall(url: String): String =
        withContext(Dispatchers.IO) {
            // Construct a new Ktor HttpClient to perform the get
            // request and then return the JSON result.
            HttpClient().get(url)
        }

    /**
     * Constants
     */
    companion object {
        private const val TAG = "HttpGetTask"

        // Get your own user name at http://www.geonames.org/login
        private const val USER_NAME = "aporter"

        private const val HOST = "api.geonames.org"
        private const val URL = "http://$HOST/earthquakesJSON?" +
                "north=44.1&" +
                "south=-9.9&" +
                "east=-22.4&" +
                "west=55.2&" +
                "username=$USER_NAME"
    }
}