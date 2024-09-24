package com.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.playlistmaker.data.ApiClient
import com.playlistmaker.data.MusicApi
import com.playlistmaker.data.itunesdb.ResultResponse
import com.playlistmaker.databinding.ActivitySearchBinding
import com.playlistmaker.view.rv_adapter.MusicRVAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: MusicRVAdapter
    private var inputText: String = ""
    private var failedQuery: String? = null
    private val apiClient = ApiClient()
    private val musicApi = apiClient.getClient().create(MusicApi::class.java)

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

        adapter = MusicRVAdapter { track ->
            Toast.makeText(
                this@SearchActivity,
                "Clicked on track: ${track.trackName}",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        binding.rvSearch.adapter = adapter

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchEditText.text.toString()
                if (query.isNotEmpty()) {
                    searchMusic(query)
                } else {
                    Toast.makeText(this, "Введите запрос", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")
            it.visibility = View.GONE
            hideKeyboard()
            clearResults()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
                inputText = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.placeholderButton.setOnClickListener {
            val query = failedQuery
            if (query != null) searchMusic(query)
        }

        binding.placeholderButton.visibility = View.GONE
    }

    private fun searchMusic(query: String) {
        failedQuery = query
        hidePlaceholderMessage()
        val call = musicApi.getMusic(query)
        call.enqueue(object : Callback<ResultResponse> {
            override fun onResponse(
                call: Call<ResultResponse>,
                response: Response<ResultResponse>
            ) {
                if (response.isSuccessful) {
                    val resultResponse = response.body()
                    if (resultResponse != null && resultResponse.results.isNotEmpty()) {
                        adapter.items = resultResponse.results
                        adapter.notifyDataSetChanged()
                        hidePlaceholderMessage()
                    } else {
                        showPlaceholderMessage(
                            getString(R.string.nothing_was_found),
                            R.drawable.music_error
                        )
                    }
                } else {
                    showPlaceholderMessage(
                        getString(R.string.nothing_was_found),
                        R.drawable.music_error
                    )
                }
            }

            override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
                if (adapter.items.isEmpty()) {
                    showPlaceholderMessage(
                        getString(R.string.connection_problem),
                        R.drawable.internet_error
                    )
                } else {
                    showPlaceholderMessage(
                        getString(R.string.connection_problem),
                        R.drawable.internet_error
                    )
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return true
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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun showPlaceholderMessage(text: String, imageRes: Int) {
        clearResults()
        binding.placeholderMessage.text = text
        binding.placeholderMessage.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
        binding.placeholderImageInternet.visibility =
            if (imageRes == R.drawable.internet_error) View.VISIBLE else View.GONE
        binding.placeholderImageMusic.visibility =
            if (imageRes != R.drawable.internet_error) View.VISIBLE else View.GONE
        binding.placeholderButton.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE

        if (text.isNotEmpty()) {
            Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
        }
    }

    private fun clearResults() {
        adapter.items = emptyList()
        adapter.notifyDataSetChanged()
    }

    private fun hidePlaceholderMessage() {
        binding.placeholderMessage.visibility = View.GONE
        binding.placeholderImageMusic.visibility = View.GONE
        binding.placeholderImageInternet.visibility = View.GONE
        binding.placeholderButton.visibility = View.GONE
    }

    companion object {
        const val EDIT_TEXT_KEY = "SOMETHING_TEXT"
    }
}
