package com.example.logiapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.logiapplication.client.ClientMainActivity
import com.example.logiapplication.interfaces.UserService
import com.example.logiapplication.models.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    companion object {
        var SHARED_PREF_NAME = "log_info"
        var FirstName = "userPassword"
    }
    lateinit var editUsuario : EditText
    lateinit var editPassword : EditText
    lateinit var getObjectUser : String
    lateinit var getfname : String
    lateinit var buttonIngresar: Button


    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        editUsuario = findViewById(R.id.editUsuario)
        editPassword = findViewById(R.id.editPassword)
        buttonIngresar = findViewById(R.id.btnIngresar)

        sharedPreferences=getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
        )

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }

        buttonIngresar.setOnClickListener(View.OnClickListener {
            if(editUsuario.text.toString().isEmpty() || editPassword.text.toString().isEmpty()) {
                Toast.makeText(this, "Ingresar usuario y/o contraseña",Toast.LENGTH_SHORT).show()
            } else {
                val emailUser: String = editUsuario.text.toString()
                val passwordUser: String = editPassword.text.toString()

                val userService: UserService = RetrofitClients.getUsersClient().create(UserService::class.java)
                userService.login(emailUser, passwordUser).enqueue(object : Callback<List<User>>{
                    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                        if(response.isSuccessful) {
                            Log.i("Success", response.body().toString())
                            try {
                                val mapper = ObjectMapper()
                                val list: List<User>? = response.body()
                                val jsonArray1: String = mapper.writeValueAsString(list)
                                val jsonArray = JSONArray(jsonArray1)
                                var i = 0
                                getObjectUser = "u"
                                getfname="p"
                                while(i<jsonArray.length() && emailUser != getObjectUser && passwordUser != getfname){
                                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                    getObjectUser=jsonObject.getString("userUsername")
                                    getfname=jsonObject.getString("userPassword")
                                    i++
                                }
                                //Toast.makeText(applicationContext, emailUser, Toast.LENGTH_SHORT).show()
                                if(emailUser == getObjectUser && passwordUser ==getfname) {
                                    //Toast.makeText(applicationContext, "Usuario", Toast.LENGTH_SHORT).show()
                                    val editor: SharedPreferences.Editor=sharedPreferences.edit()
                                    editor.putString(FirstName, getfname);
                                    editor.apply()
                                    val intent = Intent(this@LoginActivity, ClientMainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)

                                }else{
                                    Toast.makeText(this@LoginActivity, "Usuario y/o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                                }
                            }
                            catch (ex: JSONException){
                                ex.printStackTrace()
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                    }
                })

            }
        })

    }

    fun forget(view: View) {
        val intent = Intent(this, ForgetPassword::class.java)
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