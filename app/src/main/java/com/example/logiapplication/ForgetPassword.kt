package com.example.logiapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class ForgetPassword : AppCompatActivity() {
    lateinit var etEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget_password)
        viewInitializations()
    }

    fun viewInitializations() {

        etEmail = findViewById(R.id.email)

        // To show back button in actionbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    fun validateInput(): Boolean {

        if (etEmail.text.toString().equals("")) {
            etEmail.setError("Por favor, ingrese su correo electrónico")
            return false
        }
        // checking the proper email format
        if (!isEmailValid(etEmail.text.toString())) {
            etEmail.setError("Por favor, ingrese un correo válido")
            return false
        }
        return true
    }

    fun isEmailValid(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }

    fun performForgetPassword (view: View) {
        if (validateInput()) {

            Toast.makeText(this,"Se envió la nueva contraseña a su correo", Toast.LENGTH_SHORT).show()

        }
    }

    fun goToLogin(view: View)  {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

}