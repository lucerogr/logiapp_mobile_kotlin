package com.example.logiapplication.logisticOperator.ui.reporting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.databinding.LogisticConditionsActivityBinding
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.interfaces.LogsService
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.logisticOperator.LogisticMainActivity.Companion.UserCodigo
import com.example.logiapplication.models.Cargo
import com.example.logiapplication.models.Logs
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConditionsActivity:AppCompatActivity() {
    private lateinit var binding: LogisticConditionsActivityBinding

    lateinit var graphic : LineChart

    lateinit var tableAlertas : TableLayout
    lateinit var sharedPreferences: SharedPreferences
    lateinit var cargoRAutoComplete : AutoCompleteTextView
    lateinit var parameterAutoComplete : AutoCompleteTextView
    var getOperatorCodigo : Int = 0
    var getCargaId : Int = 0
    lateinit var getCargaNameSelected : String
    val listCargaId = ArrayList<Int>()
    var listCargaName = ArrayList<String>()
    var listParameter = ArrayList<String>()
    val listLogId = ArrayList<Int>()
    var listLogHour = ArrayList<String>()
    var listLogFecha = ArrayList<String>()
    var listLogUbicacion = ArrayList<String>()
    var listLogParametro = ArrayList<String>()
    var listLogs = ArrayList<Logs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LogisticConditionsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        sharedPreferences = this.getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        getOperatorCodigo = sharedPreferences.getInt(UserCodigo, 0)
        cargoRAutoComplete = findViewById(R.id.cargoRAutoComplete)
        parameterAutoComplete = findViewById(R.id.parameterAutoComplete)
        tableAlertas = findViewById(R.id.alerts_table)

        //TABLA DE ALERTAS
        val logsService: LogsService = RetrofitClients.getUsersClient().create(LogsService::class.java)
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
                            }
                        }
                        listParameter.add("Temperatura")
                        listParameter.add("Humedad")
                        listParameter.add("Velocidad")
                        val arrayAdapterP = ArrayAdapter(this@ConditionsActivity, R.layout.dropdown_item, listParameter)
                        parameterAutoComplete.setAdapter(arrayAdapterP)

                        val arrayAdapter = ArrayAdapter(this@ConditionsActivity, R.layout.dropdown_item, listCargaName)
                        cargoRAutoComplete.setAdapter(arrayAdapter)
                        cargoRAutoComplete.setOnItemClickListener { parent, view, position, id ->
                            for (i in 0 until listCargaId.size) {
                                if (position == i) {
                                    getCargaId = listCargaId[i]
                                    getCargaNameSelected = listCargaName[i]
                                    if(parameterAutoComplete.text.isEmpty()) {
                                        Toast.makeText(this@ConditionsActivity, "Seleccione el parámetro antes", Toast.LENGTH_SHORT).show()
                                        cargoRAutoComplete.setText("")
                                    }
                                    else {
                                        //ALERTAS
                                        logsService.getLogsByCargoId(getCargaId).enqueue(object : Callback<List<Logs>> {
                                            override fun onResponse(call: Call<List<Logs>>, response: Response<List<Logs>>) {
                                                if(response.isSuccessful) {
                                                    Log.i("Success", response.body().toString())
                                                    try {
                                                        val mapperL = ObjectMapper()
                                                        val listL: List<Logs>? = response.body()
                                                        val jsonArray1L: String = mapperL.writeValueAsString(listL)
                                                        val jsonArrayL = JSONArray(jsonArray1L)
                                                        for(i in 0 until jsonArrayL.length()) {
                                                            val jsonObjectL: JSONObject = jsonArrayL.getJSONObject(i)
                                                            val logObject = Gson().fromJson(jsonObjectL.toString(), Logs::class.java)
                                                            //lista de todos los logs aunque alert type sea false
                                                            listLogs.add(logObject)
                                                            if(logObject.logCargoAlertType) {
                                                                listLogId.add(jsonObjectL.getInt("codigo"))
                                                                listLogHour.add(logObject.logCargoHour)
                                                                listLogFecha.add(logObject.logCargoDate)
                                                                listLogUbicacion.add(logObject.logCargoUbication)
                                                                when {
                                                                    parameterAutoComplete.text.toString() == "Temperatura" -> {
                                                                        listLogParametro.add(logObject.logCargoTemperature)
                                                                    }
                                                                    parameterAutoComplete.text.toString() == "Humedad" -> {
                                                                        listLogParametro.add(logObject.logCargoHumidity)
                                                                    }
                                                                    parameterAutoComplete.text.toString() == "Velocidad" -> {
                                                                        listLogParametro.add(logObject.logCargoVelocity)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        init()
                                                        setLineGraphicData()
                                                    }
                                                    catch (ex: JSONException){
                                                        ex.printStackTrace()
                                                    }
                                                }
                                            }
                                            override fun onFailure(call: Call<List<Logs>>, t: Throwable) {
                                                Toast.makeText(this@ConditionsActivity, "Error", Toast.LENGTH_SHORT).show()
                                            }
                                        })

                                    }
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
                Toast.makeText(this@ConditionsActivity, "Error", Toast.LENGTH_SHORT).show()
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
        tv0.text = " Nº "
        tv0.setTextColor(Color.BLACK)
        tv0.setBackgroundColor(Color.LTGRAY)
        tv0.gravity = Gravity.CENTER
        row0.addView(tv0)

        val tv1 = TextView(this)
        tv1.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            2f
        )
        tv1.text = " Hora "
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
        tv2.text = " Fecha "
        tv2.setTextColor(Color.BLACK)
        tv2.setBackgroundColor(Color.LTGRAY)
        tv2.gravity = Gravity.CENTER
        row0.addView(tv2)

        val tv3 = TextView(this)
        tv3.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            3f
        )
        tv3.text = " Ubicación "
        tv3.setTextColor(Color.BLACK)
        tv3.setBackgroundColor(Color.LTGRAY)
        tv3.gravity = Gravity.CENTER
        row0.addView(tv3)

        val tv4 = TextView(this)
        tv4.layoutParams = TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            3f
        )
        tv4.text = parameterAutoComplete.text
        tv4.setTextColor(Color.BLACK)
        tv4.setBackgroundColor(Color.LTGRAY)
        tv4.gravity = Gravity.CENTER
        row0.addView(tv4)

        tableAlertas.addView(row0)
        for (i in 0 until listLogId.size) {
            val tbrow = TableRow(this)
            val lp: TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
            tbrow.layoutParams = lp

            //NUMERO DE CARGA
            val t0v = TextView(this)
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

            //HORA DE ALERTA
            val t1v = TextView(this)
            t1v.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
            )
            t1v.text = listLogHour[i]
            t1v.gravity = Gravity.CENTER
            t1v.setTextColor(Color.BLACK)
            t1v.setBackgroundColor(Color.WHITE)
            t1v.setPadding(5, 5, 5, 5)
            tbrow.addView(t1v)

            //FECHA DE ALERTA
            val t2v = TextView(this)
            t2v.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                3f
            )
            t2v.text = listLogFecha[i]
            t2v.gravity = Gravity.CENTER
            t2v.setTextColor(Color.BLACK)
            t2v.setBackgroundColor(Color.WHITE)
            t2v.setPadding(5, 5, 5, 5)
            tbrow.addView(t2v)

            //UBICACION DE ALERTA
            val t3v = TextView(this)
            t3v.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                3f
            )
            t3v.text = listLogUbicacion[i]
            t3v.gravity = Gravity.CENTER
            t3v.setTextColor(Color.BLACK)
            t3v.setBackgroundColor(Color.WHITE)
            t3v.setPadding(5, 5, 5, 5)
            tbrow.addView(t3v)

            //PARAMETRO
            val t4v = TextView(this)
            t4v.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                3f
            )
            t4v.text = listLogParametro[i]
            t4v.gravity = Gravity.CENTER
            t4v.setTextColor(Color.BLACK)
            t4v.setBackgroundColor(Color.WHITE)
            t4v.setPadding(5, 5, 5, 5)
            tbrow.addView(t4v)



            tableAlertas.addView(tbrow)

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
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    fun setLineGraphicData() {
        graphic = binding.root.findViewById(R.id.graphic)

        //EJE Y (PARAMETRO)
        var listParametro = ArrayList<Float>()
        var listMinutos = ArrayList<Int>()
        var listDuracion = ArrayList<Int>()

        for (i in 0 until listLogs.size) {
            val time: List<String> = listLogs[i].cargo.cargoRouteDuration.split(":")
            //time[ultimo de lalista]
            var minute = time[time.size-1].toInt() //minuto = 2
            //si duracion es 0:2, va a recorrer el 1 y 2
            for (i in 1 until minute+1) { //recorre todos los minutos de la lista desde 1
                listDuracion.add(i) //lista con cada minuto [1, 2]
            }

            if (parameterAutoComplete.text.toString() == "Temperatura") {
                listParametro.add(listLogs[i].logCargoTemperature.toFloat())
                listMinutos.add(listDuracion[i])
            }
            else if(parameterAutoComplete.text.toString() == "Humedad"){
                listParametro.add(listLogs[i].logCargoHumidity.toFloat())
                listMinutos.add(listDuracion[i])

            }
            else if(parameterAutoComplete.text.toString() == "Velocidad") {
                listParametro.add(listLogs[i].logCargoVelocity.toFloat())
                listMinutos.add(listDuracion[i])
            }
        }

        //EJE X (MINUTOS)

        /*xValue.add("1")
        xValue.add("2")
        xValue.add("3")
        xValue.add("4")
        xValue.add("5")
        xValue.add("6")
        xValue.add("7")*/

        val xValue = ArrayList<String>()
        xValue.add("0")

        val lineEntry = ArrayList<Entry>()
        lineEntry.add(Entry(0f, 0))
        for (i in 0 until listLogs.size) {
            xValue.add(listMinutos[i].toString())
            lineEntry.add(Entry(listParametro[i], listMinutos[i]))
        }
        /*
        lineEntry.add(Entry(28f, 1))
        lineEntry.add(Entry(23f, 2))
        lineEntry.add(Entry(30f, 3))
        lineEntry.add(Entry(35f, 4))
        lineEntry.add(Entry(29f, 5))
        lineEntry.add(Entry(28f, 6))
        lineEntry.add(Entry(26f, 7))*/

        val xVal = graphic.xAxis
        xVal.position = XAxis.XAxisPosition.BOTTOM

        val yValRight = graphic.axisRight
        yValRight.isEnabled = false
        val yValLeft = graphic.axisLeft
        yValLeft.isGranularityEnabled = true
        yValLeft.granularity = 3f

        val lineDataSet = LineDataSet(lineEntry,  parameterAutoComplete.text.toString() + " de la carga por minutos de ruta")
        lineDataSet.color=resources.getColor(R.color.green)

        lineDataSet.circleRadius = 0f
        lineDataSet.setDrawFilled(true)
        lineDataSet.valueTextColor=resources.getColor(R.color.colorPrimaryDark)
        lineDataSet.fillColor=resources.getColor(R.color.colorPrimaryDark)
        lineDataSet.fillAlpha = 30
        val data = LineData(xValue, lineDataSet)


        graphic.data=data
        graphic.setBackgroundColor(resources.getColor(R.color.white))
        graphic.setDescription("Minutos")
        //graphic.animateXY(3000, 3000)



    }
}