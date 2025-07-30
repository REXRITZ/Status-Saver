package com.moistlabs.statussaver.ui.recent_status.videos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.moistlabs.statussaver.databinding.FragmentVideosBinding
import com.moistlabs.statussaver.databinding.OptionsBottomSheetBinding
import com.moistlabs.statussaver.ui.adapters.StatusAdapter
import com.moistlabs.statussaver.util.Utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!
    private val adapter = StatusAdapter(
        onClick = { _, media ->
            val intent = Intent(activity, VideoPlayerActivity::class.java)
            intent.putExtra("video_uri", media.uri.toString())
            startActivity(intent)
        },
        onOptionsClick = { media ->
            showOptionsBottomSheet(media.uri, media.fileName)
        }
    )
    private val viewModel: VideosFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideosBinding.inflate(inflater)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvVideos.apply {
            layoutManager = gridLayoutManager
        }
        binding.rvVideos.adapter = adapter
        viewModel.videos.observe(viewLifecycleOwner) { data->
            adapter.submitList(data)
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

    override fun onResume() {
        super.onResume()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getAllStatus()
        }
    }

    private fun showOptionsBottomSheet(uri: Uri, fileName: String) {
        val view = OptionsBottomSheetBinding.inflate(layoutInflater)
        view.btDelete.toggleVisibility(false)
        val optionsDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(view.root)
        }
        optionsDialog.show()
        view.btRepost.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                type = "video/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                setPackage("com.whatsapp")
            }
            startActivity(intent)
            optionsDialog.dismiss()
        }
        view.btShare.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                type = "video/*"
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            startActivity(Intent.createChooser(intent, null))
            optionsDialog.dismiss()
        }
        view.btSave.setOnClickListener {
            viewModel.saveStatus(uri, fileName)
            optionsDialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}