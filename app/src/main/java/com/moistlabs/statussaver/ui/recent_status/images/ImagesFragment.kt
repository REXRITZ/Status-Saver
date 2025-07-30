package com.moistlabs.statussaver.ui.recent_status.images

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.moistlabs.statussaver.R
import com.moistlabs.statussaver.databinding.FragmentImagesBinding
import com.moistlabs.statussaver.databinding.OptionsBottomSheetBinding
import com.moistlabs.statussaver.ui.adapters.StatusAdapter
import com.moistlabs.statussaver.util.Utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImagesFragment : Fragment() {

    private var _binding: FragmentImagesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ImagesFragmentViewModel by activityViewModels()
    private val adapter = StatusAdapter(
        onClick = { pos, _ ->
            val bundle = Bundle().apply {
                putInt("startPos", pos)
                putBoolean("from_saved_fragment", false)
                putParcelableArrayList("images", ArrayList(viewModel.images.value!!))
            }
            findNavController().navigate(R.id.action_navigation_recent_status_to_galleryFragment,bundle)
        },
        onOptionsClick = { media ->
            showOptionsBottomSheet(media.uri, media.fileName)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentImagesBinding.inflate(inflater)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvImages.apply {
            layoutManager = gridLayoutManager
        }
        binding.rvImages.adapter = adapter
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(adapter.getItemViewType(position)) {
                    StatusAdapter.VIEW_TYPE_NATIVE_AD -> 2
                    else -> 1
                }
            }
        }
        viewModel.images.observe(viewLifecycleOwner) { data->
            adapter.submitList(data)
            binding.noStatusLayout.root.toggleVisibility(data.isEmpty())
            if(data.isNotEmpty() && data.size > 4) {
                adapter.loadAds(requireContext())
            }
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
                type = "image/*"
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
                type = "image/*"
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
