package com.moistlabs.statussaver.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moistlabs.statussaver.MainActivity
import com.moistlabs.statussaver.R
import com.moistlabs.statussaver.data.AppPref
import com.moistlabs.statussaver.databinding.FragmentSettingsBinding
import com.moistlabs.statussaver.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var languages: Array<String>
    private lateinit var modes: Array<String>
    @Inject
    lateinit var appPref: AppPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater)
        languages = arrayOf(getString(R.string.english) , getString(R.string.french), getString(
            R.string.german), getString(R.string.japanese), getString(
            R.string.korean), getString(R.string.portuguese), getString(R.string.russian), getString(R.string.spanish))
        modes = arrayOf(getString(R.string.system_default), getString(R.string.light), getString(R.string.dark))
        updateUI()
        return binding.root
    }

    private fun updateUI() {
        binding.run {
            tvSelectedLang.text = getString(viewModel.selectedLanguage)
            tvSelectedMode.text = getString(viewModel.mode)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.run {
            btLanguage.setOnClickListener {
                openLanguagePickerDialog()
            }
            btRate.setOnClickListener {
                startRateIntent()
            }
            btShare.setOnClickListener {
                startShareIntent()
            }
            btPolicy.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse("https://moistlabs.blogspot.com/2021/07/moist-labs.html")
                }
                startActivity(intent)
            }
            btHelp.setOnClickListener {
                openHelpDialog()
            }
            btMode.setOnClickListener {
                openModePickerDialog()
            }
        }
    }

    private fun openHelpDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.steps_to_use))
            .setView(R.layout.help_dialog)
            .show()
    }

    private fun startShareIntent() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, Utils.APP_LINK)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun startRateIntent() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + Utils.APP_ID)
                )
            )
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + Utils.APP_ID)
                )
            )
        }
    }

    private fun openModePickerDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setSingleChoiceItems(modes, appPref.getMode()
            ) { dialog, pos ->
                setTheme(pos)
                viewModel.setTheme(pos)
                updateUI()
                dialog.dismiss()
            }
            .show()
    }

    private fun openLanguagePickerDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setSingleChoiceItems(languages, appPref.getLanguage()
            ) { dialog, pos ->
                viewModel.setLanguage(pos)
                updateUI()
                (activity as MainActivity).updateLocale(Locale.forLanguageTag(Utils.languageMap[pos]!!.first))
                dialog.dismiss()
            }
            .show()
    }

    private fun setTheme(pos: Int) {
        AppCompatDelegate.setDefaultNightMode(
            Utils.getAppThemeMode(pos)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}