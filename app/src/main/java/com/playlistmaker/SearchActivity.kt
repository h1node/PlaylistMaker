package com.playlistmaker

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.playlistmaker.data.ApiClient
import com.playlistmaker.data.MusicApi
import com.playlistmaker.data.itunesdb.Music
import com.playlistmaker.data.itunesdb.ResultResponse
import com.playlistmaker.databinding.ActivitySearchBinding
import com.playlistmaker.view.rv_adapter.MusicRVAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: MusicRVAdapter
    private lateinit var historyAdapter: MusicRVAdapter
    private lateinit var listener: OnSharedPreferenceChangeListener
    private var inputText: String = ""
    private var failedQuery: String? = null
    private val musicApi = ApiClient().getClient().create(MusicApi::class.java)

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

        setupRecyclerView()
        setupToolbar()
        setupSearchEditText()
        setupClearIcon()
        setupPlaceholderButton()
        setupClearHistoryButton()


        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == SEARCH_HISTORY) {
                getSearchHistory()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        getSearchHistory()
    }

    override fun onDestroy() {
        super.onDestroy()
        getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(
            listener
        )
    }

    private fun createJsonFromTrackList(trackList: List<Music>): String {
        return Gson().toJson(trackList)
    }

    private fun createTrackListFromJson(json: String): List<Music> {
        return Gson().fromJson(json, Array<Music>::class.java).toList()
    }

    private fun setupRecyclerView() {
        searchAdapter = MusicRVAdapter { track ->
            addTrackToHistory(track)
//            Toast.makeText(this, "Clicked on track: ${track.trackName}", Toast.LENGTH_SHORT).show()
        }
        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        binding.rvSearch.adapter = searchAdapter

        historyAdapter = MusicRVAdapter { track ->
            addTrackToHistory(track)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = historyAdapter
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    private fun setupSearchEditText() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchEditText.text.toString()
                if (query.isNotEmpty()) searchMusic(query)
                else Toast.makeText(this, "Enter a request", Toast.LENGTH_SHORT).show()
                true
            } else false
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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

    private fun setupPlaceholderButton() {
        binding.placeholderButton.setOnClickListener {
            val query = failedQuery
            if (query != null) searchMusic(query)
        }
        binding.placeholderButton.visibility = View.GONE
    }

    private fun setupClearHistoryButton() {
        binding.clearAdapter.setOnClickListener {
            clearSearchHistory()
        }
    }

    private fun searchMusic(query: String) {
        failedQuery = query
        hidePlaceholderMessage()
        musicApi.getMusic(query).enqueue(object : Callback<ResultResponse> {
            override fun onResponse(
                call: Call<ResultResponse>,
                response: Response<ResultResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        val searchResult = resultResponse.results.filter { track ->
                            !track.trackName.isNullOrBlank() && track.trackTimeMillis > 0
                        }
                        if (searchResult.isNotEmpty()) {
                            searchAdapter.items = searchResult
                            searchAdapter.notifyDataSetChanged()
                        } else {
                            showPlaceholderMessage(
                                getString(R.string.nothing_was_found),
                                R.drawable.music_error,
                                false
                            )
                        }
                    } ?: showPlaceholderMessage(
                        getString(R.string.nothing_was_found),
                        R.drawable.music_error,
                        false
                    )
                } else {
                    showPlaceholderMessage(
                        getString(R.string.nothing_was_found),
                        R.drawable.music_error,
                        false
                    )
                }
            }

            override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
                if (searchAdapter.items.isEmpty()) {
                    showPlaceholderMessage(
                        getString(R.string.connection_problem),
                        R.drawable.internet_error,
                        false
                    )
                }
                Log.e("SearchActivity", "Error!!", t)
            }
        })
    }

    private fun addTrackToHistory(track: Music) {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val trackListJson = sharedPreferences.getString(SEARCH_HISTORY, null)
        val trackList = if (trackListJson != null) {
            createTrackListFromJson(trackListJson).toMutableList()
        } else {
            mutableListOf()
        }
        trackList.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        trackList.add(0, track)
        if (trackList.size > 10) {
            trackList.removeAt(10)
        }

        val newTrackListJson = createJsonFromTrackList(trackList)
        sharedPreferences.edit().putString(SEARCH_HISTORY, newTrackListJson).apply()
        historyAdapter.items = trackList
        historyAdapter.notifyDataSetChanged()

        binding.searched.visibility = View.VISIBLE
        binding.clearAdapter.visibility = View.VISIBLE
    }

    private fun getSearchHistory() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val trackListJson = sharedPreferences.getString(SEARCH_HISTORY, null)
        if (trackListJson != null) {
            val trackList = createTrackListFromJson(trackListJson)
            historyAdapter.items = trackList
            historyAdapter.notifyDataSetChanged()

            binding.searched.visibility = View.VISIBLE
            binding.clearAdapter.visibility = View.VISIBLE
        } else {
            binding.searched.visibility = View.GONE
            binding.clearAdapter.visibility = View.GONE
        }
    }

    private fun clearSearchHistory() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        sharedPreferences.edit().remove(SEARCH_HISTORY).apply()
        historyAdapter.items = emptyList()
        historyAdapter.notifyDataSetChanged()

        binding.searched.visibility = View.GONE
        binding.clearAdapter.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_KEY, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(EDIT_TEXT_KEY, "")
        binding.searchEditText.setText(inputText)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun showPlaceholderMessage(text: String, imageRes: Int, refreshButton: Boolean) =
        with(binding) {
            clearResults()
            placeholderMessage.text = text
            placeholderMessage.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE

            placeholderImageInternet.visibility = if (imageRes == R.drawable.internet_error) View.VISIBLE else View.GONE
            placeholderImageMusic.visibility = if (imageRes != R.drawable.internet_error) View.VISIBLE else View.GONE

            placeholderButton.visibility = if (refreshButton) View.VISIBLE else View.GONE
            if (imageRes == R.drawable.internet_error) {
                placeholderButton.visibility = View.VISIBLE
            }
        }

    private fun clearResults() {
        searchAdapter.items = emptyList()
        searchAdapter.notifyDataSetChanged()
    }

    private fun hidePlaceholderMessage() {
        binding.placeholderMessage.visibility = View.GONE
        binding.placeholderImageMusic.visibility = View.GONE
        binding.placeholderImageInternet.visibility = View.GONE
        binding.placeholderButton.visibility = View.GONE
    }

    companion object {
        const val EDIT_TEXT_KEY = "SOMETHING_TEXT"
        const val SHARED_PREFS = "shared_prefs"
        const val SEARCH_HISTORY = "search_history"
    }
}

