package com.example.logiapplication.logisticOperator.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.databinding.LogisticFragmentReportingBinding

class ReportingFragment : Fragment() {
    private lateinit var reportingViewModel: ReportingViewModel
    private var _binding: LogisticFragmentReportingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reportingViewModel =
            ViewModelProvider(this).get(ReportingViewModel::class.java)

        _binding = LogisticFragmentReportingBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}