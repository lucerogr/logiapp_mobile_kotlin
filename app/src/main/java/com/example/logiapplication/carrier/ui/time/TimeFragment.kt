package com.example.logiapplication.carrier.ui.time

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.R
import com.example.logiapplication.carrier.ui.syncup.ConnectionActivity
import com.example.logiapplication.databinding.CarrierFragmentTimeBinding


class TimeFragment : Fragment() {

    private lateinit var timeViewModel: TimeViewModel
    private var _binding: CarrierFragmentTimeBinding? = null

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

        _binding = CarrierFragmentTimeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val cargamentos = resources.getStringArray(R.array.cargamentos)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, cargamentos)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    //seleccionar el cargamento para que la info se guarde en ese cargamento y luego presionar el boton iNICIO
                    //igual para los otros
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