package com.example.logiapplication.client.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.databinding.ClientFragmentProfileBinding
import com.example.logiapplication.databinding.LogisticFragmentProfileBinding
import com.example.logiapplication.logisticOperator.ui.profile.LogisticProfileViewModel

class ClientProfileFragment : Fragment() {

    private lateinit var clientProfileViewModel: ClientProfileViewModel
    private var _binding: ClientFragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clientProfileViewModel =
            ViewModelProvider(this).get(ClientProfileViewModel::class.java)

        _binding = ClientFragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}