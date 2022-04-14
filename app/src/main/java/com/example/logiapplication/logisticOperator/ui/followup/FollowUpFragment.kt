package com.example.logiapplication.logisticOperator.ui.followup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.R
import com.example.logiapplication.databinding.LogisticFragmentFollowupBinding
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.logisticOperator.ui.register.NextRegisterActivity

class FollowUpFragment : Fragment() {
    private lateinit var followUpViewModel: FollowUpViewModel
    private var _binding: LogisticFragmentFollowupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var viewCargo : ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        followUpViewModel =
            ViewModelProvider(this).get(FollowUpViewModel::class.java)

        _binding = LogisticFragmentFollowupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewCargo = binding.root.findViewById(R.id.iv_view)

        viewCargo.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), ViewRegisterCargoActivity::class.java)
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