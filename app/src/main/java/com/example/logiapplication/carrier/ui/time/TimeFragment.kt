package com.example.logiapplication.carrier.ui.time

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.carrier.CarrierMainActivity
import com.example.logiapplication.carrier.ui.syncup.ConnectionActivity
import com.example.logiapplication.databinding.CarrierFragmentTimeBinding
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.models.Cargo
import com.example.logiapplication.models.FamilyProduct
import com.example.logiapplication.models.Person
import com.example.logiapplication.models.Truck
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TimeFragment : Fragment() {

    private lateinit var timeViewModel: TimeViewModel
    private var _binding: CarrierFragmentTimeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var sharedPreferences: SharedPreferences

    var getCarrierCodigo : Int = 0

    lateinit var btn_inicio : Button
    lateinit var cargo : AutoCompleteTextView

    lateinit var getObjectCargoName : String
    lateinit var getCargoNameSelected : String
    val cargoNameList = ArrayList<String>()

    var getObjectCargoId: Int = 0
    var getCargoIdSelected : Int = 0
    val cargoIdList = ArrayList<Int>()

    lateinit var getObjectCargoDate : String
    lateinit var getCargoDateSelected : String
    val cargoDateList = ArrayList<String>()

    lateinit var getObjectCargoHour : String
    lateinit var getCargoHourSelected : String
    val cargoHourList = ArrayList<String>()

    lateinit var getObjectCargoInitial : String
    lateinit var getCargoInitialSelected : String
    val cargoInitialList = ArrayList<String>()

    lateinit var getObjectCargoFinal : String
    lateinit var getCargoFinalSelected : String
    val cargoFinalList = ArrayList<String>()


    lateinit var getObjectCargoDuration : String
    lateinit var getCargoDurationSelected : String
    val cargoDurationList = ArrayList<String>()

    lateinit var getObjectCargoRoute : String
    lateinit var getCargoRouteSelected : String
    val cargoRouteList = ArrayList<String>()

    lateinit var getObjectCargoComments : String
    lateinit var getCargoCommentsSelected : String
    val cargoCommentsList = ArrayList<String>()

    lateinit var getObjectCargoTruck : Truck
    var getCargoTruckSelected : Truck = Truck(0, "", "", 0, 0, 0)
    val cargoTruckList = ArrayList<Truck>()

    lateinit var getObjectCargoFamily : FamilyProduct
    var getCargoFamilySelected : FamilyProduct = FamilyProduct(0, "", 0.0, 0.0, 0.0, 0.0, 0.0)
    val cargoFamilyList = ArrayList<FamilyProduct>()

    lateinit var getObjectCargoClient : Person
    var getCargoClientSelected : Person = Person(0, "", "", "", "", "", "")
    val cargoClientList = ArrayList<Person>()

    lateinit var getObjectCargoOperator : Person
    var getCargoOperatorSelected : Person = Person(0, "", "", "", "", "", "")
    val cargoOperatorList = ArrayList<Person>()

    lateinit var getObjectCargoCarrier : Person
    var getCargoCarrierSelected : Person = Person(0, "", "", "", "", "", "")
    val cargoCarrierList = ArrayList<Person>()


    companion object {
        var CARGO = "cargo_id"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        getCarrierCodigo = sharedPreferences.getInt(CarrierMainActivity.UserCodigo, 0)
        println(getCarrierCodigo)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        timeViewModel =
            ViewModelProvider(this).get(TimeViewModel::class.java)

        _binding = CarrierFragmentTimeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        //getCarrierCodigo = sharedPreferences.getInt(CarrierMainActivity.UserCodigo, 0)
        //LO RECIBE DEL LOGIN (IDK WHY IT CAN'T FROM CARRIER MAIN ACTIVITY
        getCarrierCodigo = sharedPreferences.getInt("DRIVER", 0)


        cargo = binding.root.findViewById(R.id.cargoAutoCompleteTextView)
        btn_inicio = binding.root.findViewById(R.id.btn_inicio_ruta)

        val cargoService: CargoService = RetrofitClients.getUsersClient().create(CargoService::class.java)
        cargoService.getCargoByCarrier(getCarrierCodigo).enqueue(object : Callback<List<Cargo>> {
            override fun onResponse(call: Call<List<Cargo>>, response: Response<List<Cargo>>) {
                if(response.isSuccessful) {
                    Log.i("Success", response.body().toString())
                    try {
                        val mapperC = ObjectMapper()
                        val listC: List<Cargo>? = response.body()
                        val jsonArray1C: String = mapperC.writeValueAsString(listC)
                        val jsonArrayC = JSONArray(jsonArray1C)
                        var i = 0
                        getObjectCargoName = "u"
                        getCargoNameSelected = "s"
                        getObjectCargoDate = "u"
                        getCargoDateSelected = "s"
                        getObjectCargoHour = "u"
                        getCargoHourSelected = "s"
                        getObjectCargoInitial = "u"
                        getCargoInitialSelected = "s"
                        getObjectCargoFinal = "u"
                        getCargoFinalSelected = "s"
                        getObjectCargoRoute = "u"
                        getCargoRouteSelected = "s"
                        getObjectCargoDuration = "u"
                        getCargoDurationSelected = "s"
                        getObjectCargoComments = "u"
                        getCargoCommentsSelected = "s"
                        for(i in 0 until jsonArrayC.length()) {
                            val jsonObjectF: JSONObject = jsonArrayC.getJSONObject(i)
                            //val jsonObjectCargo = JSONObject(Gson().toJson(jsonObjectF))
                            val cargoObject = Gson().fromJson(jsonObjectF.toString(), Cargo::class.java)
                            getObjectCargoId=jsonObjectF.getInt("codigo")
                            getObjectCargoName=cargoObject.cargoName
                            getObjectCargoDate = cargoObject.cargoDate
                            getObjectCargoHour = cargoObject.cargoHour
                            getObjectCargoInitial = cargoObject.cargoInitialUbication
                            getObjectCargoFinal=cargoObject.cargoFinalUbication
                            getObjectCargoRoute=cargoObject.cargoRouteStatus
                            getObjectCargoDuration=cargoObject.cargoRouteDuration
                            getObjectCargoTruck = cargoObject.camion
                            getObjectCargoFamily = cargoObject.famproducto
                            getObjectCargoClient = cargoObject.personClientId
                            getObjectCargoOperator = cargoObject.personOperatorId
                            getObjectCargoCarrier = cargoObject.personDriverId
                            getObjectCargoComments = cargoObject.cargoComments
                            if(cargoObject.cargoStatus == "Registrado") {
                                cargoIdList.add(getObjectCargoId)
                                cargoNameList.add(getObjectCargoName)
                                cargoDateList.add(getObjectCargoDate)
                                cargoHourList.add(getObjectCargoHour)
                                cargoInitialList.add(getObjectCargoInitial)
                                cargoFinalList.add(getObjectCargoFinal)
                                cargoRouteList.add(getObjectCargoRoute)
                                cargoDurationList.add(getObjectCargoDuration)
                                cargoTruckList.add(getObjectCargoTruck)
                                cargoClientList.add(getObjectCargoClient)
                                cargoFamilyList.add(getObjectCargoFamily)
                                cargoOperatorList.add(getObjectCargoOperator)
                                cargoCarrierList.add(getObjectCargoCarrier)
                                cargoCommentsList.add(getObjectCargoComments)
                            }
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, cargoNameList)
                        cargo.setAdapter(arrayAdapter)
                        cargo.setOnItemClickListener { parent, view, position, id ->
                            for (i in 0 until cargoIdList.size) {
                                if (position == i) {
                                    getCargoIdSelected = cargoIdList[i]
                                    getCargoNameSelected = cargoNameList[i]
                                    getCargoDateSelected = cargoDateList[i]
                                    getCargoHourSelected = cargoHourList[i]
                                    getCargoInitialSelected = cargoInitialList[i]
                                    getCargoFinalSelected = cargoFinalList[i]
                                    getCargoRouteSelected = cargoRouteList[i]
                                    getCargoDurationSelected = cargoDurationList[i]
                                    getCargoTruckSelected = cargoTruckList[i]
                                    getCargoFamilySelected = cargoFamilyList[i]
                                    getCargoClientSelected = cargoClientList[i]
                                    getCargoOperatorSelected = cargoOperatorList[i]
                                    getCargoCarrierSelected = cargoCarrierList[i]
                                    getCargoCommentsSelected = cargoCommentsList[i]
                                }
                            }
                            val cargoData = Cargo(  codigo = getCargoIdSelected,
                                cargoName = getCargoNameSelected,
                                cargoDate = getCargoDateSelected,
                                cargoHour = getCargoHourSelected,
                                cargoInitialUbication = getCargoInitialSelected,
                                cargoFinalUbication = getCargoFinalSelected,
                                cargoStatus = "En ruta",
                                cargoRouteDuration = getCargoDurationSelected,
                                cargoRouteStatus = getCargoRouteSelected,
                                camion = getCargoTruckSelected,
                                famproducto = getCargoFamilySelected,
                                personClientId = getCargoClientSelected,
                                personOperatorId = getCargoOperatorSelected,
                                personDriverId = getCargoCarrierSelected,
                                cargoComments = getCargoCommentsSelected
                            )
                            btn_inicio.setOnClickListener(View.OnClickListener {
                                //Toast.makeText(requireContext(), getCargoIdSelected.toString(),Toast.LENGTH_SHORT).show()

                                if (cargo.text.isEmpty()) {
                                    Toast.makeText(requireContext(), "Seleccione un cargamento",Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    updateCargo(cargoData, getCargoIdSelected) {
                                        if (it?.codigo != null) {
                                            Toast.makeText(requireContext(), "Se inició ruta de carga", Toast.LENGTH_SHORT).show()
                                           //esto
                                            val intent = Intent(requireContext(), ConnectionActivity::class.java)
                                            //
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                            intent.putExtra(CARGO,getCargoIdSelected)
                                            startActivity(intent)

                                        } else {
                                            Toast.makeText(requireContext(), "Error al iniciar ruta de carga", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            })
                        }
                    }
                    catch (ex: JSONException){
                        ex.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<List<Cargo>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        })
        return root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}