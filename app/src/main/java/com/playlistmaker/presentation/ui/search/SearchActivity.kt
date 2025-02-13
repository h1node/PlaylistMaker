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
import androidx.activity.enableEdgeToEdge
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
import com.playlistmaker.domain.usecase.SearchMusicUseCase
import com.playlistmaker.presentation.adapter.MusicRVAdapter
import com.playlistmaker.presentation.ui.player.AudioPlayerActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: MusicRVAdapter
    private lateinit var historyAdapter: MusicRVAdapter

    private lateinit var searchMusicUseCase: SearchMusicUseCase
    private lateinit var manageSearchHistoryUseCase: ManageSearchHistoryUseCase
    private lateinit var getSearchHistoryUseCase: GetSearchHistoryUseCase
    private lateinit var clearSearchHistoryUseCase: ClearSearchHistoryUseCase

    private val handler = Handler(Looper.getMainLooper())
    private var inputText: String = ""
    private var isClickAllowed = true

    private lateinit var creator: Creator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        creator = Creator(this)

        searchMusicUseCase = creator.searchMusicUseCase
        manageSearchHistoryUseCase = creator.manageSearchHistoryUseCase
        getSearchHistoryUseCase = creator.getSearchHistoryUseCase
        clearSearchHistoryUseCase = creator.clearSearchHistoryUseCase

        setupUI()
        updateHistory()
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
            manageSearchHistoryUseCase.addTrackToHistory(track)
            openAudioPlayer(track)
            updateHistory()
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        val query = binding.searchEditText.text.toString().trim()
        handler.removeCallbacksAndMessages(null)
        if (query.isNotEmpty()) {
            handler.postDelayed({ searchMusic(query) }, SEARCH_DEBOUNCE_DELAY)
        } else {
            clearResults()
        }
    }

    private fun setupSearchEditText() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString().trim()
                if (query.isNotEmpty()) searchMusic(query)
                true
            } else false
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                inputText = s?.toString() ?: ""
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
            clearSearchHistoryUseCase.execute()
            updateHistory()
        }
    }

    private fun searchMusic(query: String) {
        binding.progressContainer.visibility = View.VISIBLE
        hidePlaceholder()

        searchMusicUseCase.execute(
            query,
            { result ->
                binding.progressContainer.visibility = View.GONE
                if (result.isNotEmpty()) {
                    searchAdapter.items = result
                } else {
                    showPlaceholder(getString(R.string.nothing_was_found), R.drawable.music_error)
                }
            },
            {
                binding.progressContainer.visibility = View.GONE
                showPlaceholder(getString(R.string.connection_problem), R.drawable.internet_error)
            }
        )
    }

    private fun updateHistory() {
        val history = getSearchHistoryUseCase.execute()
        historyAdapter.items = history

        binding.searched.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
        binding.clearHistory.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun openAudioPlayer(track: Music) {
        if (clickDebounce()) {
            val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                putExtra("track", track)
            }
            startActivity(intent)
        }
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
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}