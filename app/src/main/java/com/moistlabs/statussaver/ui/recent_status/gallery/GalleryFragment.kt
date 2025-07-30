package com.moistlabs.statussaver.ui.recent_status.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moistlabs.statussaver.R
import com.moistlabs.statussaver.databinding.FragmentGalleryBinding
import com.moistlabs.statussaver.ui.adapters.GalleryImageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GalleryViewModel by viewModels()
    private var startPos: Int = -1
    private val adapter = GalleryImageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater)
        setHasOptionsMenu(true)
        startPos = arguments?.getInt("startPos",-1) ?: -1
        binding.rvImages.adapter = adapter
        adapter.submitList(viewModel._images)
        if (viewModel._images.size >= 4) {
            adapter.loadAds(requireContext())
        }
        if(startPos != -1) {
            binding.rvImages.setCurrentItem(startPos, false)
            startPos = -1
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.resetToastState()
            }
        }
        return binding.root
    }

    private fun startShareIntent(packageName: String) {
        val uri = viewModel.getStatusAt(binding.rvImages.currentItem).uri
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            if (packageName.isNotEmpty()) setPackage(packageName)
        }
        startActivity(Intent.createChooser(intent, null))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.repost -> {
                startShareIntent("com.whatsapp")
            }
            R.id.share -> {
                startShareIntent("")
            }
            R.id.save -> {
                viewModel.saveStatus(binding.rvImages.currentItem)
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.gallery_menu, menu)
        menu.getItem(2).isVisible = !viewModel.fromSavedFragment
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.destroyAds()
        _binding = null
    }
}