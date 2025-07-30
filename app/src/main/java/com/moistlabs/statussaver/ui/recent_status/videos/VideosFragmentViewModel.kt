package com.moistlabs.statussaver.ui.recent_status.videos

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moistlabs.statussaver.R
import com.moistlabs.statussaver.data.DataSource
import com.moistlabs.statussaver.model.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VideosFragmentViewModel @Inject constructor(
    private val dataSource: DataSource
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    private val _videos = MutableLiveData<List<Media>>()
    val videos: LiveData<List<Media>> = _videos
    private val _toastMessage = MutableLiveData(-1)
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        getAllStatus()
    }

    fun getAllStatus() {
        if (_loading.value == true) return
        _loading.value = true
        viewModelScope.launch {
            val videoStatus = withContext(Dispatchers.Default) {
                dataSource.getStatus(false)
            }
            _videos.postValue(videoStatus)
            _loading.postValue(false)
        }
    }

    fun saveStatus(uri: Uri, fileName: String) {
        viewModelScope.launch {
            dataSource.saveStatus(uri, fileName)
            _toastMessage.postValue(R.string.status_saved_message)
        }
    }

    fun resetToastState() {
        _toastMessage.value = -1
    }
}