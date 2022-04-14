package com.example.logiapplication.client.ui.confirm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.R
import com.example.logiapplication.databinding.ClientFragmentConfirmBinding
import com.example.logiapplication.logisticOperator.ui.register.NextRegisterActivity

class ConfirmFragment:Fragment() {
    private lateinit var confirmViewModel: ConfirmViewModel
    private var _binding: ClientFragmentConfirmBinding? = null

    lateinit var confirmButton : Button
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        confirmViewModel =
            ViewModelProvider(this).get(ConfirmViewModel::class.java)

        _binding = ClientFragmentConfirmBinding.inflate(inflater, container, false)
        val root: View = binding.root

        confirmButton = binding.root.findViewById(R.id.btn_confirm)

        confirmButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), ConfirmCargoActivity::class.java)
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