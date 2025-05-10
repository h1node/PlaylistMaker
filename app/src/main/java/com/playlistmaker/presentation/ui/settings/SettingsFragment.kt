package com.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.playlistmaker.R
import com.playlistmaker.databinding.FragmentSettingsBinding
import com.playlistmaker.domain.repositories.ThemeRepository
import org.koin.android.ext.android.inject


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val themeRepository: ThemeRepository by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchButton.isChecked = themeRepository.darkTheme
        binding.switchButton.setOnCheckedChangeListener { _, checked ->
            themeRepository.darkTheme = checked
        }

        configureEmailButton()
        configureShareButton()
        configureUserAgreement()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun configureEmailButton() {
        binding.support.setOnClickListener {
            val mailSubject = getString(R.string.mail_subject)
            val mailText = getString(R.string.mail_text)

            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.you_mail)))
                putExtra(Intent.EXTRA_SUBJECT, mailSubject)
                putExtra(Intent.EXTRA_TEXT, mailText)
            }
            startActivity(supportIntent)
        }
    }

    private fun configureShareButton() {
        binding.share.setOnClickListener {
            val url = getString(R.string.yp_url)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
            startActivity(shareIntent)
        }
    }

    private fun configureUserAgreement() {
        binding.userAgreement.setOnClickListener {
            val ypOfferUrl = getString(R.string.yp_offer)
            val agreementIntent = Intent(Intent.ACTION_VIEW, ypOfferUrl.toUri())
            startActivity(agreementIntent)
        }
    }
}
