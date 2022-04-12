package com.example.logiapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.logiapplication.ui.profile.ProfileFragment
import com.google.android.material.internal.ContextUtils.getActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
        )

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    fun forget(view: View) {
        val intent = Intent(this, ForgetPassword::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    fun goToMainActivity(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    private fun hasPermissions(context: Context, permissions: Array<String>) : Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if(ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Ya se cuenta con permiso de Ubicación",
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "El usuario rechazó el permiso. Habitelo manual",
                    Toast.LENGTH_LONG).show()
            }

            if(grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Ya se cuenta con permiso de Cámara",
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "El usuario rechazó el permiso. Habitelo manual",
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}