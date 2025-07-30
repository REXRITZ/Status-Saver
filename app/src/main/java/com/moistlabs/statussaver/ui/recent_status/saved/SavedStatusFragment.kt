package com.moistlabs.statussaver.ui.recent_status.saved

import android.app.Activity.RESULT_OK
import android.app.RecoverableSecurityException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.moistlabs.statussaver.R
import com.moistlabs.statussaver.databinding.FragmentSavedStatusBinding
import com.moistlabs.statussaver.databinding.OptionsBottomSheetBinding
import com.moistlabs.statussaver.model.Media
import com.moistlabs.statussaver.ui.adapters.StatusAdapter
import com.moistlabs.statussaver.ui.recent_status.videos.VideoPlayerActivity
import com.moistlabs.statussaver.util.Utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SavedStatusFragment : Fragment() {

    private var _binding: FragmentSavedStatusBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SavedStatusViewModel by viewModels()
    private var deletedMedia: Media? = null
    private val adapter = StatusAdapter(
        onClick = { pos, media ->
            if(media.isVideo) {
                val intent = Intent(activity, VideoPlayerActivity::class.java)
                intent.putExtra("video_uri", media.uri.toString())
                startActivity(intent)
            } else {
                val bundle = Bundle().apply {
                    putInt("startPos", pos)
                    putBoolean("from_saved_fragment", true)
                    putParcelableArrayList("images", ArrayList(viewModel.getImages()))
                }
                findNavController().navigate(R.id.action_navigation_recent_status_to_galleryFragment,bundle)
            }
        },
        onOptionsClick = { media ->
            showOptionsBottomSheet(media)
        }
    )
    private val intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        if(it.resultCode == RESULT_OK) {
            deletedMedia?.let {
                viewModel.updateList(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSavedStatusBinding.inflate(inflater)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvStatuses.apply {
            layoutManager = gridLayoutManager
        }
        binding.rvStatuses.adapter = adapter
        viewModel.statuses.observe(viewLifecycleOwner) { data->
            adapter.submitList(data.toList())
            binding.noStatusLayout.root.toggleVisibility(data.isEmpty())
        }
        viewModel.loading.observe(viewLifecycleOwner) { isLoading->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) { message->
            if(message != -1) {
                Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
                viewModel.resetToastState()
            }
        }
        return binding.root
    }

    private fun showOptionsBottomSheet(media: Media) {
        val view = OptionsBottomSheetBinding.inflate(layoutInflater)
        view.btSave.toggleVisibility(false)
        val optionsDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(view.root)
        }
        optionsDialog.show()
        view.btRepost.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, media.uri)
                setPackage("com.whatsapp")
            }
            startActivity(intent)
            optionsDialog.dismiss()
        }
        view.btShare.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, media.uri)
            }
            startActivity(Intent.createChooser(intent, null))
            optionsDialog.dismiss()
        }
        view.btDelete.setOnClickListener {
            lifecycleScope.launch {
                deleteStatus(media)
                deletedMedia = media
            }
            optionsDialog.dismiss()
        }
    }

    private suspend fun deleteStatus(media: Media) {
        withContext(Dispatchers.IO) {
            try {
                requireContext().contentResolver.delete(media.uri, null, null)
                deletedMedia?.let {
                    viewModel.updateList(it)
                }
            } catch (e : Exception) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(requireContext().contentResolver, listOf(media.uri)).intentSender
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverableException = e as? RecoverableSecurityException
                        recoverableException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }
                intentSender?.let {
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(it).build()
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getAllSavedStatus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}