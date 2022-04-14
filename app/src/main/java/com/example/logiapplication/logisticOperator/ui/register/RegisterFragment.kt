package com.example.logiapplication.logisticOperator.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.R
import com.example.logiapplication.carrier.ui.syncup.ConnectionActivity
import com.example.logiapplication.databinding.LogisticFragmentRegisterBinding

class RegisterFragment : Fragment() {
    private lateinit var registerViewModel: RegisterViewModel
    private var _binding: LogisticFragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var nextButton : Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerViewModel =
            ViewModelProvider(this).get(RegisterViewModel::class.java)

        _binding = LogisticFragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        nextButton = binding.root.findViewById(R.id.btn_next_register)

        nextButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), NextRegisterActivity::class.java)
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