package com.playlistmaker.presentation.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.playlistmaker.R
import com.playlistmaker.databinding.FragmentSearchBinding
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.playlistmaker.domain.usecase.ManageSearchHistoryUseCase
import com.playlistmaker.presentation.adapter.MusicRVAdapter
import com.playlistmaker.presentation.ui.main.MainActivity
import com.playlistmaker.presentation.ui.search.viewmodel.SearchViewModel
import com.playlistmaker.presentation.ui.viewmodel.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: MusicRVAdapter
    private lateinit var historyAdapter: MusicRVAdapter

    private val viewModel: SearchViewModel by viewModel()
    private val manageSearchHistoryUseCase: ManageSearchHistoryUseCase by inject()
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase by inject()
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase by inject()

    private var clickJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = MusicRVAdapter { track ->
            (activity as MainActivity).animateBottomNavigationView()
            clickDebounce()
        }

        historyAdapter = MusicRVAdapter { track ->
            (activity as MainActivity).animateBottomNavigationView()
            clickDebounce()
        }

        setupViewModel()
        setupUI()
        updateHistory()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupViewModel() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                SearchState.Loading -> showLoading()
                is SearchState.Content -> showContent(state.musicList)
                is SearchState.Empty -> showEmpty(getString(state.messageId))
                is SearchState.Error -> showError(getString(state.messageId))
            }
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())

        val trackClickListener: (Music) -> Unit = { track ->
            if (clickDebounce()) {
                manageSearchHistoryUseCase.addTrackToHistory(track)
                openAudioPlayer(track)
                updateHistory()
            }
        }

        searchAdapter = MusicRVAdapter(trackClickListener)
        historyAdapter = MusicRVAdapter(trackClickListener)

        binding.rvSearch.adapter = searchAdapter
        binding.rvHistory.adapter = historyAdapter
    }

    private fun setupUI() {
        setupRecyclerView()
        setupSearchEditText()
        setupClearIcon()
        setupClearHistoryButton()
        binding.rvHistory.visibility = View.VISIBLE
        binding.rvSearch.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressContainer.visibility = View.VISIBLE
        hidePlaceholder()
        clearResults()
    }

    private fun showContent(musicList: List<Music>) {
        binding.progressContainer.visibility = View.GONE
        hidePlaceholder()
        searchAdapter.items = musicList
    }

    private fun showEmpty(message: String) {
        binding.progressContainer.visibility = View.GONE
        showPlaceholder(message, R.drawable.music_error)
        clearResults()
    }

    private fun showError(message: String) {
        binding.progressContainer.visibility = View.GONE
        showPlaceholder(message, R.drawable.internet_error)
        clearResults()
    }

    private fun updateHistory() {
        val history = getSearchHistoryUseCase.execute()
        historyAdapter.items = history
        binding.searched.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
        binding.clearHistory.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun openAudioPlayer(track: Music) {
        val action = SearchFragmentDirections.actionSearchFragmentToAudioPlayerFragment(track)
        findNavController().navigate(action)
    }

    private fun showPlaceholder(text: String, imageRes: Int) {
        binding.placeholderMessage.text = text
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderImageMusic.setImageResource(imageRes)
        binding.placeholderImageMusic.visibility = View.VISIBLE
    }

    private fun hidePlaceholder() {
        binding.placeholderMessage.visibility = View.GONE
        binding.placeholderImageMusic.visibility = View.GONE
    }

    private fun clearResults() {
        searchAdapter.items = emptyList()
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val isAllowed = clickJob == null || clickJob?.isCompleted == true
        if (isAllowed) {
            clickJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
            }
        }
        return isAllowed
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private fun setupSearchEditText() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchDebounce(query)
                    hideKeyboard()
                }
                true
            } else false
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                binding.clearIcon.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE

                if (query.isEmpty()) {
                    showHistory()
                } else {
                    binding.rvHistory.visibility = View.GONE
                    binding.rvSearch.visibility = View.VISIBLE
                    viewModel.searchDebounce(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showHistory() {
        clearResults()
        updateHistory()
        binding.rvHistory.visibility = View.VISIBLE
        binding.rvSearch.visibility = View.GONE
        hidePlaceholder()
    }

    private fun setupClearIcon() {
        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")
            it.visibility = View.GONE
            hideKeyboard()
            showHistory()
        }
    }

    private fun setupClearHistoryButton() {
        binding.clearHistory.setOnClickListener {
            if (clickDebounce()) {
                clearSearchHistoryUseCase.execute()
                updateHistory()
            }
        }
    }
}
