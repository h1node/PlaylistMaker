package com.playlistmaker.presentation.ui.media.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.playlistmaker.R
import java.io.File


class EditPlaylistFragment : CreatePlayListFragment() {

    private val playlist by lazy {
        EditPlaylistFragmentArgs.fromBundle(requireArguments()).playlist
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.edit_playlist)
        binding.btnCreate.text = getString(R.string.save)

        viewModel.onNameChanged(playlist.name)
        viewModel.onDescriptionChanged(playlist.description)
        playlist.coverUri?.let { filename ->
            val file = File(requireContext().filesDir, filename)
            if (file.exists()) {
                viewModel.onCoverUriChanged(Uri.fromFile(file))
            }
        }

        binding.btnCreate.setOnClickListener {
            saveChanges()
        }
        setupBackPressedHandler()
    }

    private fun saveChanges() {
        val name = viewModel.name.value
        val desc = viewModel.description.value
        val uri = viewModel.coverUri.value

        val coverFileName = if (uri != null) {
            if (uri.scheme == "file") {
                playlist.coverUri
            } else {
                val fileName = "${System.currentTimeMillis()}_${name}.jpg"
                copyUriToInternalStorage(uri, fileName)
                fileName
            }
        } else {
            null
        }

        val updatedPlaylist = playlist.copy(
            name = name,
            description = desc,
            coverUri = coverFileName
        )

        viewModel.savePlaylist(updatedPlaylist)
        findNavController().popBackStack()
    }

    private fun setupBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            isEnabled = false
            findNavController().popBackStack()
        }
    }
}