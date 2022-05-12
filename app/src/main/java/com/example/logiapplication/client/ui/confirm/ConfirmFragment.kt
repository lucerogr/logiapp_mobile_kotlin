package com.example.logiapplication.client.ui.confirm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.client.ClientMainActivity
import com.example.logiapplication.client.ui.followup.ClientViewRegisterActivity
import com.example.logiapplication.databinding.ClientFragmentConfirmBinding
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.logisticOperator.ui.register.NextRegisterActivity
import com.example.logiapplication.models.Cargo
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmFragment:Fragment() {
    private lateinit var confirmViewModel: ConfirmViewModel
    private var _binding: ClientFragmentConfirmBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var sharedPreferences: SharedPreferences

    var getClientCodigo : Int = 0

    lateinit var tableCargas : TableLayout
    lateinit var tableRowCarga: TableRow

    lateinit var getCargoName : String
    lateinit var getCargoEstado : String
    var getCargoId : Int = 0
    val listCargaId = ArrayList<Int>()
    val listCargaName = ArrayList<String>()
    val listCargaEstado = ArrayList<String>()
    val listConfirmButton = ArrayList<Button>()

    lateinit var botonConfirmar : Button

    companion object {
        var CARGA_CONFIRMADA = "carga_confirmada"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        confirmViewModel =
            ViewModelProvider(this).get(ConfirmViewModel::class.java)

        _binding = ClientFragmentConfirmBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        tableCargas = binding.root.findViewById(R.id.routes_table)

        getClientCodigo = sharedPreferences.getInt(ClientMainActivity.UserCodigo, 0)


        //CARGAS

        val cargoService: CargoService = RetrofitClients.getUsersClient().create(CargoService::class.java)
        cargoService.getCargoByClient(getClientCodigo).enqueue(object : Callback<List<Cargo>> {
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
                            getCargoId=jsonObjectC.getInt("codigo")
                            getCargoName=jsonObjectC.getString("cargoName")
                            getCargoEstado=jsonObjectC.getString("cargoStatus")
                            if (getCargoEstado == "Finalizada") {
                                listCargaId.add(getCargoId)
                                listCargaName.add(getCargoName)
                                listCargaEstado.add(getCargoEstado)
                            }
                        }
                        init()
                        for (i in 0 until tableCargas.childCount-1) {
                            tableRowCarga = tableCargas.getChildAt(i+1) as TableRow
                            botonConfirmar = tableRowCarga.getChildAt(3) as Button
                            botonConfirmar.setOnClickListener(View.OnClickListener {
                                val editor: SharedPreferences.Editor=sharedPreferences.edit()
                                editor.putInt(CARGA_CONFIRMADA, listCargaId[i])
                                editor.apply()
                                val intent = Intent(requireContext(), ConfirmCargoActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                startActivity(intent)
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

    fun init() {
        val row0 = TableRow(requireContext())

        val tv0 = TextView(requireContext())
        tv0.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
        tv0.text = " # "
        tv0.setTextColor(Color.BLACK)
        tv0.setBackgroundColor(Color.LTGRAY)
        tv0.gravity = Gravity.CENTER
        row0.addView(tv0)

        val tv1 = TextView(requireContext())
        tv1.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            2f
        )
        tv1.text = " Carga "
        tv1.setTextColor(Color.BLACK)
        tv1.setBackgroundColor(Color.LTGRAY)
        tv1.gravity = Gravity.CENTER
        row0.addView(tv1)

        val tv2 = TextView(requireContext())
        tv2.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            2f
        )
        tv2.text = " Estado "
        tv2.setTextColor(Color.BLACK)
        tv2.setBackgroundColor(Color.LTGRAY)
        tv2.gravity = Gravity.CENTER
        row0.addView(tv2)

        val tv3 = TextView(requireContext())
        tv3.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            4f
        )
        tv3.text = "  "
        tv3.setTextColor(Color.BLACK)
        tv3.setBackgroundColor(Color.LTGRAY)
        tv3.gravity = Gravity.CENTER
        row0.addView(tv3)

        tableCargas.addView(row0)
        for (i in 0 until listCargaId.size) {
            val tbrow = TableRow(requireContext())
            val lp: TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
            tbrow.layoutParams = lp

            //NUMERO DE CARGA
            val t0v = TextView(requireContext())
            t0v.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            t0v.text = (i+1).toString()
            t0v.gravity = Gravity.CENTER
            t0v.setTextColor(Color.BLACK)
            t0v.setBackgroundColor(Color.WHITE)
            t0v.setPadding(5, 5, 5, 5)
            tbrow.addView(t0v)

            //NOMBRE DE CARGA
            val t1v = TextView(requireContext())
            t1v.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
            )
            t1v.text = listCargaName[i]
            t1v.gravity = Gravity.CENTER
            t1v.setTextColor(Color.BLACK)
            t1v.setBackgroundColor(Color.WHITE)
            t1v.setPadding(5, 5, 5, 5)
            tbrow.addView(t1v)

            //ESTADO DE CARGA
            val t2v = TextView(requireContext())
            t2v.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
            )
            t2v.text = listCargaEstado[i]
            t2v.gravity = Gravity.CENTER
            t2v.setTextColor(Color.BLACK)
            t2v.setBackgroundColor(Color.WHITE)
            t2v.setPadding(5, 5, 5, 5)
            tbrow.addView(t2v)

            //BOTON CONFIRMAR
            val confirmButton= Button(requireContext())
            confirmButton.layoutParams = TableRow.LayoutParams(
                60,
                120,
                4f
            )
            //confirmButton.setPadding(5, 5, 5, 5)
            confirmButton.text = "CONFIRMAR"
            confirmButton.setBackgroundColor(Color.GRAY)
            confirmButton.setTextColor(Color.WHITE)
            //confirmButton.layoutParams = Gravity.CENTER
            listConfirmButton.add(confirmButton)
            tbrow.addView(confirmButton)

            tableCargas.addView(tbrow)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}