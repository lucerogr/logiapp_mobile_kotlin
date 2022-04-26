package com.example.logiapplication.logisticOperator.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.LoginActivity.Companion.DateBirth
import com.example.logiapplication.LoginActivity.Companion.Email
import com.example.logiapplication.LoginActivity.Companion.LastName
import com.example.logiapplication.LoginActivity.Companion.Name
import com.example.logiapplication.R
import com.example.logiapplication.databinding.LogisticFragmentProfileBinding

class LogisticProfileFragment : Fragment() {

    private lateinit var logisticProfileViewModel: LogisticProfileViewModel
    private var _binding: LogisticFragmentProfileBinding? = null
    lateinit var UserName : TextView
    lateinit var UserLastName : TextView
    lateinit var UserDate : TextView
    lateinit var UserEmail : TextView


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UserName = binding.root.findViewById(R.id.logistic_name)
        UserLastName = binding.root.findViewById(R.id.logistic_last_name)
        UserDate = binding.root.findViewById(R.id.logistic_date)
        UserEmail = binding.root.findViewById(R.id.logistic_email)

        val args = arguments
        UserName.text = args?.getString(Name)
        UserLastName.text = args?.getString(LastName)
        UserDate.text = args?.getString(DateBirth)
        UserEmail.text = args?.getString(Email)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logisticProfileViewModel =
            ViewModelProvider(this).get(LogisticProfileViewModel::class.java)

        _binding = LogisticFragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/
}