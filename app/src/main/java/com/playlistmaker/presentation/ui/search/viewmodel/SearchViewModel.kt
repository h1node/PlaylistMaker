package com.playlistmaker.presentation.ui.search.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.playlistmaker.R
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.usecase.SearchMusicUseCase
import com.playlistmaker.presentation.ui.viewmodel.SearchState
import com.playlistmaker.presentation.ui.viewmodel.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class SearchViewModel(
    application: Application,
    private val searchMusicUseCase: SearchMusicUseCase
) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    private val _state = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = _state

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(SEARCH_DEBOUNCE_DELAY)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    searchMusicUseCase.execute(query)
                        .map<List<Music>, SearchState> { result ->
                            if (result.isEmpty()) {
                                SearchState.Empty(getApplication<Application>().getString(R.string.nothing_was_found))
                            } else {
                                SearchState.Content(result)
                            }
                        }
                        .onStart { emit(SearchState.Loading) }
                        .catch { e ->
                            emit(
                                SearchState.Error(
                                    getApplication<Application>().getString(R.string.connection_problem)
                                )
                            )
                            showToast.postValue(e.message ?: "Unknown error")
                        }
                }
                .collect { state ->
                    _state.postValue(state)
                }
        }
    }

    fun searchDebounce(newText: String) {
        _searchQuery.value = newText
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
