package com.example.logiapplication.logisticOperator.ui.register

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.carrier.CarrierMainActivity
import com.example.logiapplication.carrier.ui.syncup.ConnectionActivity
import com.example.logiapplication.client.ClientMainActivity
import com.example.logiapplication.databinding.LogisticFragmentRegisterBinding
import com.example.logiapplication.interfaces.FamilyProductService
import com.example.logiapplication.interfaces.TruckService
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.models.FamilyProduct
import com.example.logiapplication.models.Truck
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {
    private lateinit var registerViewModel: RegisterViewModel
    lateinit var getObjectFamilyProductName : String
    val listFamilyProduct = ArrayList<String>()
    lateinit var getObjectTruckPlate : String
    val listTruckPlate = ArrayList<String>()

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

        nextButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), NextRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        })

        //FAMILIA DE PRODUCTO
        val familyProductService: FamilyProductService = RetrofitClients.getUsersClient().create(FamilyProductService::class.java)

        familyProductService.getFamilyProduct().enqueue(object : Callback<List<FamilyProduct>> {
            override fun onResponse(call: Call<List<FamilyProduct>>, response: Response<List<FamilyProduct>>) {
                if(response.isSuccessful) {
                    Log.i("Success", response.body().toString())
                    try {
                        val mapper = ObjectMapper()
                        val list: List<FamilyProduct>? = response.body()
                        val jsonArray1: String = mapper.writeValueAsString(list)
                        val jsonArray = JSONArray(jsonArray1)
                        var i = 0
                        getObjectFamilyProductName = "u"
                        for(i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            getObjectFamilyProductName=jsonObject.getString("familyProductName")
                            //println(getObjectFamilyProductName)
                            listFamilyProduct.add(getObjectFamilyProductName)

                            println(listFamilyProduct)
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listFamilyProduct)
                        binding.familyAutoComplete.setAdapter(arrayAdapter)
                        /*binding.cargoAutoComplete.setOnItemClickListener { parent, view, position, id ->
                            when (position) {
                                0 -> {
                                    //seleccionar el cargamento para que la info se guarde en ese cargamento y luego presionar el boton iNICIO
                                    //igual para los otros
                                    val intent = Intent(requireContext(), ConnectionActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    startActivity(intent)
                                }
                                1 -> {
                                    val intent = Intent(requireContext(), ConnectionActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    startActivity(intent)
                                }
                                else -> {
                                    val intent = Intent(requireContext(), ConnectionActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    startActivity(intent)
                                }
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
                        val mapper = ObjectMapper()
                        val list: List<Truck>? = response.body()
                        val jsonArray1: String = mapper.writeValueAsString(list)
                        val jsonArray = JSONArray(jsonArray1)
                        var i = 0
                        getObjectTruckPlate = "u"
                        for(i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            getObjectTruckPlate=jsonObject.getString("truckPlate")
                            //println(getObjectFamilyProductName)
                            listTruckPlate.add(getObjectTruckPlate)
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listTruckPlate)
                        binding.camionAutoComplete.setAdapter(arrayAdapter)
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

        //
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}