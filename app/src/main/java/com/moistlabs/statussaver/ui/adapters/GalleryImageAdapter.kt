package com.moistlabs.statussaver.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.moistlabs.statussaver.databinding.GalleryAdItemBinding
import com.moistlabs.statussaver.databinding.GalleryListItemBinding
import com.moistlabs.statussaver.model.Media

class GalleryImageAdapter : ListAdapter<Media, BaseViewHolder>(StatusDiffCallback()){

    private var adLoader: AdLoader? = null
    private val nativeAdsList = mutableListOf<NativeAd?>()

    fun loadAds(context: Context) {
        adLoader = AdLoader.Builder(context, "ca-app-pub-2502221940892901/3887461193")
            .forNativeAd { ad : NativeAd ->
                nativeAdsList.add(ad)
            }
            .build()
        adLoader?.loadAds(AdRequest.Builder().build(), ITEMS_PER_AD)
    }

    inner class GalleryImageViewHolder(
        private val binding: GalleryListItemBinding
    ) : BaseViewHolder(binding) {
        override fun onBind(media: Media) {
            Glide.with(binding.root.context)
                .load(media.uri)
                .into(binding.ivImage)
        }
    }

    inner class AdViewHolder(
        private val binding: GalleryAdItemBinding,
    ) : BaseViewHolder(binding) {
        override fun onBind(media: Media) {
            val pos = adapterPosition % ITEMS_PER_AD
            if(nativeAdsList.size > pos)
                binding.root.setNativeAd(nativeAdsList[pos])
            Log.d("Ad pos", adapterPosition.toString())
            Log.d("ads loaded", nativeAdsList.size.toString())
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val contentBinding = GalleryListItemBinding.inflate(
            inflater,
            parent,
            false
        )
        val adBinding = GalleryAdItemBinding.inflate(
            inflater,
            parent,
            false
        )
        return when(viewType) {
            VIEW_TYPE_NATIVE_AD -> AdViewHolder(adBinding)
            VIEW_TYPE_REGULAR_CONTENT -> GalleryImageViewHolder(contentBinding)
            else -> throw IllegalArgumentException("Incorrect viewtype provided")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

    fun destroyAds() {
        for (ad in nativeAdsList) {
            ad?.destroy()
        }
    }

    companion object {
        const val VIEW_TYPE_NATIVE_AD = 1
        const val VIEW_TYPE_REGULAR_CONTENT = 0
        const val ITEMS_PER_AD = 4
    }
}