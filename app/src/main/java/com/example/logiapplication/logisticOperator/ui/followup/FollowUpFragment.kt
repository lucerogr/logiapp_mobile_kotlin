package com.example.logiapplication.logisticOperator.ui.followup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
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
import com.example.logiapplication.carrier.ui.syncup.ConnectionActivity
import com.example.logiapplication.carrier.ui.time.TimeFragment
import com.example.logiapplication.databinding.LogisticFragmentFollowupBinding
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.interfaces.ProductService
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.logisticOperator.ui.register.NextRegisterActivity
import com.example.logiapplication.models.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowUpFragment : Fragment() {
    private lateinit var followUpViewModel: FollowUpViewModel
    private var _binding: LogisticFragmentFollowupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var sharedPreferences: SharedPreferences

    var getOperatorCodigo : Int = 0

    lateinit var tableCargas : TableLayout
    lateinit var tableRowCarga: TableRow


    lateinit var getCargoName : String
    lateinit var getCargoEstado : String
    var getCargoId : Int = 0
    val listCargaId = ArrayList<Int>()
    val listCargaName = ArrayList<String>()
    val listCargaEstado = ArrayList<String>()
    val listImageView = ArrayList<ImageView>()
    val listImageEdit = ArrayList<ImageView>()
    val listImageCancel = ArrayList<ImageView>()

    val listCargaDate = ArrayList<String>()
    val listCargaHour = ArrayList<String>()
    val listCargaInitialUbication = ArrayList<String>()
    val listCargaFinalUbication = ArrayList<String>()
    val listCargaRouteDuration = ArrayList<String>()
    val listCargaRouteStatus = ArrayList<String>()
    val listcamion = ArrayList<Truck>()
    val listfamproducto = ArrayList<FamilyProduct>()
    val listpersonClientId = ArrayList<Person>()
    val listpersonOperatorId = ArrayList<Person>()
    val listpersonDriverId = ArrayList<Person>()
    val listCargaComments= ArrayList<String>()



    lateinit var itemImageView: ImageView
    lateinit var itemImageEdit: ImageView
    lateinit var itemImageCancel: ImageView

    companion object {
        var CARGA_ID = "carga_id"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        followUpViewModel =
            ViewModelProvider(this).get(FollowUpViewModel::class.java)

        _binding = LogisticFragmentFollowupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)


        tableCargas = binding.root.findViewById(R.id.cargo_list_table)

        getOperatorCodigo = sharedPreferences.getInt(LogisticMainActivity.UserCodigo, 0)

        //println(getOperatorCodigo)
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
                            getCargoId=jsonObjectC.getInt("codigo")
                            getCargoName=jsonObjectC.getString("cargoName")
                            getCargoEstado=jsonObjectC.getString("cargoStatus")
                            listCargaId.add(getCargoId)
                            listCargaName.add(getCargoName)
                            listCargaEstado.add(getCargoEstado)
                            listCargaDate.add(cargoObject.cargoDate)
                            listCargaHour.add(cargoObject.cargoHour)
                            listCargaInitialUbication.add(cargoObject.cargoInitialUbication)
                            listCargaFinalUbication.add(cargoObject.cargoFinalUbication)
                            listCargaRouteDuration.add(cargoObject.cargoRouteDuration)
                            listCargaRouteStatus.add(cargoObject.cargoRouteStatus)
                            listcamion.add(cargoObject.camion)
                            listfamproducto.add(cargoObject.famproducto)
                            listpersonClientId.add(cargoObject.personClientId)
                            listpersonOperatorId.add(cargoObject.personOperatorId)
                            listpersonDriverId.add(cargoObject.personDriverId)
                            listCargaComments.add(cargoObject.cargoComments)
                        }
                        init()
                        for (i in 0 until tableCargas.childCount-1) {
                            tableRowCarga = tableCargas.getChildAt(i+1) as TableRow
                            itemImageView = tableRowCarga.getChildAt(3) as ImageView
                            itemImageEdit = tableRowCarga.getChildAt(4) as ImageView
                            itemImageCancel = tableRowCarga.getChildAt(5) as ImageView
                            itemImageView.setOnClickListener(View.OnClickListener {
                                val editor: SharedPreferences.Editor=sharedPreferences.edit()
                                editor.putInt(CARGA_ID, listCargaId[i])
                                editor.apply()
                                val intent = Intent(requireContext(), ViewRegisterCargoActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                startActivity(intent)
                            })
                            itemImageEdit.setOnClickListener(View.OnClickListener {
                                val editor: SharedPreferences.Editor=sharedPreferences.edit()
                                editor.putInt(CARGA_ID, listCargaId[i])
                                editor.apply()
                                val intent = Intent(requireContext(), ModifyCargoActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                startActivity(intent)
                            })
                            itemImageCancel.setOnClickListener(View.OnClickListener {
                                val cargoData = Cargo(  codigo = listCargaId[i],
                                    cargoName = listCargaName[i],
                                    cargoDate = listCargaDate[i],
                                    cargoHour = listCargaHour[i],
                                    cargoInitialUbication = listCargaInitialUbication[i],
                                    cargoFinalUbication = listCargaFinalUbication[i],
                                    cargoStatus = "Cancelada",
                                    cargoRouteDuration = listCargaRouteDuration[i],
                                    cargoRouteStatus = listCargaRouteStatus[i],
                                    camion = listcamion[i],
                                    famproducto = listfamproducto[i],
                                    personClientId = listpersonClientId[i],
                                    personOperatorId = listpersonOperatorId[i],
                                    personDriverId = listpersonDriverId[i],
                                    cargoComments = listCargaComments[i]
                                )
                                updateCargo(cargoData, listCargaId[i]) {
                                    if (it?.codigo != null) {
                                        Toast.makeText(requireContext(), cargoData.cargoName + " cancelado", Toast.LENGTH_SHORT).show()

                                    } else {
                                        Toast.makeText(requireContext(), "Error al cancelar la carga", Toast.LENGTH_SHORT).show()
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

    fun init() {
        val row0 = TableRow(requireContext())

        val tv0 = TextView(requireContext())
        tv0.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.5f
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
            3f
        )
        tv3.text = "  "
        tv3.setTextColor(Color.BLACK)
        tv3.setBackgroundColor(Color.LTGRAY)
        tv3.gravity = Gravity.CENTER
        row0.addView(tv3)

        val tv4 = TextView(requireContext())
        tv4.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            3f
        )
        tv4.text = "  "
        tv4.setTextColor(Color.BLACK)
        tv4.setBackgroundColor(Color.LTGRAY)
        tv4.gravity = Gravity.CENTER
        row0.addView(tv4)

        val tv5 = TextView(requireContext())
        tv5.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            3f
        )
        tv5.text = "  "
        tv5.setTextColor(Color.BLACK)
        tv5.setBackgroundColor(Color.LTGRAY)
        tv5.gravity = Gravity.CENTER
        row0.addView(tv5)
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
                1.5f
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

            //IMAGEN DE VER CARGA
            val image_view = ImageView(requireContext())
            image_view.layoutParams = TableRow.LayoutParams(
                0,
                (39 * resources.displayMetrics.density).toInt(),
                3f
            )
            //image_view.layoutParams = Gravity.CENTER
            image_view.setBackgroundColor(Color.WHITE)
            image_view.setPadding(5, 5, 5, 5)
            image_view.setImageResource(R.drawable.ver)
            listImageView.add(image_view)
            tbrow.addView(image_view)

            //IMAGEN DE EDITAR CARGA
            val image_edit = ImageView(requireContext())
            image_edit.layoutParams = TableRow.LayoutParams(
                0,
                (39 * resources.displayMetrics.density).toInt(),
                3f
            )
            image_edit.setBackgroundColor(Color.WHITE)
            image_edit.setPadding(5, 5, 5, 5)
            image_edit.setImageResource(R.drawable.editar)
            listImageEdit.add(image_edit)
            tbrow.addView(image_edit)

            //IMAGEN DE CANCELAR CARGA
            val image_cancel = ImageView(requireContext())
            image_cancel.layoutParams = TableRow.LayoutParams(
                0,
                (39 * resources.displayMetrics.density).toInt(),
                3f
            )

            image_cancel.setBackgroundColor(Color.WHITE)
            image_cancel.setPadding(5, 5, 5, 5)
            image_cancel.setImageResource(R.drawable.cerrar)
            listImageCancel.add(image_cancel)
            tbrow.addView(image_cancel)

            tableCargas.addView(tbrow)

        }
    }
}