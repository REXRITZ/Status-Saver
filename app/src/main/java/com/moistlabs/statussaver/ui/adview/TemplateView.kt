package com.moistlabs.statussaver.ui.adview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.moistlabs.statussaver.R
import com.moistlabs.statussaver.util.Utils.toggleVisibility

class TemplateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private var templateType = 0
    private var nativeAd: NativeAd? = null
    private var nativeAdView: NativeAdView? = null
    private var advertiserView: TextView? = null
    private var titleView: TextView? = null
    private var bodyView: TextView? = null
    private var ratingBar: RatingBar? = null
    private var mediaView: MediaView? = null
    private var callToActionView: Button? = null
    private var adContent: ConstraintLayout? = null
    private var adEmptyContent: LinearLayout? = null

    init {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.TemplateView, 0, 0)
        templateType = try {
            attributes.getResourceId(
                R.styleable.TemplateView_gnt_template_type, R.layout.main_feed_ad_template
            )
        } finally {
            attributes.recycle()
        }
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(templateType, this)
    }

    fun setNativeAd(nativeAd: NativeAd?) {
        this.nativeAd = nativeAd
        if (nativeAd == null) {
            adEmptyContent!!.toggleVisibility(true)
            adContent!!.visibility = GONE
            return
        }
        adContent!!.visibility = VISIBLE
        adEmptyContent!!.visibility = GONE

        val advertiser = nativeAd.advertiser
        val headline = nativeAd.headline
        val body = nativeAd.body
        val cta = nativeAd.callToAction
        val starRating = nativeAd.starRating
        nativeAdView!!.callToActionView = callToActionView
        nativeAdView!!.headlineView = titleView
        nativeAdView!!.bodyView = bodyView
        nativeAdView!!.mediaView = mediaView
        titleView!!.text = headline
        callToActionView!!.text = cta
        bodyView!!.text = body
        if (advertiser != null && templateType == R.layout.gallery_feed_ad_template) {
            nativeAdView!!.advertiserView = advertiserView
            advertiserView!!.text = advertiser
        }
        if (starRating != null && starRating > 0) {
            ratingBar!!.visibility = VISIBLE
            ratingBar!!.rating = starRating.toFloat()
            nativeAdView!!.starRatingView = ratingBar
        } else {
            ratingBar!!.visibility = GONE
        }
        if (nativeAd.mediaContent != null) {
            mediaView!!.visibility = VISIBLE
            mediaView!!.mediaContent = nativeAd.mediaContent
        } else {
            mediaView!!.visibility = GONE
        }
        nativeAdView!!.setNativeAd(nativeAd)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        adContent = findViewById(R.id.ad_content)
        adEmptyContent = findViewById(R.id.ad_empty)
        nativeAdView = findViewById(R.id.native_ad_view)
        titleView = findViewById(R.id.title)
        bodyView = findViewById(R.id.body)

        ratingBar = findViewById(R.id.rating_bar)
        ratingBar?.setEnabled(false)

        callToActionView = findViewById(R.id.cta)
        mediaView = findViewById(R.id.media_view)
        if (templateType == R.layout.main_feed_ad_template) {
            mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        }
        if (templateType == R.layout.gallery_feed_ad_template) {
            advertiserView = findViewById(R.id.advertiser)
        }
    }
}