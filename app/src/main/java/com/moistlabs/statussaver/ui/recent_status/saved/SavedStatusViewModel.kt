package com.moistlabs.statussaver.ui.recent_status.saved

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moistlabs.statussaver.R
import com.moistlabs.statussaver.data.DataSource
import com.moistlabs.statussaver.model.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedStatusViewModel @Inject constructor(
    private val dataSource: DataSource
): ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    private val _statuses = MutableLiveData<List<Media>>()
    val statuses: LiveData<List<Media>> = _statuses
    private val _toastMessage = MutableLiveData(-1)
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        getAllSavedStatus()
    }

    fun getAllSavedStatus() {
        if (_loading.value == true) return
        _loading.value = true
        viewModelScope.launch {
            val statuses = dataSource.getSavedStatus()
            _statuses.postValue(statuses)
            _loading.postValue(false)
        }
    }

    fun resetToastState() {
        _toastMessage.value = -1
    }

    fun updateList(media: Media) {
        val medias = statuses.value!! as MutableList
        medias.remove(media)
        _statuses.value = medias
        _toastMessage.value = R.string.status_deleted_message
    }

    fun getImages(): List<Media> {
        val images = statuses.value!!.filter {
            !it.isVideo
        }
        return images
    }
}