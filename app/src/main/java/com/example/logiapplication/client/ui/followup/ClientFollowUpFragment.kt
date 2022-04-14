package com.example.logiapplication.client.ui.followup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.client.ui.profile.ClientProfileViewModel
import com.example.logiapplication.databinding.ClientFragmentFollowupBinding
import com.example.logiapplication.databinding.ClientFragmentProfileBinding

class ClientFollowUpFragment:Fragment() {

    private lateinit var clientFollowUpViewModel: ClientFollowUpViewModel
    private var _binding: ClientFragmentFollowupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clientFollowUpViewModel =
            ViewModelProvider(this).get(ClientFollowUpViewModel::class.java)

        _binding = ClientFragmentFollowupBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}