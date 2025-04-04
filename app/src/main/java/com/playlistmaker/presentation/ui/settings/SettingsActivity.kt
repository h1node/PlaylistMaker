package com.playlistmaker.presentation.ui.settings


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.playlistmaker.R
import com.playlistmaker.databinding.ActivitySettingsBinding
import com.playlistmaker.domain.repositories.ThemeRepository
import org.koin.android.ext.android.inject


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val themeRepository: ThemeRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        binding.switchButton.isChecked = themeRepository.darkTheme
        binding.switchButton.setOnCheckedChangeListener { _, checked ->
            themeRepository.darkTheme = checked
        }

        setupWindowInsets()
        configureEmailButton()
        configureShareButton()
        configureUserAgreement()

    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return true
    }

    private fun configureEmailButton() {
        val supportButton = findViewById<TextView>(R.id.support)
        supportButton.setOnClickListener {
            val mailSubject = getString(R.string.mail_subject)
            val mailText = getString(R.string.mail_text)

            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.you_mail))
                putExtra(Intent.EXTRA_SUBJECT, mailSubject)
                putExtra(Intent.EXTRA_TEXT, mailText)
            }
            startActivity(supportIntent)
        }
    }

    private fun configureShareButton() {
        val shareButton = findViewById<TextView>(R.id.share)
        shareButton.setOnClickListener {
            val url = getString(R.string.yp_url)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
            startActivity(shareIntent)
        }
    }

    private fun configureUserAgreement() {
        val userAgreementButton = findViewById<TextView>(R.id.user_agreement)
        userAgreementButton.setOnClickListener {
            val ypOfferUrl = getString(R.string.yp_offer)
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ypOfferUrl))
            startActivity(agreementIntent)
        }
    }
}
