package com.example.logiapplication.carrier.ui.profile

import android.content.Context
import android.content.SharedPreferences
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

    lateinit var sharedPreferences : SharedPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = CarrierFragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences=requireActivity().getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        UserName = binding.root.findViewById(R.id.driver_name)
        UserLastName = binding.root.findViewById(R.id.driver_last_name)
        UserDate = binding.root.findViewById(R.id.driver_date)
        UserEmail = binding.root.findViewById(R.id.driver_email)

        UserName.text = sharedPreferences.getString(LoginActivity.Name, null)
        UserLastName.text = sharedPreferences.getString(LoginActivity.LastName, null)
        UserDate.text = sharedPreferences.getString(LoginActivity.DateBirth, null)
        UserEmail.text = sharedPreferences.getString(LoginActivity.Email, null)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}