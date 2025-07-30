package com.moistlabs.statussaver.ui.recent_status

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.moistlabs.statussaver.R
import com.moistlabs.statussaver.data.AppPref
import com.moistlabs.statussaver.databinding.FragmentRecentStatusBinding
import com.moistlabs.statussaver.ui.adapters.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


@AndroidEntryPoint
class RecentStatusFragment : Fragment() {

    private var _binding: FragmentRecentStatusBinding? = null
    private val binding get() = _binding!!
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var tabItems: Array<String>
    @Inject
    lateinit var appPref: AppPref
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecentStatusBinding.inflate(inflater)
        tabItems = arrayOf(getString(R.string.tab_images), getString(R.string.tab_videos), getString(
            R.string.tab_saved))
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewpager.adapter = adapter
        TabLayoutMediator(binding.tabLayoutStatus, binding.viewpager) { tab, position ->
            tab.text = tabItems[position]
        }.attach()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handlePermissions()
    }

    private fun handlePermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if(appPref.getPath().toString().isEmpty()) {
                    findNavController().navigate(R.id.action_navigation_recentStatus_to_permissionRequestFragment)
                }
            } else if(!EasyPermissions.hasPermissions(requireContext(),permissions[0])) {
                findNavController().navigate(R.id.action_navigation_recentStatus_to_permissionRequestFragment)
            }
        }
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !EasyPermissions.hasPermissions(requireContext(), *permissions))
            findNavController().navigate(R.id.action_navigation_recentStatus_to_permissionRequestFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}