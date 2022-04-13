package com.example.logiapplication.logisticOperator.ui.followup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.databinding.LogisticFragmentFollowupBinding

class FollowUpFragment : Fragment() {
    private lateinit var followUpViewModel: FollowUpViewModel
    private var _binding: LogisticFragmentFollowupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        followUpViewModel =
            ViewModelProvider(this).get(FollowUpViewModel::class.java)

        _binding = LogisticFragmentFollowupBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}