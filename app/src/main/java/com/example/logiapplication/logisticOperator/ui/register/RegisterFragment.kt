package com.example.logiapplication.logisticOperator.ui.register

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.databinding.LogisticFragmentRegisterBinding
import com.example.logiapplication.interfaces.FamilyProductService
import com.example.logiapplication.interfaces.TruckService
import com.example.logiapplication.interfaces.UserService
import com.example.logiapplication.models.FamilyProduct
import com.example.logiapplication.models.Truck
import com.example.logiapplication.models.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RegisterFragment : Fragment() {
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
    val listCarrierId = ArrayList<Int>()

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
    lateinit var getFamilyProductNameSelected : String
    private var _binding: LogisticFragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var nextButton : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerViewModel =
            ViewModelProvider(this).get(RegisterViewModel::class.java)

        _binding = LogisticFragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        nextButton = binding.root.findViewById(R.id.btn_next_register)
        familiaProducto = binding.root.findViewById(R.id.familyAutoComplete)
        camion = binding.root.findViewById(R.id.camionAutoComplete)
        transportista = binding.root.findViewById(R.id.carrierAutoComplete)
        cliente = binding.root.findViewById(R.id.clientAutoComplete)
        fechaRecojo = binding.root.findViewById(R.id.fechaRecojoAutoComplete)
        horaRecojo = binding.root.findViewById(R.id.horaRecojoAutoComplete)
        lugarRecojo = binding.root.findViewById(R.id.lugarRecojoAutoComplete)
        lugarEntrega = binding.root.findViewById(R.id.lugarEntregaAutoComplete)
        nombreCarga=binding.root.findViewById(R.id.tv_cargo_number)


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
                        var i = 0
                        getObjectFamilyProductName = "u"
                        getFamilyProductNameSelected = "s"
                        for(i in 0 until jsonArrayF.length()) {
                            val jsonObjectF: JSONObject = jsonArrayF.getJSONObject(i)
                            getObjectFamilyProductId=jsonObjectF.getInt("codigo")
                            getObjectFamilyProductName=jsonObjectF.getString("familyProductName")
                            //println(getObjectFamilyProductName)
                            listFamilyProductId.add(getObjectFamilyProductId)
                            listFamilyProduct.add(getObjectFamilyProductName)
                            //println(listFamilyProduct)
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listFamilyProduct)
                        familiaProducto.setAdapter(arrayAdapter)
                        familiaProducto.setOnItemClickListener { parent, view, position, id ->
                            for(i in listFamilyProductId) {
                                if(position+1 == i){
                                    getFamilyProductSelected = position + 1
                                    getFamilyProductNameSelected = listFamilyProduct[position]
                                    println(getFamilyProductNameSelected)

                                }
                            }
                        }
                            /*when (position) {
                                0 -> {
                                    //seleccionar el cargamento para que la info se guarde en ese cargamento y luego presionar el boton iNICIO
                                    //igual para los otros
                                }

                            }*/
                    }
                    catch (ex: JSONException){
                        ex.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<List<FamilyProduct>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
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
                            //println(getObjectFamilyProductName)
                            listTruckPlate.add(getObjectTruckPlate)
                            listTrucktId.add(getObjectTruckId)
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listTruckPlate)
                        camion.setAdapter(arrayAdapter)
                        camion.setOnItemClickListener { parent, view, position, id ->
                            for(i in listTrucktId) {
                                if(position+1 == i){
                                    getTruckSelected = position+1
                                    //println(getTruckSelected)
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
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
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
                            userService.getPersonByUserId(getCarrierCodigo).enqueue(object: Callback<Array<Any?>?> {
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
                                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listCarrier)
                        transportista.setAdapter(arrayAdapter)
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
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
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
                            userService.getPersonByUserId(getClientCodigo).enqueue(object: Callback<Array<Any?>?> {
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
                                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listClient)
                        cliente.setAdapter(arrayAdapter)
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
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        })

        //FECHA DE RECOJO
        val date =
            OnDateSetListener { view, year, month, day ->
                calendario.set(Calendar.YEAR, year)
                calendario.set(Calendar.MONTH, month)
                calendario.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        fechaRecojo.setOnClickListener(View.OnClickListener {
            DatePickerDialog(
                requireContext(),
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
        horaRecojo.setOnClickListener(View.OnClickListener {
            TimePickerDialog(
                requireContext(),
                time,
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true
            ).show()
        })

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
                Toast.makeText(requireContext(), "Ingrese todos la información solicitada",Toast.LENGTH_SHORT).show()
            } else{
                //IR A LA SIGUIENTE ACTIVIDAD PARA ELEGIR PRODUCTOS
                val intent = Intent(requireContext(), NextRegisterActivity::class.java)
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
                startActivity(intent)
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //@SuppressLint("SimpleDateFormat")
    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat)
        fechaRecojo.setText(dateFormat.format(calendario.time))
    }

    //@SuppressLint("SimpleDateFormat")
    private fun updateTimeLabel() {
        val myFormat = "HH:mm"
        val timeFormat = SimpleDateFormat(myFormat)
        horaRecojo.setText(timeFormat.format(calendario.time))
    }
}