package com.example.logiapplication.logisticOperator.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.databinding.LogisticFragmentProfileBinding

class LogisticProfileFragment : Fragment() {

    private lateinit var logisticProfileViewModel: LogisticProfileViewModel
    private var _binding: LogisticFragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logisticProfileViewModel =
            ViewModelProvider(this).get(LogisticProfileViewModel::class.java)

        _binding = LogisticFragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}