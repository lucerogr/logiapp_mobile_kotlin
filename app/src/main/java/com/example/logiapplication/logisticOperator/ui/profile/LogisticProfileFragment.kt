package com.example.logiapplication.logisticOperator.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.LoginActivity.Companion.DateBirth
import com.example.logiapplication.LoginActivity.Companion.Email
import com.example.logiapplication.LoginActivity.Companion.LastName
import com.example.logiapplication.LoginActivity.Companion.Name
import com.example.logiapplication.R
import com.example.logiapplication.databinding.LogisticFragmentProfileBinding
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.models.User

class LogisticProfileFragment : Fragment() {

    private lateinit var logisticProfileViewModel: LogisticProfileViewModel
    private var _binding: LogisticFragmentProfileBinding? = null
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
        logisticProfileViewModel =
            ViewModelProvider(this).get(LogisticProfileViewModel::class.java)

        _binding = LogisticFragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences=requireActivity().getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        UserName = binding.root.findViewById(R.id.logistic_name)
        UserLastName = binding.root.findViewById(R.id.logistic_last_name)
        UserDate = binding.root.findViewById(R.id.logistic_date)
        UserEmail = binding.root.findViewById(R.id.logistic_email)

        UserName.text = sharedPreferences.getString(Name, null)
        UserLastName.text = sharedPreferences.getString(LastName, null)
        UserDate.text = sharedPreferences.getString(DateBirth, null)
        UserEmail.text = sharedPreferences.getString(Email, null)

        return root
    }
}