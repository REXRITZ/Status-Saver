package com.moistlabs.statussaver.ui.recent_status.videos

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import com.moistlabs.statussaver.databinding.ActivityVideoPlayerBinding
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity

class VideoPlayerActivity : LocaleAwareCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var videoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoUri = Uri.parse(intent.getStringExtra("video_uri")!!)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.setMediaController(mediaController)
        binding.videoView.requestFocus()
        binding.videoView.start()
    }
}