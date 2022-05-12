package com.example.logiapplication.client.ui.confirm

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
import com.example.logiapplication.client.ui.confirm.ConfirmFragment.Companion.CARGA_CONFIRMADA
import com.example.logiapplication.client.ui.followup.ClientFollowUpFragment
import com.example.logiapplication.databinding.ClientConfirmCargoActivityBinding
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.interfaces.ProductCargoService
import com.example.logiapplication.logisticOperator.ui.followup.ModifyProductsActivity
import com.example.logiapplication.models.Cargo
import com.example.logiapplication.models.ProductCargo
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmCargoActivity : AppCompatActivity() {
    private lateinit var binding: ClientConfirmCargoActivityBinding

    lateinit var confirmButton : Button

    lateinit var sharedPreferences: SharedPreferences
    var getCargoId : Int = 0
    lateinit var cargoNombre : TextView
    lateinit var cargoCarrier : TextView
    lateinit var cargoClient : TextView
    lateinit var cargoDuration : TextView
    lateinit var getCargoDuration : String
    lateinit var cargoRouteStatus : AutoCompleteTextView
    lateinit var cargoComments : AutoCompleteTextView

    val listRouteStatus = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ClientConfirmCargoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        confirmButton = binding.root.findViewById(R.id.btn_confirm_cargo)
        cargoNombre = binding.root.findViewById(R.id.tv_carg_nro)
        cargoCarrier = binding.root.findViewById(R.id.tv_transportista_text)
        cargoClient = binding.root.findViewById(R.id.tv_cliente_text)
        cargoDuration = binding.root.findViewById(R.id.tv_duracion_text)
        cargoRouteStatus = binding.root.findViewById(R.id.cargamentoAutoComplete)
        cargoComments = binding.root.findViewById(R.id.comentarioAutoComplete)


        getCargoId = sharedPreferences.getInt(CARGA_CONFIRMADA, 0)

        //GET CARGO
        val cargoService: CargoService = RetrofitClients.getUsersClient().create(CargoService::class.java)
        cargoService.getCargo(getCargoId).enqueue(object : Callback<Cargo> {
            override fun onResponse(call: Call<Cargo>, response: Response<Cargo>) {
                if(response.isSuccessful) {
                    Log.i("Success", response.body().toString())
                    try {
                        val cargo: Cargo? = response.body()
                        val jsonObjectCargo= JSONObject(Gson().toJson(cargo))
                        val cargoObject = Gson().fromJson(jsonObjectCargo.toString(), Cargo::class.java)
                        cargoNombre.text = cargoObject.cargoName
                        cargoCarrier.text = (cargoObject.personDriverId.personName + " " + cargoObject.personDriverId.personLastName)
                        cargoClient.text = (cargoObject.personClientId.personName + " " + cargoObject.personClientId.personLastName)
                        val time: List<String> = cargoObject.cargoRouteDuration.split(":")
                        getCargoDuration = time[0] + " horas  " + time[1] + " min"
                        cargoDuration.text = getCargoDuration

                        listRouteStatus.add("Observado")
                        listRouteStatus.add("Correcto")
                        val arrayAdapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, listRouteStatus)
                        cargoRouteStatus.setAdapter(arrayAdapter)
                        cargoRouteStatus.setText(cargoObject.cargoRouteStatus, false)

                        confirmButton.setOnClickListener(View.OnClickListener {
                            if(cargoRouteStatus.text.isEmpty()
                                || cargoComments.text.isEmpty()){
                                Toast.makeText(applicationContext, "Ingrese todos la información solicitada",Toast.LENGTH_SHORT).show()
                            } else{
                                val cargoData = Cargo(  codigo = cargoObject.codigo,
                                    cargoName = cargoObject.cargoName,
                                    cargoDate = cargoObject.cargoDate,
                                    cargoHour = cargoObject.cargoHour,
                                    cargoInitialUbication = cargoObject.cargoInitialUbication,
                                    cargoFinalUbication = cargoObject.cargoFinalUbication,
                                    cargoStatus = "Confirmada",
                                    cargoRouteDuration = cargoObject.cargoRouteDuration,
                                    cargoRouteStatus = cargoRouteStatus.text.toString(),
                                    camion = cargoObject.camion,
                                    famproducto = cargoObject.famproducto,
                                    personClientId = cargoObject.personClientId,
                                    personOperatorId = cargoObject.personOperatorId,
                                    personDriverId = cargoObject.personDriverId,
                                    cargoComments = cargoComments.text.toString()
                                )
                                updateCargo(cargoData, getCargoId) {
                                    //Toast.makeText(applicationContext, "Se actualizó la carga. Ahora actualice los productos", Toast.LENGTH_SHORT).show()
                                    if (it?.codigo != null) {
                                        Toast.makeText(applicationContext, "Se confirmò la carga", Toast.LENGTH_SHORT).show()

                                    } else {
                                        Toast.makeText(applicationContext, "Error al confirmar la carga", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                val intent = Intent(this@ConfirmCargoActivity, ClientMainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                startActivity(intent)

                            }

                        })

                    }
                    catch (ex: JSONException){
                        ex.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<Cargo>, t: Throwable) {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
        })

    }
    fun updateCargo(cargoData: Cargo, id: Int, onResult: (Cargo?) -> Unit){
        val cargoService: CargoService = RetrofitClients.getUsersClient().create(CargoService::class.java)
        cargoService.updateCargo(cargoData, id).enqueue(object : Callback<Cargo> {
            override fun onResponse(call: Call<Cargo>, response: Response<Cargo>) {
                val updatedCargo = response.body()
                onResult(updatedCargo)
            }
            override fun onFailure(call: Call<Cargo>, t: Throwable) {
                onResult(null)
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
        val intent = Intent(this, ClientMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }
    fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }
}