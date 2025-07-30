package com.moistlabs.statussaver.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.moistlabs.statussaver.databinding.NativeAdItemBinding
import com.moistlabs.statussaver.databinding.StatusListItemBinding
import com.moistlabs.statussaver.model.Media
import com.moistlabs.statussaver.util.Utils.CROSS_FADE_DURATION
import com.moistlabs.statussaver.util.Utils.toggleVisibility


class StatusAdapter(
    private val onClick: (Int, Media) -> Unit,
    private val onOptionsClick: (Media) -> Unit,
) : ListAdapter<Media, BaseViewHolder>(StatusDiffCallback()){

    private var adLoader: AdLoader? = null
    private val nativeAdsList = mutableListOf<NativeAd?>()

    fun loadAds(context: Context) {
        adLoader = AdLoader.Builder(context, "ca-app-pub-2502221940892901/1452869545")
            .forNativeAd { ad : NativeAd ->
                nativeAdsList.add(ad)
            }
            .build()
        adLoader?.loadAds(AdRequest.Builder().build(), ITEMS_PER_AD)
    }

    inner class StatusViewHolder(
        private val binding: StatusListItemBinding,
    ) : BaseViewHolder(binding) {
        override fun onBind(media: Media) {
            Glide.with(binding.root.context)
                .load(media.uri)
                .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
                .into(binding.ivThumbnail)
            binding.btPlayVideo.toggleVisibility(media.isVideo)
            binding.btPlayVideo.setOnClickListener {
                onClick(adapterPosition, media)
            }
            binding.btOptions.setOnClickListener {
                onOptionsClick(media)
            }
            binding.root.setOnClickListener {
                onClick(adapterPosition, media)
            }
        }
    }

    inner class AdViewHolder(
        private val binding: NativeAdItemBinding,
    ) : BaseViewHolder(binding) {
        override fun onBind(media: Media) {
            val pos = adapterPosition % ITEMS_PER_AD
            if(nativeAdsList.size > pos)
                binding.root.setNativeAd(nativeAdsList[pos])
            else
                binding.root.setNativeAd(null)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val contentBinding = StatusListItemBinding.inflate(
            inflater,
            parent,
            false
        )
        val adBinding = NativeAdItemBinding.inflate(
            inflater,
            parent,
            false
        )
        return when(viewType) {
            VIEW_TYPE_NATIVE_AD -> AdViewHolder(adBinding)
            VIEW_TYPE_REGULAR_CONTENT -> StatusViewHolder(contentBinding)
            else -> throw IllegalArgumentException("Incorrect viewtype provided")
        }

    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

    companion object {
        const val VIEW_TYPE_NATIVE_AD = 1
        const val VIEW_TYPE_REGULAR_CONTENT = 0
        const val ITEMS_PER_AD = 4
    }

}