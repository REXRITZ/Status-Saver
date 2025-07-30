package com.moistlabs.statussaver.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.moistlabs.statussaver.ui.recent_status.images.ImagesFragment
import com.moistlabs.statussaver.ui.recent_status.saved.SavedStatusFragment
import com.moistlabs.statussaver.ui.recent_status.videos.VideosFragment

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = listOf(
        ImagesFragment(),
        VideosFragment(),
        SavedStatusFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}