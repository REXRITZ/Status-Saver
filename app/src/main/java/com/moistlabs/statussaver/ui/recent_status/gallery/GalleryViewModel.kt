package com.moistlabs.statussaver.ui.recent_status.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moistlabs.statussaver.data.DataSource
import com.moistlabs.statussaver.model.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val dataSource: DataSource,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val _images = mutableListOf<Media>()

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> = _toastMessage

    val fromSavedFragment: Boolean

    init {
        _images.addAll(savedStateHandle.get<ArrayList<Media>>("images")!!)
        fromSavedFragment = savedStateHandle.get("from_saved_fragment") ?: false
        println(fromSavedFragment)
    }

    fun saveStatus(pos: Int) {
        val media = _images[pos]
        viewModelScope.launch {
            dataSource.saveStatus(media.uri, media.fileName)
            _toastMessage.postValue("status saved successfully!")
        }
    }

    fun resetToastState() {
        _toastMessage.value = ""
    }

    fun getStatusAt(position: Int) = _images[position]

}