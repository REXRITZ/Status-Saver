package com.moistlabs.statussaver.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.moistlabs.statussaver.model.Media

abstract class BaseViewHolder(
    binding: ViewBinding,
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun onBind(media: Media)
}