package com.playlistmaker


import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

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

        val shareButton = findViewById<TextView>(R.id.share)
        shareButton.setOnClickListener {
            val url = getString(R.string.yp_url)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
            startActivity(shareIntent)
        }

        val userAgreementButton = findViewById<TextView>(R.id.user_agreement)
        userAgreementButton.setOnClickListener {
            val ypOfferUrl = getString(R.string.yp_offer)
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
}
