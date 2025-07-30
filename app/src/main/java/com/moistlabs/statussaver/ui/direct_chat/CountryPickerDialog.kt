package com.moistlabs.statussaver.ui.direct_chat

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moistlabs.statussaver.databinding.CountrySelectDialogBinding
import com.moistlabs.statussaver.model.Country
import com.moistlabs.statussaver.ui.adapters.CountryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryPickerDialog : DialogFragment() {

    private var _binding: CountrySelectDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DirectChatViewModel by viewModels(ownerProducer = {requireParentFragment()})
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            adapter.filter(s.toString())
        }
    }
    val adapter = CountryAdapter(
        onClick = { country ->
            viewModel.country = country
            viewModel.uiToggle.value = true
            dismiss()
        },
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = CountrySelectDialogBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext()).run {
            setView(binding.root)
            create()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.rvCountries.adapter = adapter
        adapter.submitList(Country.countries)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.etSearchView.addTextChangedListener(textWatcher)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.etSearchView.removeTextChangedListener(textWatcher)
        _binding = null
    }
}