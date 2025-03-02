package com.playlistmaker.presentation.ui.search.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.playlistmaker.R
import com.playlistmaker.core.Creator
import com.playlistmaker.domain.usecase.SearchMusicUseCase
import com.playlistmaker.presentation.ui.viewmodel.SearchState
import com.playlistmaker.presentation.ui.viewmodel.SingleLiveEvent

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(application: Application): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SearchViewModel(application)
                }
            }
    }

    private val searchMusicUseCase: SearchMusicUseCase
    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    private var latestSearchText: String? = null

    init {
        Creator.initialize(application)
        searchMusicUseCase = Creator.provideSearchMusicUseCase()
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            if (newSearchText.isNotEmpty()) {
                renderState(SearchState.Loading)

                searchMusicUseCase.execute(
                    query = newSearchText,
                    callback = { result ->
                        if (result.isNotEmpty()) {
                            renderState(SearchState.Content(result))
                        } else {
                            renderState(SearchState.Empty(getApplication<Application>().getString(R.string.nothing_was_found)))
                        }
                    },
                    errorCallback = { throwable ->
                        renderState(SearchState.Error(getApplication<Application>().getString(R.string.connection_problem)))
                        showToast.postValue(throwable.message ?: "Unknown error")
                    }
                )
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }
}