package com.example.logiapplication.client.ui.followup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.client.ClientMainActivity
import com.example.logiapplication.client.ui.followup.ClientFollowUpFragment.Companion.CARGA_ID
import com.example.logiapplication.databinding.ClientViewRegisterActivityBinding
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.interfaces.ProductCargoService
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.logisticOperator.ui.followup.FollowUpFragment
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

class ClientViewRegisterActivity: AppCompatActivity() {
    private lateinit var binding: ClientViewRegisterActivityBinding

    lateinit var backButton : Button

    lateinit var sharedPreferences: SharedPreferences
    var getCargoId : Int = 0
    lateinit var cargoNombre : TextView
    lateinit var cargoFamilia : TextView
    lateinit var cargoCamion : TextView
    lateinit var cargoCarrier : TextView
    lateinit var cargoClient : TextView
    lateinit var cargoDate : TextView
    lateinit var cargoHour : TextView
    lateinit var cargoLugarRecojo : TextView
    lateinit var cargoLugarEntrega : TextView
    lateinit var cargoRouteStatus : TextView
    lateinit var cargoComment : AutoCompleteTextView


    lateinit var getCrates : String
    lateinit var getProductName : String
    var getProductId : Int = 0
    val listProductId = ArrayList<Int>()
    val listCrates = ArrayList<String>()
    val listProductName = ArrayList<String>()

    lateinit var table : TableLayout
    lateinit var tableRow : TableRow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ClientViewRegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        backButton = binding.root.findViewById(R.id.btn_back_C)
        cargoNombre = binding.root.findViewById(R.id.tv_cargo_number_C)
        cargoFamilia = binding.root.findViewById(R.id.tv_family_text_C)
        cargoCamion = binding.root.findViewById(R.id.tv_camion_text_C)
        cargoCarrier = binding.root.findViewById(R.id.tv_carrier_text_C)
        cargoClient = binding.root.findViewById(R.id.tv_client_text_C)
        cargoDate = binding.root.findViewById(R.id.tv_fecha_recojo_text_C)
        cargoHour = binding.root.findViewById(R.id.tv_hora_recojo_text_C)
        cargoLugarRecojo = binding.root.findViewById(R.id.tv_lugar_recojo_text_C)
        cargoLugarEntrega = binding.root.findViewById(R.id.tv_lugar_entrega_text_C)
        cargoRouteStatus = binding.root.findViewById(R.id.tv_route_status_c)
        cargoComment = binding.root.findViewById(R.id.commentAutoComplete)


        table = binding.root.findViewById(R.id.products_table_C)


        getCargoId = sharedPreferences.getInt(CARGA_ID, 0)

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
                        cargoFamilia.text = cargoObject.famproducto.familyProductName
                        cargoCamion.text = cargoObject.camion.truckPlate
                        cargoCarrier.text = (cargoObject.personDriverId.personName + " " + cargoObject.personDriverId.personLastName)
                        cargoClient.text = (cargoObject.personClientId.personName + " " + cargoObject.personClientId.personLastName)
                        cargoDate.text = cargoObject.cargoDate
                        cargoHour.text = cargoObject.cargoHour
                        cargoLugarRecojo.text = cargoObject.cargoInitialUbication
                        cargoLugarEntrega.text = cargoObject.cargoFinalUbication
                        cargoRouteStatus.text = "Estado de carga: " + cargoObject.cargoRouteStatus
                        cargoComment.setText(cargoObject.cargoComments)
                        val productCargoService: ProductCargoService = RetrofitClients.getUsersClient().create(
                            ProductCargoService::class.java)
                        productCargoService.getProductCargoByCargoId(getCargoId).enqueue(object :
                            Callback<List<ProductCargo>> {
                            override fun onResponse(call: Call<List<ProductCargo>>, response: Response<List<ProductCargo>>) {
                                if(response.isSuccessful) {
                                    Log.i("Success", response.body().toString())
                                    try {
                                        val mapperP = ObjectMapper()
                                        val listP: List<ProductCargo>? = response.body()
                                        val jsonArray1P: String = mapperP.writeValueAsString(listP)
                                        val jsonArrayP = JSONArray(jsonArray1P)
                                        for(i in 0 until jsonArrayP.length()) {
                                            val jsonObjectP: JSONObject = jsonArrayP.getJSONObject(i)
                                            val productCargoObject = Gson().fromJson(jsonObjectP.toString(), ProductCargo::class.java)
                                            getCrates = productCargoObject.productCargoCrates
                                            getProductId = productCargoObject.producto.codigo
                                            getProductName = productCargoObject.producto.productName
                                            listProductId.add(getProductId)
                                            listProductName.add(getProductName)
                                            listCrates.add(getCrates)
                                        }
                                        init()

                                    }
                                    catch (ex: JSONException){
                                        ex.printStackTrace()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<List<ProductCargo>>, t: Throwable) {
                                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
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

        backButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ClientMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        })
    }

    fun init() {
        val row0 = TableRow(this)

        val tv1 = TextView(this)
        tv1.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            3f
        )
        tv1.text = " Producto "
        tv1.setTextColor(Color.BLACK)
        tv1.setBackgroundColor(Color.LTGRAY)
        tv1.gravity = Gravity.CENTER
        row0.addView(tv1)

        val tv2 = TextView(this)
        tv2.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            3f
        )
        tv2.text = " Nº Jabas "
        tv2.setTextColor(Color.BLACK)
        tv2.setBackgroundColor(Color.LTGRAY)
        tv2.gravity = Gravity.CENTER
        row0.addView(tv2)
        table.addView(row0)
        for (i in 0 until listProductId.size) {
            val tbrow = TableRow(this)
            val lp: TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
            tbrow.layoutParams = lp

            val tv = TextView(this)
            tv.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                3f
            )
            //obtener el nombre del producto
            tv.text = listProductName[i]
            tv.gravity = Gravity.CENTER
            tv.setTextColor(Color.BLACK)
            tv.setBackgroundColor(Color.WHITE)
            tv.setPadding(5, 5, 5, 5)
            tbrow.addView(tv)

            val t2v = TextView(this)
            t2v.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                3f
            )
            t2v.text = listCrates[i]
            t2v.gravity = Gravity.CENTER
            t2v.setTextColor(Color.BLACK)
            t2v.setBackgroundColor(Color.WHITE)
            t2v.setPadding(5, 5, 5, 5)
            tbrow.addView(t2v)
            table.addView(tbrow)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.client_main, menu)
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
        val alerta: AlertDialog.Builder = AlertDialog.Builder(this)
        alerta.setMessage("¿Desea salir de la aplicación?")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, which ->
                val editor: SharedPreferences.Editor=sharedPreferences.edit()
                editor.clear()
                editor.apply()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.cancel()
            }
        val titulo: AlertDialog = alerta.create()
        titulo.setTitle("Cerrar sesión")
        titulo.show()
    }
}