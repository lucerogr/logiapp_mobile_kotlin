package com.example.logiapplication.logisticOperator.ui.reporting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.client.ClientMainActivity
import com.example.logiapplication.databinding.LogisticFinalStateActivityBinding
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.logisticOperator.ui.followup.FollowUpFragment
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity
import com.example.logiapplication.logisticOperator.ui.followup.ViewRegisterCargoActivity
import com.example.logiapplication.models.Cargo
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinalStateActivity:AppCompatActivity() {
    private lateinit var binding: LogisticFinalStateActivityBinding
    lateinit var sharedPreferences: SharedPreferences

    var getCargaId : Int = 0
    lateinit var getCargaNameSelected : String
    val listCargaId = ArrayList<Int>()
    var listCargaName = ArrayList<String>()
    var listTransportista= ArrayList<String>()
    var listCliente= ArrayList<String>()
    var listDuracion = ArrayList<String>()
    var listEstado = ArrayList<String>()
    var listComentario= ArrayList<String>()

    lateinit var cargaAutoComplete : AutoCompleteTextView
    lateinit var cargoTransportista : TextView
    lateinit var cargoCliente : TextView
    lateinit var cargoDuracion : TextView
    lateinit var cargoEstado : TextView
    lateinit var cargoComentario : TextView

    var getOperatorCodigo : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LogisticFinalStateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        sharedPreferences = this.getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        getOperatorCodigo = sharedPreferences.getInt(LogisticMainActivity.UserCodigo, 0)
        cargaAutoComplete = findViewById(R.id.cargamentoAutoComplete)
        cargoTransportista = findViewById(R.id.tv_transportista_text)
        cargoCliente = findViewById(R.id.tv_cliente_text)
        cargoDuracion = findViewById(R.id.tv_duracion_text)
        cargoEstado = findViewById(R.id.tv_estado_text)
        cargoComentario = findViewById(R.id.tv_comentario_text)


        //GET CARGO
        //CARGAS
        val cargoService: CargoService = RetrofitClients.getUsersClient().create(CargoService::class.java)
        cargoService.getCargoByOperator(getOperatorCodigo).enqueue(object : Callback<List<Cargo>> {
            override fun onResponse(call: Call<List<Cargo>>, response: Response<List<Cargo>>) {
                if(response.isSuccessful) {
                    Log.i("Success", response.body().toString())
                    try {
                        val mapperC = ObjectMapper()
                        val listC: List<Cargo>? = response.body()
                        val jsonArray1C: String = mapperC.writeValueAsString(listC)
                        val jsonArrayC = JSONArray(jsonArray1C)
                        for(i in 0 until jsonArrayC.length()) {
                            val jsonObjectC: JSONObject = jsonArrayC.getJSONObject(i)
                            val cargoObject = Gson().fromJson(jsonObjectC.toString(), Cargo::class.java)
                            if(cargoObject.cargoStatus == "Confirmada") {
                                listCargaId.add(jsonObjectC.getInt("codigo"))
                                listCargaName.add(cargoObject.cargoName)
                                listTransportista.add(cargoObject.personDriverId.personName + " " + cargoObject.personDriverId.personLastName)
                                listCliente.add(cargoObject.personClientId.personName + " " + cargoObject.personClientId.personLastName)
                                val time: List<String> = cargoObject.cargoRouteDuration.split(":")
                                listDuracion.add(time[0] + " horas  " + time[1] + " min")
                                listEstado.add(cargoObject.cargoRouteStatus)
                                listComentario.add(cargoObject.cargoComments)

                            }
                        }
                        val arrayAdapter = ArrayAdapter(this@FinalStateActivity, R.layout.dropdown_item, listCargaName)
                        cargaAutoComplete.setAdapter(arrayAdapter)
                        cargaAutoComplete.setOnItemClickListener { parent, view, position, id ->
                            for (i in 0 until listCargaId.size) {
                                if (position == i) {
                                    getCargaId = listCargaId[i]
                                    getCargaNameSelected = listCargaName[i]

                                    cargoTransportista.text = listTransportista[i]
                                    cargoCliente.text = listCliente[i]
                                    cargoDuracion.text = listDuracion[i]
                                    cargoEstado.text = listEstado[i]
                                    cargoComentario.text = listComentario[i]


                                }
                            }
                        }
                    }
                    catch (ex: JSONException){
                        ex.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<List<Cargo>>, t: Throwable) {
                Toast.makeText(this@FinalStateActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.logistic_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.itemId
        when (item.itemId) {
            R.id.back-> goBack()
            R.id.log_out_action ->goToLoginActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    fun goBack() {
        val intent = Intent(this, LogisticMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }
    fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }
}