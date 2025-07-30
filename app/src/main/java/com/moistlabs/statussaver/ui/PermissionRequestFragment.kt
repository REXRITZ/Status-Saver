package com.moistlabs.statussaver.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.moistlabs.statussaver.R
import com.moistlabs.statussaver.data.AppPref
import com.moistlabs.statussaver.databinding.PermissionBottomSheetBinding
import com.moistlabs.statussaver.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PermissionRequestFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var appPref: AppPref
    private var _binding: PermissionBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val permissionLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val flag = result.data!!.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                val uri = result.data!!.data!!
                requireContext().contentResolver.takePersistableUriPermission(uri, flag)
                appPref.setPath(uri.toString())
                dismiss()
            }
        }

    private val normalPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var isGranted = true
        for(permission in permissions) {
            isGranted = isGranted and permission.value
        }
        if (isGranted) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                launchFolderAccessPermissionFlow()
            } else {
                dismiss()
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.permission_message), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PermissionBottomSheetBinding.inflate(inflater)
        dialog?.setCanceledOnTouchOutside(false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.run {
            btAllow.setOnClickListener {
                requestPermission()
            }
            btCancel.setOnClickListener {
                activity?.finish()
            }
        }
    }

    private fun requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launchFolderAccessPermissionFlow()
            } else {
                normalPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        } else {
            normalPermissionLauncher.launch(permissions)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun launchFolderAccessPermissionFlow() {
        val intent =
            (requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager?)!!.primaryStorageVolume.createOpenDocumentTreeIntent()
        val uri = DocumentsContract.buildDocumentUri(
            "com.android.externalstorage.documents",
            "primary:" + Utils.WHATSAPP_PATH
        )
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        permissionLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}