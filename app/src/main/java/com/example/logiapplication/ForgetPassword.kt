package com.example.logiapplication

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.logiapplication.interfaces.UserService
import com.example.logiapplication.models.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class ForgetPassword : AppCompatActivity() {
    companion object {
        var codePwd = "codigoPwd"
        var userIdPwd = "userIdPassword"
    }

    lateinit var etEmail: EditText
    lateinit var btnSend: Button
    lateinit var getUsername : String
    var getUserId : Int = 0

    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget_password)

        etEmail = findViewById(R.id.email)
        btnSend = findViewById(R.id.send)

        sharedPreferences=getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        btnSend.setOnClickListener(View.OnClickListener {
            if (etEmail.text.toString().equals("")) {
                etEmail.setError("Por favor, ingrese su correo electrónico")
            } else if (!isEmailValid(etEmail.text.toString())) {
                etEmail.setError("Por favor, ingrese un correo válido")
            } else {
                val emailUser: String = etEmail.text.toString()
                val userService: UserService = RetrofitClients.getUsersClient().create(UserService::class.java)
                userService.login().enqueue(object : Callback<List<User>> {
                    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                        if (response.isSuccessful) {
                            Log.i("Success", response.body().toString())
                            try {
                                val mapper = ObjectMapper()
                                val list: List<User>? = response.body()
                                val jsonArray1: String = mapper.writeValueAsString(list)
                                val jsonArray = JSONArray(jsonArray1)

                                var i = 0
                                getUsername = "u"

                                while (i < jsonArray.length() && emailUser != getUsername) {
                                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                    getUsername = jsonObject.getString("userUsername")
                                    getUserId = jsonObject.getInt("codigo")
                                    i++
                                }

                                if(emailUser == getUsername) {
                                    sendEmail(emailUser, getUserId)
                                }
                                else {
                                    Toast.makeText(applicationContext, "Correo no registrado", Toast.LENGTH_SHORT).show()
                                }
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

        val policy = ThreadPolicy.Builder()
            .permitAll()
            .build()
        StrictMode.setThreadPolicy(policy)
    }

    fun sendEmail(email: String, id: Int) {
        val r = (1000..9999).random()

        val username = "senderlogiapp@outlook.com"
        val password = "Logiappsendr1"
        val messageToSend = "Por favor, ingresar el siguiente codigo para cambiar la contraseña del usuario $email: $r"

        val props = Properties()
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.smtp.host", "smtp-mail.outlook.com")
        props.put("mail.smtp.port", "587")

        val session = Session.getInstance(props,
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(username, password)
                }
            })
        try {
            val message: Message = MimeMessage(session)
            message.setFrom(InternetAddress(username))
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse("receiverlogiapp@outlook.com")
            )
            //Logiappreceivr1
            message.subject = "LOGIAPP-CAMBIAR CONTRASEÑA"
            message.setText(messageToSend)
            Transport.send(message)
            val editor: SharedPreferences.Editor=sharedPreferences.edit()
            editor.putInt(codePwd, r)
            editor.putInt(userIdPwd, id)
            editor.apply()
            Toast.makeText(applicationContext, "Se envió el código de validación", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@ForgetPassword, ChangePassword::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)

        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
        }

        /*Log.i("Send email", "")

        val TO = arrayOf(email)
        val CC = arrayOf("anlugeri2001@gmail.com")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"

        val r = (1000..9999).random()

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
        emailIntent.putExtra(Intent.EXTRA_CC, CC)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "LOGIAPP-CAMBIAR CONTRASEÑA")
        emailIntent.putExtra(Intent.EXTRA_TEXT,
            "Por favor, ingresar el siguiente codigo para cambiar la contraseña del usuario $email: $r"
        )

        //pasar el codigo de validacion


        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar correo..."))
            finish()
            Log.i("Se finalizó el envio...", "")
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "Error", Toast.LENGTH_SHORT
            ).show()
        }*/
    }

    fun isEmailValid(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }
}