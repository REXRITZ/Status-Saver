package com.moistlabs.statussaver.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.moistlabs.statussaver.model.AdPlaceholder
import com.moistlabs.statussaver.model.Media
import com.moistlabs.statussaver.util.Utils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject


class DataSource @Inject constructor(
    private val context: Context,
    private val appPref: AppPref,
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun getStatus(imagesOnly: Boolean) : List<Media> {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            loadStatusAbove11(imagesOnly)
        } else {
            loadStatusBelow11(imagesOnly)
        }
    }

    private suspend fun loadStatusAbove11(imagesOnly: Boolean): List<Media> = withContext(ioDispatcher){
        val statuses = mutableListOf<Media>()
        val documentUri = appPref.getPath()
        if (documentUri.toString().isEmpty()) return@withContext statuses
        val documentFile = DocumentFile.fromTreeUri(context, documentUri) ?: return@withContext statuses
        val files = withContext(Dispatchers.Default) {
            documentFile.listFiles().map {
                Media(fileName = it.name!!, uri = it.uri, isVideo = it.type == "video/mp4")
            }.filter {
                if(imagesOnly)
                    !it.fileName.contains("nomedia") && !it.isVideo
                else
                    !it.fileName.contains("nomedia") && it.isVideo
            }
        }
        val adPlaceholder = AdPlaceholder()
        var modder = 4
        for (i in files.indices) {
            statuses.add(files[i])
            if ((i + 1) % modder == 0) {
                statuses.add(adPlaceholder)
            }
            if(i > 20) modder = 10
        }
        statuses
    }

    private suspend fun loadStatusBelow11(imagesOnly: Boolean): MutableList<Media> = withContext(ioDispatcher) {
        val statuses = mutableListOf<Media>()
        val path = Utils.getStatusPath()
        val directory = File(path).listFiles() ?: return@withContext statuses
        val files = directory.map {
            val uri = FileProvider.getUriForFile(
                context,
                Utils.APP_ID + ".fileprovider",
                it
            )
            Media(fileName = it.name, uri = uri, isVideo = it.extension == "video/mp4")
        }.filter {
            if(imagesOnly)
                !it.fileName.contains("nomedia") && !it.isVideo
            else
                !it.fileName.contains("nomedia") && it.isVideo
        }
        val adPlaceholder = AdPlaceholder()
        var modder = 4
        for (i in files.indices) {
            statuses.add(files[i])
            if ((i + 1) % modder == 0) {
                statuses.add(adPlaceholder)
            }
            if(i > 20) modder = 10
        }
        statuses
    }

    suspend fun getSavedStatus(): List<Media> = withContext(ioDispatcher) {
        val statuses = mutableListOf<Media>()
        val collection = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.DATA
        )
        val selection = "${MediaStore.MediaColumns.DATA} like ?"
        val selectionArgs = arrayOf("%Pictures/Status Saver%")

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val isVideo = mimeType == "video/mp4"
                var contentUri = ContentUris.withAppendedId(
                    collection,
                    id
                ).toString()
                contentUri = if(isVideo)
                    contentUri.replace("file","video/media")
                else
                    contentUri.replace("file","images/media")

                statuses.add(Media(displayName, Uri.parse(contentUri), isVideo))
                Log.d("Media", "$displayName $isVideo $contentUri")
            }
            statuses.toList()
        } ?: listOf()
    }


    suspend fun saveStatus(sourceUri: Uri, fileName: String) = withContext(ioDispatcher) {
        val inputStream = context.contentResolver.openInputStream(sourceUri)
        val sourceChannel = (inputStream as FileInputStream).channel
        val destinationChannel = FileOutputStream(File(Utils.getSavedStatusPath(), fileName)).channel
        destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size())
        sourceChannel.close()
        destinationChannel.close()
        inputStream.close()
    }

}