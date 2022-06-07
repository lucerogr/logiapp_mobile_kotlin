package com.example.logiapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.logiapplication.ForgetPassword.Companion.codePwd
import com.example.logiapplication.ForgetPassword.Companion.userIdPwd
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.interfaces.UserService
import com.example.logiapplication.models.Cargo
import com.example.logiapplication.models.ProductCargo
import com.example.logiapplication.models.Truck
import com.example.logiapplication.models.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassword : AppCompatActivity() {
    lateinit var codigo: EditText
    lateinit var newPwd: EditText
    lateinit var confirmNewPwd: EditText
    lateinit var btnChangePwd: Button
    lateinit var sharedPreferences: SharedPreferences

    var codePassword: Int = 0
    var userId: Int = 0

    var userObject : User = User(0," ", " ", 0, 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        codigo = findViewById(R.id.et_code)
        newPwd = findViewById(R.id.et_password_one)
        confirmNewPwd = findViewById(R.id.et_password_two)
        btnChangePwd = findViewById(R.id.btn_change)

        codePassword = sharedPreferences.getInt(codePwd, 0)
        userId = sharedPreferences.getInt(userIdPwd, 0)

        btnChangePwd.setOnClickListener(View.OnClickListener {
            if(codePassword.toString() == codigo.text.toString()){
                if(newPwd.text.toString().equals("")){
                    newPwd.setError("Por favor ingrese una contraseña")
                }
                else if (!confirmNewPwd.text.toString().equals(newPwd.text.toString())){
                    confirmNewPwd.setError("Contraseñas no coinciden")
                }
                else{
                    val userService: UserService = RetrofitClients.getUsersClient().create(
                        UserService::class.java)
                    userService.getUser(userId).enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (response.isSuccessful) {
                                Log.i("Success", response.body().toString())
                                try {
                                    val user: User? = response.body()
                                    val jsonObjectUser = JSONObject(Gson().toJson(user))
                                    userObject = Gson().fromJson(jsonObjectUser.toString(), User::class.java)

                                    val userData = User (codigo = userObject.codigo,
                                        userUsername = userObject.userUsername,
                                        userPassword = confirmNewPwd.text.toString(),
                                        roleId = userObject.roleId,
                                        personId = userObject.roleId
                                    )
                                    updateUser(userData, userId) {
                                        if (it?.codigo != null) {
                                            Toast.makeText(applicationContext, "Se cambió la contraseña", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this@ChangePassword, LoginActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                }
                                catch (ex: JSONException){
                                    ex.printStackTrace()
                                }
                            }
                        }
                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(applicationContext, "Error de conexión, intentelo nuevamente", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            else{
                codigo.setError("Ingresar un código válido")
            }
        })
    }
    fun updateUser(userData: User, id: Int, onResult: (User?) -> Unit){
        val userService: UserService = RetrofitClients.getUsersClient().create(UserService::class.java)
        userService.updateUser(userData, id).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val updatedUser = response.body()
                onResult(updatedUser)
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                onResult(null)
            }
        })
    }
}