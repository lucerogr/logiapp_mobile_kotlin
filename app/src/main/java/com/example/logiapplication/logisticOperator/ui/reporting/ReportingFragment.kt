package com.example.logiapplication.logisticOperator.ui.reporting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.R
import com.example.logiapplication.databinding.LogisticFragmentReportingBinding
import com.example.logiapplication.logisticOperator.ui.register.NextRegisterActivity

class ReportingFragment : Fragment() {
    private lateinit var reportingViewModel: ReportingViewModel
    private var _binding: LogisticFragmentReportingBinding? = null
    lateinit var conditionButton :Button
    lateinit var finalStateButton : Button
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

        conditionButton=binding.root.findViewById(R.id.btn_conditions)
        finalStateButton=binding.root.findViewById(R.id.btn_final_state)

        conditionButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), ConditionsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        })

        finalStateButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), FinalStateActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}