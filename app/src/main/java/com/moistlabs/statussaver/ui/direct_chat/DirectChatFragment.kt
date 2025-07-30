package com.moistlabs.statussaver.ui.direct_chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moistlabs.statussaver.databinding.FragmentDirectChatBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder

@AndroidEntryPoint
class DirectChatFragment : Fragment() {

    private var _binding: FragmentDirectChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DirectChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDirectChatBinding.inflate(inflater)
        viewModel.uiToggle.observe(viewLifecycleOwner) {
            updateUI()
        }
        return binding.root
    }

    private fun updateUI() {
        binding.run {
            etCountryCode.editText?.setText(viewModel.getCountryCodeWithName())
            etPhone.editText?.setText(viewModel.number)
            etMessage.editText?.setText(viewModel.message)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.run {
            etCountryCode.editText!!.setOnClickListener {
                val dioalg = CountryPickerDialog()
                dioalg.show(childFragmentManager, "Country picker")
            }
            btSend.setOnClickListener {
                val phoneNumber = viewModel.getPhoneNumber()
                val message = viewModel.message
                if(phoneNumber.isNotEmpty() && message.isNotEmpty()) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_VIEW
                        setPackage("com.whatsapp")
                        data = Uri.parse(
                            "https://wa.me/$phoneNumber?text=${URLEncoder.encode(message,"UTF-8")}"
                        )
                    }
                    startActivity(intent)
                }
            }
            etPhone.editText?.addTextChangedListener {
                viewModel.number = it.toString()
            }
            etMessage.editText?.addTextChangedListener {
                viewModel.message = it.toString()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}