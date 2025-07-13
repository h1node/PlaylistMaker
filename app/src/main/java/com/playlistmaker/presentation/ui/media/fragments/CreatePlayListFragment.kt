package com.playlistmaker.presentation.ui.media.fragments


import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.playlistmaker.R
import com.playlistmaker.databinding.FragmentCreatePlayListBinding
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.presentation.ui.media.viewmodel.CreatePlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


open class CreatePlayListFragment : Fragment() {

    private var _binding: FragmentCreatePlayListBinding? = null
    protected val binding get() = _binding!!

    protected val viewModel: CreatePlaylistViewModel by viewModel()
    private var selectedCoverUri: Uri? = null

    private val pickMediaLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.onCoverUriChanged(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initUi()
        setupBackPressedHandler()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.name.collect { name ->
                if (binding.playlistName.text.toString() != name) {
                    binding.playlistName.setText(name)
                }
                binding.btnCreate.isEnabled = name.isNotBlank()
                binding.tilPlaylistName.isActivated = name.isNotBlank()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.description.collect { desc ->
                val text = desc.orEmpty()
                if (binding.playlistDescription.text.toString() != text) {
                    binding.playlistDescription.setText(text)
                }
                binding.tilPlaylistDescription.isActivated = text.isNotBlank()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.coverUri.collect { uri ->
                selectedCoverUri = uri
                if (uri != null) {
                    binding.playlistCoverImage.setImageURI(uri)
                    binding.playlistCoverImage.visibility = View.VISIBLE
                    binding.addPhotoIcon.visibility = View.GONE
                } else {
                    binding.playlistCoverImage.visibility = View.GONE
                    binding.addPhotoIcon.visibility = View.VISIBLE
                }
            }
        }

        binding.playlistName.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChanged(text.toString())
            binding.tilPlaylistName.isActivated = !text.isNullOrBlank()
        }

        binding.playlistDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionChanged(text?.toString()?.takeIf { it.isNotBlank() })
            binding.tilPlaylistDescription.isActivated = !text.isNullOrBlank()
        }


        binding.coverPlaceholder.setOnClickListener {
            pickMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnCreate.setOnClickListener { createPlaylist() }
    }

    private fun createPlaylist() {
        val name = viewModel.name.value
        val desc = viewModel.description.value
        val uri = viewModel.coverUri.value
        val fileName = uri?.let { "${System.currentTimeMillis()}_${name}.jpg" }
        fileName?.let { copyUriToInternalStorage(uri, it) }

        val playlist = Playlist(
            id = 0L,
            name = name,
            description = desc,
            coverUri = fileName
        )
        viewModel.savePlaylist(playlist)

        Toast.makeText(requireContext(), "Плейлист '$name' создан", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun setupBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val emptyName = viewModel.name.value.isBlank()
                    val emptyDesc = viewModel.description.value.isNullOrBlank()
                    val emptyCover = viewModel.coverUri.value == null

                    if (emptyName && emptyDesc && emptyCover) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    } else {
                        val dialog = AlertDialog.Builder(requireContext())
                            .setTitle(getString(R.string.alert_create_playlist))
                            .setMessage(getString(R.string.alert_data_will_be_lost))
                            .setNegativeButton(getString(R.string.Cancel), null)
                            .setPositiveButton(getString(R.string.Complete)) { _, _ ->
                                isEnabled = false
                                requireActivity().onBackPressed()
                            }
                            .show()

                        val color =
                            ContextCompat.getColor(requireContext(), R.color.dialog_button_color)
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(color)
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(color)
                    }
                }
            }
        )
    }

    protected fun copyUriToInternalStorage(uri: Uri?, fileName: String) {
        uri ?: return
        try {
            val targetDir = File(requireContext().filesDir, "playlist_covers")
            if (!targetDir.exists()) targetDir.mkdirs()
            val outFile = File(targetDir, fileName)
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}