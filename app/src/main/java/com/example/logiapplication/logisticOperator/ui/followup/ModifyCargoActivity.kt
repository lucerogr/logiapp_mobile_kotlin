package com.example.logiapplication.logisticOperator.ui.followup

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.logiapplication.client.ui.followup.ClientFollowUpFragment.Companion.CARGA_ID
import com.example.logiapplication.databinding.LogisticModifyCargoActivityBinding
import com.example.logiapplication.databinding.LogisticViewRegisterCargoActivityBinding
import com.example.logiapplication.interfaces.*
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.logisticOperator.ui.register.NextRegisterActivity
import com.example.logiapplication.logisticOperator.ui.register.RegisterViewModel
import com.example.logiapplication.models.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ModifyCargoActivity:AppCompatActivity() {
    private lateinit var binding: LogisticModifyCargoActivityBinding
    lateinit var nextButton : Button
    lateinit var botonCancelarEdicion : Button
    companion object {
        var FAMILY_ID = "family_product"
        var FAMILY_NAME = "family_name"
        var TRUCK_ID = "truck_id"
        var CARRIER_ID = "carrier_id"
        var CLIENT_ID ="client_id"
        var FECHA_RECOJO ="fecha_recojo"
        var HORA_RECOJO ="hora_recojo"
        var LUGAR_RECOJO ="lugar_recojo"
        var LUGAR_ENTREGA ="lugar_entrega"
        var NOMBRE_CARGA = "nombre_carga"
        var CARGO_ID = "cargo_codigo"
        var CARGO_STATUS = "cargo_status"
        var CARGO_ROUTE_STATUS = "cargo_route_status"
        var CARGO_DURATION = "cargo_duration"
        var CARGO_COMMENT = "cargo_comment"
    }
    private lateinit var registerViewModel: RegisterViewModel
    var getObjectFamilyProductId : Int = 0
    lateinit var getObjectFamilyProductName : String
    val listFamilyProduct = ArrayList<String>()
    val listFamilyProductId = ArrayList<Int>()

    var getObjectTruckId : Int = 0
    lateinit var getObjectTruckPlate : String
    val listTruckPlate = ArrayList<String>()
    val listTrucktId = ArrayList<Int>()

    var getCarrierCodigo : Int = 0
    lateinit var getObjectCarrierName : String
    lateinit var getObjectCarrierLastName : String
    lateinit var CarrierNameAndLastName : String
    var getCarrierId : Int = 0
    val listCarrier = ArrayList<String>()
    var listCarrierId = ArrayList<Int>()

    var getClientCodigo : Int = 0
    lateinit var getObjectClientName : String
    lateinit var getObjectClientLastName : String
    lateinit var ClientNameAndLastName : String
    var getClientId : Int = 0
    val listClient = ArrayList<String>()
    val listClientId = ArrayList<Int>()

    val calendario = Calendar.getInstance()

    lateinit var familiaProducto : AutoCompleteTextView
    lateinit var camion : AutoCompleteTextView
    lateinit var transportista : AutoCompleteTextView
    lateinit var cliente : AutoCompleteTextView
    lateinit var fechaRecojo : AutoCompleteTextView
    lateinit var horaRecojo : AutoCompleteTextView
    lateinit var lugarRecojo : AutoCompleteTextView
    lateinit var lugarEntrega : AutoCompleteTextView
    lateinit var nombreCarga : EditText

    var getFamilyProductSelected : Int = 0
    var getTruckSelected : Int = 0
    var getCarrierSelected : Int = 0
    var getClientSelected : Int = 0
    var getFamilyProductNameSelected : String = "s"

    lateinit var sharedPreferences: SharedPreferences
    var getCargoId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LogisticModifyCargoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        getCargoId = sharedPreferences.getInt(CARGA_ID, 0)

        nextButton = binding.root.findViewById(R.id.btn_next_register_L)


        nextButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ModifyProductsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        })

        familiaProducto = binding.root.findViewById(R.id.familyAutoComplete_L)
        camion = binding.root.findViewById(R.id.camionAutoComplete_L)
        transportista = binding.root.findViewById(R.id.carrierAutoComplete_L)
        cliente = binding.root.findViewById(R.id.clientAutoComplete_L)
        fechaRecojo = binding.root.findViewById(R.id.fechaRecojoAutoComplete_L)
        horaRecojo = binding.root.findViewById(R.id.horaRecojoAutoComplete_L)
        lugarRecojo = binding.root.findViewById(R.id.lugarRecojoAutoComplete_L)
        lugarEntrega = binding.root.findViewById(R.id.lugarEntregaAutoComplete_L)
        nombreCarga=binding.root.findViewById(R.id.tv_cargo_number_L)
        botonCancelarEdicion = binding.root.findViewById(R.id.btn_cancel_register_L)

        //Cancelar edición de carga
        botonCancelarEdicion.setOnClickListener(View.OnClickListener{
            val intent = Intent(this, LogisticMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        })

        //GET DATOS DE LA CARGA
        val cargoService: CargoService = RetrofitClients.getUsersClient().create(CargoService::class.java)
        cargoService.getCargo(getCargoId).enqueue(object : Callback<Cargo> {
            override fun onResponse(call: Call<Cargo>, response: Response<Cargo>) {
                if(response.isSuccessful) {
                    Log.i("Success", response.body().toString())
                    try {
                        val cargo: Cargo? = response.body()
                        val jsonObjectCargo= JSONObject(Gson().toJson(cargo))
                        val cargoObject = Gson().fromJson(jsonObjectCargo.toString(), Cargo::class.java)
                        nombreCarga.setText(cargoObject.cargoName)

                        //FAMILIA DE PRODUCTO

                        val familyProductService: FamilyProductService = RetrofitClients.getUsersClient().create(FamilyProductService::class.java)

                        familyProductService.getFamilyProduct().enqueue(object : Callback<List<FamilyProduct>> {
                            override fun onResponse(call: Call<List<FamilyProduct>>, response: Response<List<FamilyProduct>>) {
                                if(response.isSuccessful) {
                                    Log.i("Success", response.body().toString())
                                    try {
                                        val mapperF = ObjectMapper()
                                        val listF: List<FamilyProduct>? = response.body()
                                        val jsonArray1F: String = mapperF.writeValueAsString(listF)
                                        val jsonArrayF = JSONArray(jsonArray1F)
                                        getObjectFamilyProductName = "u"
                                        for(i in 0 until jsonArrayF.length()) {
                                            val jsonObjectF: JSONObject = jsonArrayF.getJSONObject(i)
                                            getObjectFamilyProductId=jsonObjectF.getInt("codigo")
                                            getObjectFamilyProductName=jsonObjectF.getString("familyProductName")
                                            listFamilyProductId.add(getObjectFamilyProductId)
                                            listFamilyProduct.add(getObjectFamilyProductName)
                                        }
                                        val arrayAdapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, listFamilyProduct)
                                        familiaProducto.setAdapter(arrayAdapter)
                                        familiaProducto.setText(cargoObject.famproducto.familyProductName, false)
                                        familiaProducto.setOnItemClickListener { parent, view, position, id ->
                                            for (i in 0 until listFamilyProductId.size) {
                                                if (position == i) {
                                                    getFamilyProductSelected = listFamilyProductId[i]
                                                    getFamilyProductNameSelected = listFamilyProduct[i]
                                                }
                                            }
                                        }
                                    }
                                    catch (ex: JSONException){
                                        ex.printStackTrace()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<List<FamilyProduct>>, t: Throwable) {
                                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                            }
                        })

                        //CAMION
                        val truckService: TruckService = RetrofitClients.getUsersClient().create(TruckService::class.java)

                        truckService.getCamionData().enqueue(object : Callback<List<Truck>> {
                            override fun onResponse(call: Call<List<Truck>>, response: Response<List<Truck>>) {
                                if(response.isSuccessful) {
                                    Log.i("Success", response.body().toString())
                                    try {
                                        val mapperC = ObjectMapper()
                                        val listC: List<Truck>? = response.body()
                                        val jsonArray1C: String = mapperC.writeValueAsString(listC)
                                        val jsonArrayC = JSONArray(jsonArray1C)
                                        var i = 0
                                        getObjectTruckPlate = "u"
                                        for(i in 0 until jsonArrayC.length()) {
                                            val jsonObjectC: JSONObject = jsonArrayC.getJSONObject(i)
                                            getObjectTruckId=jsonObjectC.getInt("codigo")
                                            getObjectTruckPlate=jsonObjectC.getString("truckPlate")
                                            listTruckPlate.add(getObjectTruckPlate)
                                            listTrucktId.add(getObjectTruckId)
                                        }
                                        val arrayAdapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, listTruckPlate)
                                        camion.setAdapter(arrayAdapter)
                                        camion.setText(cargoObject.camion.truckPlate, false)
                                        camion.setOnItemClickListener { parent, view, position, id ->
                                            for (i in 0 until listTrucktId.size) {
                                                if (position == i) {
                                                    getTruckSelected = listTrucktId[i]
                                                }
                                            }
                                        }
                                    }
                                    catch (ex: JSONException){
                                        ex.printStackTrace()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<List<Truck>>, t: Throwable) {
                                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                            }
                        })

                        //TRANSPORTISTA
                        val userService: UserService = RetrofitClients.getUsersClient().create(UserService::class.java)

                        userService.getUsersByRolId(2).enqueue(object : Callback<List<User>> {
                            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                                if(response.isSuccessful) {
                                    Log.i("Success", response.body().toString())
                                    try {
                                        val mapperT = ObjectMapper()
                                        val listT: List<User>? = response.body()
                                        val jsonArray1T: String = mapperT.writeValueAsString(listT)
                                        val jsonArrayT = JSONArray(jsonArray1T)
                                        //var i = 0
                                        //getObjectCarrierName = "u"
                                        for(i in 0 until jsonArrayT.length()) {
                                            val jsonObjectT: JSONObject = jsonArrayT.getJSONObject(i)
                                            getCarrierCodigo=jsonObjectT.getInt("codigo")
                                            userService.getPersonByUserId(getCarrierCodigo).enqueue(object:
                                                Callback<Array<Any?>?> {
                                                override fun onResponse(call: Call<Array<Any?>?>, response: Response<Array<Any?>?>) {
                                                    if (response.isSuccessful) {
                                                        Log.i("Success", response.body().toString())
                                                        try {
                                                            val mapperT2 = ObjectMapper()
                                                            val listT2: Array<Any?>? = response.body()
                                                            val jsonArray1T2: String = mapperT2.writeValueAsString(listT2)
                                                            val jsonArrayT2 = JSONArray(jsonArray1T2)
                                                            val jsonArrayT22 = jsonArrayT2.getJSONArray(0)

                                                            val jsonObjectT2: JSONObject = jsonArrayT22.getJSONObject(0)
                                                            getCarrierId = jsonObjectT2.getInt("codigo")
                                                            getObjectCarrierName = jsonObjectT2.getString("personName")
                                                            getObjectCarrierLastName = jsonObjectT2.getString("personLastName")
                                                            CarrierNameAndLastName = "$getObjectCarrierName $getObjectCarrierLastName"

                                                            listCarrier.add(CarrierNameAndLastName)
                                                            listCarrierId.add(getCarrierId)

                                                        }
                                                        catch (ex: JSONException){
                                                            ex.printStackTrace()
                                                        }
                                                    }
                                                }
                                                override fun onFailure(call: Call<Array<Any?>?>, t: Throwable) {
                                                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                                                }
                                            })
                                        }
                                        val arrayAdapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, listCarrier)
                                        transportista.setAdapter(arrayAdapter)
                                        transportista.setText((cargoObject.personDriverId.personName + " " + cargoObject.personDriverId.personLastName), false)
                                        transportista.setOnItemClickListener { parent, view, position, id ->
                                            //println( parent.selectedItem.)
                                            for (i in 0 until listCarrierId.size) {
                                                if (position == i) {
                                                    getCarrierSelected = listCarrierId[i]
                                                }
                                            }
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

                        //CLIENTE
                        userService.getUsersByRolId(1).enqueue(object : Callback<List<User>> {
                            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                                if(response.isSuccessful) {
                                    Log.i("Success", response.body().toString())
                                    try {
                                        val mapperC = ObjectMapper()
                                        val listC: List<User>? = response.body()
                                        val jsonArray1C: String = mapperC.writeValueAsString(listC)
                                        val jsonArrayC = JSONArray(jsonArray1C)
                                        var i = 0
                                        for(i in 0 until jsonArrayC.length()) {
                                            val jsonObjectC: JSONObject = jsonArrayC.getJSONObject(i)
                                            getClientCodigo=jsonObjectC.getInt("codigo")
                                            userService.getPersonByUserId(getClientCodigo).enqueue(object:
                                                Callback<Array<Any?>?> {
                                                override fun onResponse(call: Call<Array<Any?>?>, response: Response<Array<Any?>?>) {
                                                    if (response.isSuccessful) {
                                                        Log.i("Success", response.body().toString())
                                                        try {
                                                            val mapperC2 = ObjectMapper()
                                                            val listC2: Array<Any?>? = response.body()
                                                            val jsonArray1C2: String = mapperC2.writeValueAsString(listC2)
                                                            val jsonArrayC2 = JSONArray(jsonArray1C2)
                                                            val jsonArrayC22 = jsonArrayC2.getJSONArray(0)

                                                            val jsonObjectC2: JSONObject = jsonArrayC22.getJSONObject(0)
                                                            getClientId = jsonObjectC2.getInt("codigo")
                                                            getObjectClientName = jsonObjectC2.getString("personName")
                                                            getObjectClientLastName = jsonObjectC2.getString("personLastName")
                                                            ClientNameAndLastName = "$getObjectClientName $getObjectClientLastName"

                                                            listClient.add(ClientNameAndLastName)
                                                            listClientId.add(getClientId)

                                                        }
                                                        catch (ex: JSONException){
                                                            ex.printStackTrace()
                                                        }
                                                    }
                                                }
                                                override fun onFailure(call: Call<Array<Any?>?>, t: Throwable) {
                                                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                                                }
                                            })
                                        }
                                        val arrayAdapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, listClient)
                                        cliente.setAdapter(arrayAdapter)
                                        cliente.setText((cargoObject.personClientId.personName + " " + cargoObject.personClientId.personLastName), false)
                                        cliente.setOnItemClickListener { parent, view, position, id ->
                                            for (i in 0 until listClientId.size) {
                                                if (position == i) {
                                                    getClientSelected = listClientId[i]
                                                }
                                            }
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

                        //FECHA DE RECOJO
                        val date =
                            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                                calendario.set(Calendar.YEAR, year)
                                calendario.set(Calendar.MONTH, month)
                                calendario.set(Calendar.DAY_OF_MONTH, day)
                                updateLabel()
                            }
                        fechaRecojo.setText(cargoObject.cargoDate)
                        fechaRecojo.setOnClickListener(View.OnClickListener {
                            DatePickerDialog(
                                this@ModifyCargoActivity,
                                date,
                                calendario.get(Calendar.YEAR),
                                calendario.get(Calendar.MONTH),
                                calendario.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        })

                        //HORA DE RECOJO
                        val time =
                            TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                                calendario.set(Calendar.HOUR_OF_DAY, hour)
                                calendario.set(Calendar.MINUTE, minute)
                                updateTimeLabel()
                            }
                        horaRecojo.setText(cargoObject.cargoHour)
                        horaRecojo.setOnClickListener(View.OnClickListener {
                            TimePickerDialog(
                                this@ModifyCargoActivity,
                                time,
                                calendario.get(Calendar.HOUR_OF_DAY),
                                calendario.get(Calendar.MINUTE),
                                true
                            ).show()
                        })

                        //LUGAR DE RECOJO
                        lugarRecojo.setText(cargoObject.cargoInitialUbication)
                        //LUGAR DE ENTREGA
                        lugarEntrega.setText(cargoObject.cargoFinalUbication)


                        if (getFamilyProductSelected == 0 ){
                            getFamilyProductSelected = cargoObject.famproducto.codigo
                            getFamilyProductNameSelected = cargoObject.famproducto.familyProductName
                        }
                        if (getTruckSelected == 0 ){
                            getTruckSelected = cargoObject.camion.codigo
                        }
                        if (getCarrierSelected == 0 ){
                            getCarrierSelected = cargoObject.personDriverId.codigo
                        }
                        if (getClientSelected == 0 ){
                            getClientSelected = cargoObject.personClientId.codigo
                        }


                        //VALIDACIONES

                        nextButton.setOnClickListener(View.OnClickListener {
                            if(familiaProducto.text.isEmpty()
                                || camion.text.isEmpty()
                                || transportista.text.isEmpty()
                                || cliente.text.isEmpty()
                                || fechaRecojo.text.isEmpty()
                                || horaRecojo.text.isEmpty()
                                || lugarRecojo.text.isEmpty()
                                || lugarEntrega.text.isEmpty()
                                || nombreCarga.text.isEmpty()){
                                Toast.makeText(applicationContext, "Ingrese todos la información solicitada",Toast.LENGTH_SHORT).show()
                            } else{
                                //IR A LA SIGUIENTE ACTIVIDAD PARA ELEGIR PRODUCTOS
                                val intent = Intent(applicationContext, ModifyProductsActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                intent.putExtra(FAMILY_ID,getFamilyProductSelected)
                                intent.putExtra(TRUCK_ID,getTruckSelected)
                                intent.putExtra(CARRIER_ID,getCarrierSelected)
                                intent.putExtra(CLIENT_ID,getClientSelected)
                                intent.putExtra(FAMILY_NAME,getFamilyProductNameSelected)
                                intent.putExtra(FECHA_RECOJO,fechaRecojo.text.toString())
                                intent.putExtra(HORA_RECOJO,horaRecojo.text.toString())
                                intent.putExtra(LUGAR_RECOJO,lugarRecojo.text.toString())
                                intent.putExtra(LUGAR_ENTREGA,lugarEntrega.text.toString())
                                intent.putExtra(NOMBRE_CARGA,nombreCarga.text.toString())
                                intent.putExtra(CARGO_ID, cargoObject.codigo)
                                intent.putExtra(CARGO_STATUS,cargoObject.cargoStatus)
                                intent.putExtra(CARGO_ROUTE_STATUS,cargoObject.cargoRouteStatus)
                                intent.putExtra(CARGO_DURATION,cargoObject.cargoRouteDuration)
                                intent.putExtra(CARGO_COMMENT, cargoObject.cargoComments)
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

    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat)
        fechaRecojo.setText(dateFormat.format(calendario.time))
    }

    private fun updateTimeLabel() {
        val myFormat = "HH:mm"
        val timeFormat = SimpleDateFormat(myFormat)
        horaRecojo.setText(timeFormat.format(calendario.time))
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