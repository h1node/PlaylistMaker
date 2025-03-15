package com.playlistmaker.presentation.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.playlistmaker.R
import com.playlistmaker.core.Creator
import com.playlistmaker.databinding.ActivitySearchBinding
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.playlistmaker.domain.usecase.ManageSearchHistoryUseCase
import com.playlistmaker.presentation.adapter.MusicRVAdapter
import com.playlistmaker.presentation.ui.player.AudioPlayerActivity
import com.playlistmaker.presentation.ui.search.viewmodel.SearchViewModel
import com.playlistmaker.presentation.ui.viewmodel.SearchState

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: MusicRVAdapter
    private lateinit var historyAdapter: MusicRVAdapter

    private val viewModel: SearchViewModel by viewModels { SearchViewModel.getViewModelFactory(this.application) }
    private lateinit var manageSearchHistoryUseCase: ManageSearchHistoryUseCase
    private lateinit var getSearchHistoryUseCase: GetSearchHistoryUseCase
    private lateinit var clearSearchHistoryUseCase: ClearSearchHistoryUseCase

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        Creator.initialize(this)

        manageSearchHistoryUseCase = Creator.provideManageSearchHistoryUseCase()
        getSearchHistoryUseCase = Creator.provideGetSearchHistoryUseCase()
        clearSearchHistoryUseCase = Creator.provideClearSearchHistoryUseCase()

        setupWindowInsets()
        setupViewModel()
        setupUI()
        updateHistory()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewModel() {
        viewModel.observeState().observe(this) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showContent(state.musicList)
                is SearchState.Empty -> showEmpty(state.message)
                is SearchState.Error -> showError(state.message)
            }
        }

        viewModel.observeShowToast().observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        setupRecyclerView()
        setupToolbar()
        setupSearchEditText()
        setupClearIcon()
        setupClearHistoryButton()
    }

    private fun setupRecyclerView() {
        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.layoutManager = LinearLayoutManager(this)

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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                viewModel.searchDebounce(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClearIcon() {
        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")
            it.visibility = View.GONE
            hideKeyboard()
            clearResults()
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
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra("track", track)
        }
        startActivity(intent)
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
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}