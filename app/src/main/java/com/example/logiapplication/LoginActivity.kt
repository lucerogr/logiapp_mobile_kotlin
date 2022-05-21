package com.example.logiapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.logiapplication.carrier.CarrierMainActivity
import com.example.logiapplication.client.ClientMainActivity
import com.example.logiapplication.interfaces.UserService
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.models.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {
    companion object {
        var SHARED_PREF_NAME = "log_info"
        var FirstName = "userPassword"
        var Name = "name"
        var LastName = "lastName"
        var DateBirth = "dateBirth"
        var Email = "email"
        //var UserId ="userId"
    }
    lateinit var editUsuario : EditText
    lateinit var editPassword : EditText
    lateinit var getUsername : String
    lateinit var getPassword : String
    lateinit var getPersonName : String
    lateinit var getPersonBirthDate : String
    lateinit var getPersonLastName : String
    lateinit var getNameAndLastName : String
    var getCodigo : Int = 0
    lateinit var buttonIngresar: Button
    var getRolId : Int = 0
    var getPersonId : Int = 0


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

        //INGRESAR LOGIN
        buttonIngresar.setOnClickListener(View.OnClickListener {
            if(editUsuario.text.toString().isEmpty() || editPassword.text.toString().isEmpty()) {
                Toast.makeText(this, "Ingresar usuario y/o contraseña",Toast.LENGTH_SHORT).show()
            } else {
                val emailUser: String = editUsuario.text.toString()
                val passwordUser: String = editPassword.text.toString()
                val userService: UserService = RetrofitClients.getUsersClient().create(UserService::class.java)
                userService.login().enqueue(object : Callback<List<User>>{
                    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                        if(response.isSuccessful) {
                            Log.i("Success", response.body().toString())
                            try {
                                val mapper = ObjectMapper()
                                val list: List<User>? = response.body()
                                val jsonArray1: String = mapper.writeValueAsString(list)
                                val jsonArray = JSONArray(jsonArray1)

                                //println(jsonArray)

                                var i = 0
                                getUsername = "u"
                                getPassword = "p"
                                getNameAndLastName = "n"
                                while(i<jsonArray.length() && emailUser != getUsername && passwordUser != getPassword){
                                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                    getUsername=jsonObject.getString("userUsername")
                                    getPassword=jsonObject.getString("userPassword")
                                    getCodigo =jsonObject.getInt("codigo")

                                    i++
                                }
                                userService.getRolesByUserId(getCodigo).enqueue(object: Callback<Array<Any?>?> {
                                    override fun onResponse(call: Call<Array<Any?>?>, response: Response<Array<Any?>?>) {
                                        if (response.isSuccessful) {
                                            Log.i("Success", response.body().toString())
                                            try {
                                                val mapperR = ObjectMapper()
                                                val listR: Array<Any?>? = response.body()
                                                val jsonArray1R: String = mapperR.writeValueAsString(listR)
                                                val jsonArrayR = JSONArray(jsonArray1R)
                                                val jsonArrayR2 = jsonArrayR.getJSONArray(0)


                                                val jsonObjectR: JSONObject = jsonArrayR2.getJSONObject(0)
                                                getRolId=jsonObjectR.getInt("codigo")
                                                //println(getRolId)
                                            }
                                            catch (ex: JSONException){
                                                ex.printStackTrace()
                                            }
                                        }
                                    }
                                    override fun onFailure(call: Call<Array<Any?>?>, t: Throwable) {
                                        Toast.makeText(applicationContext, "Error de conexión, intentelo nuevamente", Toast.LENGTH_SHORT).show()
                                    }
                                })
                                userService.getPersonByUserId(getCodigo).enqueue(object: Callback<Array<Any?>?> {
                                    override fun onResponse(call: Call<Array<Any?>?>, response: Response<Array<Any?>?>) {
                                        if (response.isSuccessful) {
                                            Log.i("Success", response.body().toString())
                                            try {
                                                val mapperP = ObjectMapper()
                                                val listP: Array<Any?>? = response.body()
                                                val jsonArray1P: String = mapperP.writeValueAsString(listP)
                                                val jsonArrayP = JSONArray(jsonArray1P)
                                                val jsonArrayP2 = jsonArrayP.getJSONArray(0)


                                                val jsonObjectP: JSONObject = jsonArrayP2.getJSONObject(0)
                                                getPersonId=jsonObjectP.getInt("codigo")
                                                getPersonName = jsonObjectP.getString("personName")
                                                getPersonLastName = jsonObjectP.getString("personLastName")
                                                getPersonBirthDate = jsonObjectP.getString("personBirthDate")
                                                getNameAndLastName = "$getPersonName $getPersonLastName"

                                                if(emailUser == getUsername && passwordUser ==getPassword) {
                                                    //Toast.makeText(applicationContext, "GREAT", Toast.LENGTH_SHORT).show()
                                                    //println(getNameAndLastName)
                                                    val editor: SharedPreferences.Editor=sharedPreferences.edit()
                                                    editor.putString(FirstName, getNameAndLastName);
                                                    editor.apply()
                                                    when(getRolId) {
                                                        1 -> {
                                                            val editor: SharedPreferences.Editor=sharedPreferences.edit()
                                                            editor.putString(FirstName, getNameAndLastName)
                                                            editor.putString(Name, getPersonName)
                                                            editor.putString(LastName, getPersonLastName)
                                                            editor.putString(DateBirth, getPersonBirthDate)
                                                            editor.putString(Email, getUsername)
                                                            editor.apply()
                                                            val intent = Intent(this@LoginActivity, ClientMainActivity::class.java)
                                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                            intent.putExtra("UserId", getPersonId)
                                                            startActivity(intent) }
                                                        2 -> {
                                                            val editor: SharedPreferences.Editor=sharedPreferences.edit()
                                                            editor.putInt("DRIVER", getPersonId)
                                                            editor.putString(FirstName, getNameAndLastName)
                                                            editor.putString(Name, getPersonName)
                                                            editor.putString(LastName, getPersonLastName)
                                                            editor.putString(DateBirth, getPersonBirthDate)
                                                            editor.putString(Email, getUsername)
                                                            editor.apply()
                                                            val intent = Intent(this@LoginActivity, CarrierMainActivity::class.java)
                                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                            intent.putExtra("UserId", getPersonId)
                                                            startActivity(intent) }
                                                        3 -> {
                                                            val editor: SharedPreferences.Editor=sharedPreferences.edit()
                                                            editor.putString(FirstName, getNameAndLastName)
                                                            editor.putString(Name, getPersonName)
                                                            editor.putString(LastName, getPersonLastName)
                                                            editor.putString(DateBirth, getPersonBirthDate)
                                                            editor.putString(Email, getUsername)
                                                            editor.apply()
                                                            val intent = Intent(this@LoginActivity, LogisticMainActivity::class.java)
                                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                            intent.putExtra("UserId", getPersonId)
                                                            startActivity(intent) }
                                                    }

                                                }else{
                                                    if (!validarEmail(editUsuario.text.toString())){
                                                        editUsuario.error = "Correo no válido"
                                                    }
                                                    else {
                                                        Toast.makeText(this@LoginActivity, "Correo o contraseña no válidos", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                            catch (ex: JSONException){
                                                ex.printStackTrace()
                                            }
                                        }
                                    }
                                    override fun onFailure(call: Call<Array<Any?>?>, t: Throwable) {
                                        Toast.makeText(applicationContext, "Error de conexión, intentelo nuevamente", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                            catch (ex: JSONException){
                                ex.printStackTrace()
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error de conexión, intentelo nuevamente", Toast.LENGTH_SHORT).show()
                    }
                })

            }
        })

    }

    private fun validarEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
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