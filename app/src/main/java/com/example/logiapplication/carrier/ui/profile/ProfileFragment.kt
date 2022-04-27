package com.example.logiapplication.carrier.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.databinding.CarrierFragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: CarrierFragmentProfileBinding? = null

    lateinit var UserName : TextView
    lateinit var UserLastName : TextView
    lateinit var UserDate : TextView
    lateinit var UserEmail : TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UserName = binding.root.findViewById(R.id.driver_name)
        UserLastName = binding.root.findViewById(R.id.driver_last_name)
        UserDate = binding.root.findViewById(R.id.driver_date)
        UserEmail = binding.root.findViewById(R.id.driver_email)

        val args = arguments
        UserName.text = args?.getString(LoginActivity.Name)
        UserLastName.text = args?.getString(LoginActivity.LastName)
        UserDate.text = args?.getString(LoginActivity.DateBirth)
        UserEmail.text = args?.getString(LoginActivity.Email)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = CarrierFragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}