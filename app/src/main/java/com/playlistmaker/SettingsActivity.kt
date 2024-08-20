package com.playlistmaker


import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val supportButton = findViewById<ImageView>(R.id.supportButton)
        supportButton.setOnClickListener {
            val mailSubject = getString(R.string.mail_subject)
            val mailText = getString(R.string.mail_text)

            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("you@mail.com"))
                putExtra(Intent.EXTRA_SUBJECT, mailSubject)
                putExtra(Intent.EXTRA_TEXT, mailText)
            }
            startActivity(supportIntent)
        }

        val shareButton = findViewById<ImageView>(R.id.shareButton)
        shareButton.setOnClickListener {
            val url = YP_URL
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
            startActivity(shareIntent)
        }

        val userAgreementButton = findViewById<ImageView>(R.id.userAgreementButton)
        userAgreementButton.setOnClickListener {
            val ypOfferUrl = YP_OFFER
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ypOfferUrl))
            startActivity(agreementIntent)
        }

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val switchButton = findViewById<SwitchCompat>(R.id.switch_button)
        switchButton.isChecked =
            when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }


    companion object {
        const val YP_URL = "https://practicum.yandex.ru/profile/android-developer/"
        const val YP_OFFER = "https://yandex.ru/legal/practicum_offer/"
    }
}
