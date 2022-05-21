package com.example.logiapplication.logisticOperator.ui.followup

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
import androidx.core.view.isVisible
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.carrier.CarrierMainActivity
import com.example.logiapplication.carrier.ui.syncup.ConnectionActivity
import com.example.logiapplication.carrier.ui.time.TimeFragment
import com.example.logiapplication.client.ClientMainActivity
import com.example.logiapplication.client.ClientMainActivity.Companion.UserCodigo
import com.example.logiapplication.databinding.LogisticModifyProductsActivityBinding
import com.example.logiapplication.interfaces.*
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.CARGO_COMMENT
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.CARGO_DURATION
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.CARGO_ID
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.CARGO_ROUTE_STATUS
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.CARGO_STATUS
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.CARRIER_ID
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.CLIENT_ID
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.FAMILY_ID
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.FAMILY_NAME
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.FECHA_RECOJO
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.HORA_RECOJO
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.LUGAR_ENTREGA
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.LUGAR_RECOJO
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.NOMBRE_CARGA
import com.example.logiapplication.logisticOperator.ui.followup.ModifyCargoActivity.Companion.TRUCK_ID
import com.example.logiapplication.logisticOperator.ui.register.RegisterFragment
import com.example.logiapplication.models.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyProductsActivity:AppCompatActivity() {
    private lateinit var binding: LogisticModifyProductsActivityBinding


    lateinit var sharedPreferences: SharedPreferences
    var getOperatorCodigo : Int = 0
    var getFamilyProductId : Int = 0
    lateinit var getFamilyProductName : String
    var getTruckId : Int = 0
    var getCarrierId : Int = 0
    var getClientId : Int = 0
    lateinit var getProductName : String
    var getProductId : Int = 0
    val listProductId = ArrayList<Int>()
    val listProductName = ArrayList<String>()
    lateinit var tvFamily : TextView
    lateinit var getFechaRecojo : String
    lateinit var getHoraRecojo : String
    lateinit var getLugarRecojo : String
    lateinit var getLugarEntrega : String
    lateinit var getNombreCarga : String
    var getCargoId : Int = 0
    lateinit var getCargoStatus : String
    lateinit var getCargoComments : String
    lateinit var getCargoRouteStatus : String
    lateinit var getCargoDuration : String
    var getListProductCarga = ArrayList<Int>()
    var getProductCargaId: Int = 0


    lateinit var botonRegistrar : Button
    lateinit var botonRegistrarProductos : Button
    lateinit var botonCancel: Button
    lateinit var tv_nombre_carga : TextView

    lateinit var table : TableLayout
    val listCheckbox = ArrayList<CheckBox>()
    lateinit var itemTableCheckBox: CheckBox
    lateinit var tableRow : TableRow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)


        binding = LogisticModifyProductsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        botonRegistrar = binding.root.findViewById(R.id.btn_save_cargo)
        botonRegistrarProductos = binding.root.findViewById(R.id.btn_save_products)
        table = binding.root.findViewById(R.id.add_product_table)
        tv_nombre_carga = binding.root.findViewById(R.id.tv_cargo_nro_L)

        //DE LA CARGA
        getOperatorCodigo = sharedPreferences.getInt(UserCodigo, 0)
        getFamilyProductId = intent.getIntExtra(FAMILY_ID, 0)
        getTruckId = intent.getIntExtra(TRUCK_ID, 0)
        getCarrierId = intent.getIntExtra(CARRIER_ID, 0)
        getClientId = intent.getIntExtra(CLIENT_ID, 0)
        getFamilyProductName = intent.getStringExtra(FAMILY_NAME).toString()
        getFechaRecojo = intent.getStringExtra(FECHA_RECOJO).toString()
        getHoraRecojo = intent.getStringExtra(HORA_RECOJO).toString()
        getLugarRecojo = intent.getStringExtra(LUGAR_RECOJO).toString()
        getLugarEntrega = intent.getStringExtra(LUGAR_ENTREGA).toString()
        getNombreCarga = intent.getStringExtra(NOMBRE_CARGA).toString()
        getCargoId = intent.getIntExtra(CARGO_ID, 0)
        getCargoStatus = intent.getStringExtra(CARGO_STATUS).toString()
        getCargoDuration = intent.getStringExtra(CARGO_DURATION).toString()
        getCargoRouteStatus = intent.getStringExtra(CARGO_ROUTE_STATUS).toString()
        getCargoComments = intent.getStringExtra(CARGO_COMMENT).toString()


        //DE LA FAMILIA DE PRODUCTOS
        tvFamily = binding.root.findViewById(R.id.tv_family_name_L)
        tvFamily.text = getFamilyProductName
        tv_nombre_carga.text = getNombreCarga
        botonCancel = binding.root.findViewById(R.id.btn_cancel_cargo)


        botonCancel.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ModifyCargoActivity ::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        })

        //PRODUCTOS
        val productService: ProductService = RetrofitClients.getUsersClient().create(ProductService::class.java)
        productService.getProductosByFamilyId(getFamilyProductId).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if(response.isSuccessful) {
                    Log.i("Success", response.body().toString())
                    try {
                        val mapperP = ObjectMapper()
                        val listP: List<Product>? = response.body()
                        val jsonArray1P: String = mapperP.writeValueAsString(listP)
                        val jsonArrayP = JSONArray(jsonArray1P)
                        for(i in 0 until jsonArrayP.length()) {
                            val jsonObjectP: JSONObject = jsonArrayP.getJSONObject(i)
                            getProductId=jsonObjectP.getInt("codigo")
                            getProductName=jsonObjectP.getString("productName")
                            listProductId.add(getProductId)
                            listProductName.add(getProductName)
                        }
                        init()
                        //OBTENER LOS DATOS DE CAMION, FAMILIA DE PRODUCTO, CLIENTE, TRANSPORTISTA, OPERADOR
                        val truckService: TruckService = RetrofitClients.getUsersClient().create(
                            TruckService::class.java)
                        truckService.getTruck(getTruckId).enqueue(object : Callback<Truck> {
                            override fun onResponse(call: Call<Truck>, response: Response<Truck>) {
                                if(response.isSuccessful) {
                                    Log.i("Success", response.body().toString())
                                    try {
                                        //val mapperT = ObjectMapper()
                                        val truck: Truck? = response.body()
                                        val jsonObjectTruck = JSONObject(Gson().toJson(truck))
                                        val truckObject = Gson().fromJson(jsonObjectTruck.toString(), Truck::class.java)
                                        val familyProductService: FamilyProductService = RetrofitClients.getUsersClient().create(FamilyProductService::class.java)
                                        familyProductService.getAFamilyProduct(getFamilyProductId).enqueue(object : Callback<FamilyProduct> {
                                            override fun onResponse(call: Call<FamilyProduct>, response: Response<FamilyProduct>) {
                                                if(response.isSuccessful) {
                                                    Log.i("Success", response.body().toString())
                                                    try {
                                                        //val mapperT = ObjectMapper()
                                                        val familyProduct: FamilyProduct? = response.body()
                                                        val jsonObjectFamilyProduct = JSONObject(
                                                            Gson().toJson(familyProduct))
                                                        val familyProductObject = Gson().fromJson(jsonObjectFamilyProduct.toString(), FamilyProduct::class.java)
                                                        val personService: PersonService = RetrofitClients.getUsersClient().create(
                                                            PersonService::class.java)
                                                        personService.getPerson(getCarrierId).enqueue(object : Callback<Person> {
                                                            override fun onResponse(call: Call<Person>, response: Response<Person>) {
                                                                if(response.isSuccessful) {
                                                                    Log.i("Success", response.body().toString())
                                                                    try {
                                                                        //val mapperT = ObjectMapper()
                                                                        val carrier: Person? = response.body()
                                                                        val jsonObjectCarrier = JSONObject(
                                                                            Gson().toJson(carrier))
                                                                        val carrierObject = Gson().fromJson(jsonObjectCarrier.toString(), Person::class.java)
                                                                        personService.getPerson(getClientId).enqueue(object : Callback<Person> {
                                                                            override fun onResponse(call: Call<Person>, response: Response<Person>) {
                                                                                if(response.isSuccessful) {
                                                                                    Log.i("Success", response.body().toString())
                                                                                    try {
                                                                                        //val mapperT = ObjectMapper()
                                                                                        val client: Person? = response.body()
                                                                                        val jsonObjectClient = JSONObject(
                                                                                            Gson().toJson(client))
                                                                                        val clientObject = Gson().fromJson(jsonObjectClient.toString(), Person::class.java)
                                                                                        personService.getPerson(getOperatorCodigo).enqueue(object : Callback<Person> {
                                                                                            override fun onResponse(call: Call<Person>, response: Response<Person>) {
                                                                                                if(response.isSuccessful) {
                                                                                                    Log.i("Success", response.body().toString())
                                                                                                    try {
                                                                                                        //val mapperT = ObjectMapper()
                                                                                                        val operator: Person? = response.body()
                                                                                                        val jsonObjectOperator = JSONObject(
                                                                                                            Gson().toJson(operator))
                                                                                                        val operatorObject = Gson().fromJson(jsonObjectOperator.toString(), Person::class.java)
                                                                                                        val cargoData = Cargo(  codigo = getCargoId,
                                                                                                            cargoName = getNombreCarga,
                                                                                                            cargoDate = getFechaRecojo,
                                                                                                            cargoHour = getHoraRecojo,
                                                                                                            cargoInitialUbication = getLugarRecojo,
                                                                                                            cargoFinalUbication = getLugarEntrega,
                                                                                                            cargoStatus = getCargoStatus,
                                                                                                            cargoRouteDuration = getCargoDuration,
                                                                                                            cargoRouteStatus = getCargoRouteStatus,
                                                                                                            camion = truckObject,
                                                                                                            famproducto = familyProductObject,
                                                                                                            personClientId = clientObject,
                                                                                                            personOperatorId = operatorObject,
                                                                                                            personDriverId = carrierObject,
                                                                                                            cargoComments = getCargoComments
                                                                                                        )

                                                                                                        //println(cargoData)
                                                                                                        botonRegistrar.setOnClickListener(View.OnClickListener{
                                                                                                            updateCargo(cargoData, getCargoId) {
                                                                                                                //Toast.makeText(applicationContext, "Se actualizó la carga. Ahora actualice los productos", Toast.LENGTH_SHORT).show()
                                                                                                                if (it?.codigo != null) {
                                                                                                                    Toast.makeText(applicationContext, "Se actualizó la carga. Ahora actualice los productos", Toast.LENGTH_SHORT).show()

                                                                                                                } else {
                                                                                                                    Toast.makeText(applicationContext, "Error al actualizar la carga", Toast.LENGTH_SHORT).show()
                                                                                                                }
                                                                                                            }
                                                                                                            botonRegistrar.isVisible=false

                                                                                                            botonRegistrarProductos.setOnClickListener(View.OnClickListener{
                                                                                                                val contador = ArrayList<Int>()
                                                                                                                val texto = ArrayList<String>()
                                                                                                                for (i in 0 until table.childCount-1) {
                                                                                                                    tableRow = table.getChildAt(i+1) as TableRow
                                                                                                                    itemTableCheckBox = tableRow.getChildAt(0) as CheckBox
                                                                                                                    if (itemTableCheckBox.isChecked) {
                                                                                                                        //se guarda en contador el numero de la fila
                                                                                                                        contador.add(i)
                                                                                                                        val crates = tableRow.getChildAt(2) as AutoCompleteTextView
                                                                                                                        texto.add(crates.text.toString())
                                                                                                                    }
                                                                                                                }
                                                                                                                //PRODUCT CARGO
                                                                                                                val productCargoService : ProductCargoService = RetrofitClients.getUsersClient().create(ProductCargoService::class.java)
                                                                                                                productCargoService.getProductCargoByCargoId(getCargoId).enqueue(object : Callback<List<ProductCargo>> {
                                                                                                                    override fun onResponse(call: Call<List<ProductCargo>>, response: Response<List<ProductCargo>>) {
                                                                                                                        if(response.isSuccessful) {
                                                                                                                            Log.i("Success", response.body().toString())
                                                                                                                            try {
                                                                                                                                val mapperPC = ObjectMapper()
                                                                                                                                val listPC: List<ProductCargo>? = response.body()
                                                                                                                                val jsonArray1PC: String = mapperPC.writeValueAsString(listPC)
                                                                                                                                val jsonArrayPC = JSONArray(jsonArray1PC)
                                                                                                                                for(i in 0 until jsonArrayPC.length()) {
                                                                                                                                    val jsonObjectPC: JSONObject = jsonArrayPC.getJSONObject(i)
                                                                                                                                    getProductCargaId=jsonObjectPC.getInt("codigo")
                                                                                                                                    getListProductCarga.add(getProductCargaId)
                                                                                                                                }

                                                                                                                                if(contador.size==0 ) {
                                                                                                                                    Toast.makeText(applicationContext, "Por favor seleccione los productos para actualizar la carga", Toast.LENGTH_LONG).show()
                                                                                                                                }
                                                                                                                                else {
                                                                                                                                    for (i in contador) {
                                                                                                                                        // ingresar al producto id
                                                                                                                                        //Toast.makeText(applicationContext,listProductId[i] .toString(), Toast.LENGTH_LONG).show()
                                                                                                                                        productService.getProduct(listProductId[i]).enqueue(object : Callback<Product>{
                                                                                                                                            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                                                                                                                                                if(response.isSuccessful) {
                                                                                                                                                    Log.i("Success", response.body().toString())
                                                                                                                                                    try {
                                                                                                                                                        val product: Product? = response.body()
                                                                                                                                                        val jsonObjectProduct = JSONObject(Gson().toJson(product))
                                                                                                                                                        val productObject = Gson().fromJson(jsonObjectProduct.toString(), Product::class.java)
                                                                                                                                                        val cargoService: CargoService = RetrofitClients.getUsersClient().create(CargoService::class.java)
                                                                                                                                                        //val listCargoId = ArrayList<Int>()
                                                                                                                                                        cargoService.getCargo(getCargoId).enqueue(object : Callback<Cargo>{
                                                                                                                                                            override fun onResponse(call: Call<Cargo>, response: Response<Cargo>) {
                                                                                                                                                                if(response.isSuccessful) {
                                                                                                                                                                    Log.i("Success", response.body().toString())
                                                                                                                                                                    try {
                                                                                                                                                                        val cargoActual: Cargo? = response.body()
                                                                                                                                                                        val jsonObjectCargo = JSONObject(Gson().toJson(cargoActual))
                                                                                                                                                                        val cargoObject = Gson().fromJson(jsonObjectCargo.toString(), Cargo::class.java)

                                                                                                                                                                        val productCargoData = ProductCargo (codigo = getListProductCarga[i],
                                                                                                                                                                            productCargoCrates = texto[i],
                                                                                                                                                                            cargo = cargoObject,
                                                                                                                                                                            producto = productObject
                                                                                                                                                                        )
                                                                                                                                                                        //pasar lista de codigo, y lugo lista[i](este es el id)
                                                                                                                                                                        updateProductCargo(productCargoData, getListProductCarga[i]) {
                                                                                                                                                                            if (it?.codigo != null) {
                                                                                                                                                                                // it = newly added user parsed as response
                                                                                                                                                                                // it?.id = newly added user ID
                                                                                                                                                                                Toast.makeText(applicationContext, "Se actualizaron los productos de la carga", Toast.LENGTH_SHORT).show()
                                                                                                                                                                                val intent = Intent(this@ModifyProductsActivity, LogisticMainActivity::class.java)
                                                                                                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                                                                                                                                                                startActivity(intent)
                                                                                                                                                                            } else {
                                                                                                                                                                                Toast.makeText(applicationContext, "Error al actualizar los productos de la carga", Toast.LENGTH_SHORT).show()
                                                                                                                                                                            }
                                                                                                                                                                        }
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
                                                                                                                                                    catch (ex: JSONException){
                                                                                                                                                        ex.printStackTrace()
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                            override fun onFailure(call: Call<Product>, t: Throwable) {
                                                                                                                                                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                                                                                                                                            }
                                                                                                                                        })

                                                                                                                                    }
                                                                                                                                }


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
                                                                                                            })
                                                                                                        })
                                                                                                    }
                                                                                                    catch (ex: JSONException){
                                                                                                        ex.printStackTrace()
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                            override fun onFailure(call: Call<Person>, t: Throwable) {
                                                                                                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                                                                                            }
                                                                                        })

                                                                                    }
                                                                                    catch (ex: JSONException){
                                                                                        ex.printStackTrace()
                                                                                    }
                                                                                }
                                                                            }
                                                                            override fun onFailure(call: Call<Person>, t: Throwable) {
                                                                                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                        })
                                                                    }
                                                                    catch (ex: JSONException){
                                                                        ex.printStackTrace()
                                                                    }
                                                                }
                                                            }
                                                            override fun onFailure(call: Call<Person>, t: Throwable) {
                                                                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                                                            }
                                                        })
                                                    }
                                                    catch (ex: JSONException){
                                                        ex.printStackTrace()
                                                    }
                                                }
                                            }
                                            override fun onFailure(call: Call<FamilyProduct>, t: Throwable) {
                                                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                                            }
                                        })
                                    }
                                    catch (ex: JSONException){
                                        ex.printStackTrace()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<Truck>, t: Throwable) {
                                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    catch (ex: JSONException){
                        ex.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
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

    fun updateProductCargo(productCargoData: ProductCargo, id: Int, onResult: (ProductCargo?) -> Unit) {
        val productCargoService: ProductCargoService = RetrofitClients.getUsersClient().create(ProductCargoService::class.java)
        productCargoService.updateProductCargo(productCargoData, id).enqueue(object :
            Callback<ProductCargo> {
            override fun onResponse(call: Call<ProductCargo>, response: Response<ProductCargo>) {
                val updatedProductCargo = response.body()
                onResult(updatedProductCargo)
            }
            override fun onFailure(call: Call<ProductCargo>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun init() {
        val row0 = TableRow(this)

        val tv0 = TextView(this)
        tv0.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.5f
        )
        tv0.text = " Agregar "
        tv0.setTextColor(Color.BLACK)
        tv0.setBackgroundColor(Color.LTGRAY)
        tv0.gravity = Gravity.CENTER
        row0.addView(tv0)

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

            val chec = CheckBox(this)
            chec.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
            //primero se obtiene la fila, luego el elemento, luego se ve si esta clicked
            chec.setBackgroundColor(Color.WHITE)
            chec.setPadding(5, 5, 5, 5)
            chec.gravity = Gravity.CENTER
            listCheckbox.add(chec)
            tbrow.addView(chec)

            val tv = TextView(this)
            tv.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                3f
            )
            tv.text = listProductName[i]
            tv.gravity = Gravity.CENTER
            tv.setTextColor(Color.BLACK)
            tv.setBackgroundColor(Color.WHITE)
            tv.setPadding(5, 5, 5, 5)
            tbrow.addView(tv)

            val t2v = AutoCompleteTextView(this)
            t2v.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                3f
            )
            t2v.hint = " Cantidad "
            t2v.gravity = Gravity.CENTER
            t2v.setTextColor(Color.BLACK)
            t2v.setBackgroundColor(Color.WHITE)
            //t2v.inputType = InputType.TYPE_NULL
            t2v.setPadding(5, 5, 5, 5)
            tbrow.addView(t2v)
            table.addView(tbrow)
        }
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
        val alerta: AlertDialog.Builder = AlertDialog.Builder(this)
        alerta.setMessage("¿Desea salir de la aplicación?")
            .setCancelable(false)
            .setPositiveButton("Si") { dialog, which ->
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