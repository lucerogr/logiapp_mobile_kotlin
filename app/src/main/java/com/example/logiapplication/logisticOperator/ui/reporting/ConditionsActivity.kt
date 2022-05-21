package com.example.logiapplication.logisticOperator.ui.reporting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
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
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfWriter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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

    private val STORAGE_CODE = 1001
    lateinit var botonExportar : Button

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
        botonExportar = findViewById(R.id.btn_export)


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
                                                            println(logObject.logCargoTemperature)
                                                            println(jsonObjectL.getString("logCargoAlertType"))
                                                            println(logObject.logCargoAlertType)
                                                            if(parameterAutoComplete.text.toString() == "Temperatura" && logObject.logCargoAlertType == "1") {
                                                                listLogId.add(jsonObjectL.getInt("codigo"))
                                                                listLogHour.add(logObject.logCargoHour)
                                                                listLogFecha.add(logObject.logCargoDate)
                                                                listLogUbicacion.add(logObject.logCargoUbication)
                                                                listLogParametro.add(logObject.logCargoTemperature)
                                                                /*when {
                                                                    parameterAutoComplete.text.toString() == "Temperatura" -> {
                                                                        listLogParametro.add(logObject.logCargoTemperature)
                                                                    }
                                                                    parameterAutoComplete.text.toString() == "Humedad" -> {
                                                                        listLogParametro.add(logObject.logCargoHumidity)
                                                                    }
                                                                    parameterAutoComplete.text.toString() == "Velocidad" -> {
                                                                        listLogParametro.add(logObject.logCargoVelocity)
                                                                    }
                                                                }*/
                                                            } else if(parameterAutoComplete.text.toString() == "Humedad" && logObject.logCargoAlertType == "2") {
                                                                listLogId.add(jsonObjectL.getInt("codigo"))
                                                                listLogHour.add(logObject.logCargoHour)
                                                                listLogFecha.add(logObject.logCargoDate)
                                                                listLogUbicacion.add(logObject.logCargoUbication)
                                                                listLogParametro.add(logObject.logCargoHumidity)

                                                            } else if(parameterAutoComplete.text.toString() == "Velocidad" && logObject.logCargoAlertType == "3") {
                                                                listLogId.add(jsonObjectL.getInt("codigo"))
                                                                listLogHour.add(logObject.logCargoHour)
                                                                listLogFecha.add(logObject.logCargoDate)
                                                                listLogUbicacion.add(logObject.logCargoUbication)
                                                                listLogParametro.add(logObject.logCargoVelocity)
                                                            }
                                                        }
                                                        println(listLogParametro.size)
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

        botonExportar.setOnClickListener{
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, STORAGE_CODE)
                }
                else {
                    exportPDF()
                }
            }
            else {
                exportPDF()
            }
        }

    }
    private fun exportPDF() {
        val doc = Document()
        //se guarda en descargas
        val fileName = SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName + ".pdf"

        try {
            PdfWriter.getInstance(doc, FileOutputStream(filePath))

            doc.open()
            val streamG = ByteArrayOutputStream()
            val streamT = ByteArrayOutputStream()
            //grafico
            val bitmapG: Bitmap = graphic.chartBitmap
            bitmapG.compress(Bitmap.CompressFormat.JPEG, 100, streamG)
            val myGraph: Image = Image.getInstance(streamG.toByteArray())
            myGraph.scalePercent(50F)
            myGraph.alignment = Image.MIDDLE
            //tabla
            val bitmapT : Bitmap = tableAlertas.drawToBitmap()
            bitmapT.compress(Bitmap.CompressFormat.JPEG, 100, streamT)
            val myTable : Image = Image.getInstance(streamT.toByteArray())

            //myTable.scaleToFit(PageSize.A4.width, PageSize.A4.height)
            myTable.scalePercent(50f)
            myTable.alignment = Image.MIDDLE
            val boldFont = Font(Font.FontFamily.TIMES_ROMAN, 22f, Font.BOLD)
            val semiBoldFont = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
            val titulo = Paragraph(Chunk("Condiciones de Carga", boldFont))
            titulo.alignment = Paragraph.ALIGN_CENTER
            doc.add(titulo)
            doc.add(Paragraph(Chunk(getCargaNameSelected, semiBoldFont)))
            doc.add(myGraph)
            doc.newPage()
            doc.add(Paragraph(Chunk("Alertas", semiBoldFont)))
            doc.add(myTable)
            doc.addAuthor("LogiApp")
            doc.close()
            Toast.makeText(this,"$fileName.pdf\n se ha creado en \n$filePath", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, ""+e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            STORAGE_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportPDF()
                } else{
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun init() {
        if (listLogId.size == 0) {
            val row0 = TableRow(this)

            val tv0 = TextView(this)
            tv0.layoutParams = TableRow.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
            tv0.text = "No se reportaron alertas"
            tv0.setTextColor(Color.BLACK)
            tv0.setBackgroundColor(Color.LTGRAY)
            tv0.gravity = Gravity.CENTER
            row0.addView(tv0)

            tableAlertas.addView(row0)
        }
        else {
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

    fun setLineGraphicData() {
        graphic = binding.root.findViewById(R.id.graphic)

        //EJE Y (PARAMETRO)
        var listParametro = ArrayList<Float>()
        var listMinutos = ArrayList<Int>()
        var listDuracion = ArrayList<Int>()


        //desde aca
        val time: List<String> = listLogs[0].cargo.cargoRouteDuration.split(":") //obtengo la duracion
        var minute = time[time.size-1].toInt() //minuto = 2 ...obtengo los minutos
        for (i in 0 until minute+1) { //recorre todos los minutos de la lista desde 1
            listDuracion.add(i) //lista con cada minuto [0, 1, 2]
        }

        //recorrer list duracion creo
        // si es 0, toma el primer minuto, si es 1, el segundo, si es 2, el tercero
        //mas abajo en listLogs, tambien cambiar
        for (i in 0 until listLogs.size) {
            if (parameterAutoComplete.text.toString() == "Temperatura") {
                listParametro.add(listLogs[i].logCargoTemperature.toFloat())
                val time: List<String> = listLogs[i].logCargoHour.split(":")
                var minute = time[time.size-1].toInt()
                if (listMinutos.isEmpty()) {
                    listMinutos.add(minute)
                }
                else if(minute != listMinutos[listMinutos.size-1]){
                    listMinutos.add(minute)
                }

            }
            else if(parameterAutoComplete.text.toString() == "Humedad"){
                listParametro.add(listLogs[i].logCargoHumidity.toFloat())
                val time: List<String> = listLogs[i].logCargoHour.split(":")
                var minute = time[time.size-1].toInt()
                if (listMinutos.isEmpty()) {
                    listMinutos.add(minute)
                }
                else if(minute != listMinutos[listMinutos.size-1]){
                    listMinutos.add(minute)
                }

            }
            else if(parameterAutoComplete.text.toString() == "Velocidad") {
                listParametro.add(listLogs[i].logCargoVelocity.toFloat())
                val time: List<String> = listLogs[i].logCargoHour.split(":")
                var minute = time[time.size-1].toInt()
                if (listMinutos.isEmpty()) {
                    listMinutos.add(minute)
                }
                else if(minute != listMinutos[listMinutos.size-1]){
                    listMinutos.add(minute)
                }
            }
        }

        //hasta aca
        /*for (i in 0 until listLogs.size) {
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
        }*/

        //EJE X (MINUTOS)


        val xValue = ArrayList<String>()
        /*xValue.add("0")
        xValue.add("1")
        xValue.add("2")
        xValue.add("3")
        xValue.add("4")

        val lineEntry = ArrayList<Entry>()
        lineEntry.add(Entry(19f, 0))
        lineEntry.add(Entry(19f, 1))
        lineEntry.add(Entry(8f, 2))
        lineEntry.add(Entry(6f, 2))
        lineEntry.add(Entry(8f, 2))*/

        val lineEntry = ArrayList<Entry>()
        for (i in 0 until listDuracion.size) {
            for (j in 0 until listLogs.size) {
                xValue.add(j.toString())
                val t: List<String> = listLogs[j].logCargoHour.split(":")
                var m = t[t.size - 1].toInt()
                if (m == listMinutos[i]) {
                    lineEntry.add(Entry(listParametro[j], i))
                }
            }
        }

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