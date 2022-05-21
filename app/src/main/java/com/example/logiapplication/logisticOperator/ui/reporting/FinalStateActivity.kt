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
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.databinding.LogisticFinalStateActivityBinding
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.example.logiapplication.models.Cargo
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
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


class FinalStateActivity:AppCompatActivity() {
    private lateinit var binding: LogisticFinalStateActivityBinding
    lateinit var sharedPreferences: SharedPreferences

    var getCargaId : Int = 0
    lateinit var getCargaNameSelected : String
    val listCargaId = ArrayList<Int>()
    var listCargaName = ArrayList<String>()
    var listTransportista= ArrayList<String>()
    var listCliente= ArrayList<String>()
    var listDuracion = ArrayList<String>()
    var listEstado = ArrayList<String>()
    var listComentario= ArrayList<String>()

    lateinit var cargaAutoComplete : AutoCompleteTextView
    lateinit var cargoTransportista : TextView
    lateinit var cargoCliente : TextView
    lateinit var cargoDuracion : TextView
    lateinit var cargoEstado : TextView
    lateinit var cargoComentario : TextView

    var getOperatorCodigo : Int = 0

    lateinit var layoutFS: ConstraintLayout
    private val STORAGE_CODE = 1001
    lateinit var botonExportarFinal : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LogisticFinalStateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        sharedPreferences = this.getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        getOperatorCodigo = sharedPreferences.getInt(LogisticMainActivity.UserCodigo, 0)
        cargaAutoComplete = findViewById(R.id.cargamentoAutoComplete)
        cargoTransportista = findViewById(R.id.tv_transportista_text)
        cargoCliente = findViewById(R.id.tv_cliente_text)
        cargoDuracion = findViewById(R.id.tv_duracion_text)
        cargoEstado = findViewById(R.id.tv_estado_text)
        cargoComentario = findViewById(R.id.tv_comentario_text)

        botonExportarFinal = findViewById(R.id.btn_export_final)

        layoutFS = findViewById(R.id.finalStateLayout)

        botonExportarFinal.setOnClickListener{
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
                            if(cargoObject.cargoStatus == "Confirmada") {
                                listCargaId.add(jsonObjectC.getInt("codigo"))
                                listCargaName.add(cargoObject.cargoName)
                                listTransportista.add(cargoObject.personDriverId.personName + " " + cargoObject.personDriverId.personLastName)
                                listCliente.add(cargoObject.personClientId.personName + " " + cargoObject.personClientId.personLastName)
                                val time: List<String> = cargoObject.cargoRouteDuration.split(":")
                                listDuracion.add(time[0] + " horas  " + time[1] + " min")
                                listEstado.add(cargoObject.cargoRouteStatus)
                                listComentario.add(cargoObject.cargoComments)

                            }
                        }
                        val arrayAdapter = ArrayAdapter(this@FinalStateActivity, R.layout.dropdown_item, listCargaName)
                        cargaAutoComplete.setAdapter(arrayAdapter)
                        cargaAutoComplete.setOnItemClickListener { parent, view, position, id ->
                            for (i in 0 until listCargaId.size) {
                                if (position == i) {
                                    getCargaId = listCargaId[i]
                                    getCargaNameSelected = listCargaName[i]

                                    cargoTransportista.text = listTransportista[i]
                                    cargoCliente.text = listCliente[i]
                                    cargoDuracion.text = listDuracion[i]
                                    cargoEstado.text = listEstado[i]
                                    cargoComentario.text = listComentario[i]


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
                Toast.makeText(this@FinalStateActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })

    }

    
    private fun exportPDF() {
        val doc = Document()
        //se guarda en descargas
        val fileName = SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName + ".pdf"

        try {
            PdfWriter.getInstance(doc, FileOutputStream(filePath))
            doc.open()

            val boldFont = Font(Font.FontFamily.TIMES_ROMAN, 22f, Font.BOLD)
            val semiBoldFont = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)

            val table = PdfPTable(2)
            table.addCell("Cargamento")
            table.addCell(cargaAutoComplete.text.toString())
            table.addCell("Transportista")
            table.addCell(cargoTransportista.text.toString())
            table.addCell("Cliente")
            table.addCell(cargoCliente.text.toString())
            table.addCell("Duraciòn de ruta")
            table.addCell(cargoDuracion.text.toString())
            table.addCell("Estado de carga")
            table.addCell(cargoEstado.text.toString())
            table.addCell("Comentario")
            table.addCell(cargoComentario.text.toString())

            doc.addAuthor("LogiApp")
            val titulo = Paragraph(Chunk("Estado final de carga", boldFont))
            titulo.alignment = Paragraph.ALIGN_CENTER
            doc.add(titulo)
            doc.add(Paragraph(" "))
            doc.add(table)
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