package com.moistlabs.statussaver.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.moistlabs.statussaver.model.Media

class StatusDiffCallback : DiffUtil.ItemCallback<Media>() {
    override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
        return oldItem.uri == newItem.uri && oldItem.type == newItem.type
    }
}