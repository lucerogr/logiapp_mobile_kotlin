package com.example.logiapplication.ui.time

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.databinding.FragmentTimeBinding
import com.example.logiapplication.ui.syncup.ConnectionActivity


class TimeFragment : Fragment() {

    private lateinit var timeViewModel: TimeViewModel
    private var _binding: FragmentTimeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        timeViewModel =
            ViewModelProvider(this).get(TimeViewModel::class.java)

        _binding = FragmentTimeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val cargamentos = resources.getStringArray(R.array.cargamentos)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, cargamentos)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    val intent = Intent(requireContext(), ConnectionActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(requireContext(), ConnectionActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                else -> {
                    val intent = Intent(requireContext(), ConnectionActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
            }
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        val cargamentos = resources.getStringArray(R.array.cargamentos)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, cargamentos)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    val intent = Intent(requireContext(), ConnectionActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(requireContext(), ConnectionActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                else -> {
                    val intent = Intent(requireContext(), ConnectionActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}