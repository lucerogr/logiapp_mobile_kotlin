package com.example.logiapplication.carrier.ui.syncup

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.carrier.BluetoothConfiguration
import com.example.logiapplication.carrier.CLocation
import com.example.logiapplication.carrier.CarrierMainActivity
import com.example.logiapplication.carrier.ui.time.TimeFragment.Companion.CARGO
import com.example.logiapplication.databinding.CarrierActivityConnectionBinding
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.interfaces.LogsService
import com.example.logiapplication.models.Cargo
import com.example.logiapplication.models.Logs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class ConnectionActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: CarrierActivityConnectionBinding
    lateinit var data2: TextView
    lateinit var data4: TextView
    lateinit var data6: TextView


    lateinit var minTemp: TextView
    lateinit var maxTemp: TextView
    lateinit var minHum: TextView
    lateinit var maxHum: TextView
    lateinit var maxVel: TextView

    lateinit var botonFinRuta: Button
    var getCargoId : Int = 0

    lateinit var latitude: String
    lateinit var longitude: String

    var contador : Int = 0
    var minutos : Int = 0
    var segundos : Int = 0
    var horas : Int = 0

    var msj = ""
    var initHilo = false
    var hilo = false
    var boolean: Boolean = true

    var mLocation: Location? = null
    lateinit var logData : Logs
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TEMPERATURE AND HUMIDITY
        binding = CarrierActivityConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        data2 = findViewById(R.id.temperature)
        data4 = findViewById(R.id.humidity)
        data6 = findViewById(R.id.velocity)
        botonFinRuta = findViewById(R.id.btn_fin_ruta)
        minTemp = findViewById(R.id.minT)
        maxTemp = findViewById(R.id.maxT)
        minHum = findViewById(R.id.minH)
        maxHum = findViewById(R.id.maxH)
        maxVel = findViewById(R.id.maxV)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        boolean = true
        getCargoId = intent.getIntExtra(CARGO, 0)

        thread(start = true) {
            while (!initHilo && !hilo) {
                Thread.sleep(50)
            }
            while (!hilo) {
                BluetoothConfiguration.mTx("i")
                //Thread.sleep(100)
                msj = BluetoothConfiguration.mRx()
                if (msj != "") {
                    if (!hilo) {
                        runOnUiThread(Runnable {
                            val input: String = msj
                            val lines: List<String> = input.split("\n")
                            data2.text = lines[1].toDouble().toInt().toString()
                            data4.text = lines[3].toDouble().toInt().toString()

                        })
                        fun String.fullTrim() = trim().replace("\uFEFF", "")
                        //val number = "39.05166667".fullTrim().toDouble()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val fechaActual = dateFormat.format(Date())
                        val dateFormat2 = SimpleDateFormat("HH:mm")
                        val horaActual = dateFormat2.format(Date())

                        getLastLocation()
                        contador++
                        minutos = 0
                        segundos = 0
                        horas = 0
                        val cargoService: CargoService = RetrofitClients.getUsersClient().create(CargoService::class.java)
                        cargoService.getCargo(getCargoId).enqueue(object : Callback<Cargo> {
                            @SuppressLint("SetTextI18n")
                            override fun onResponse(call: Call<Cargo>, response: Response<Cargo>) {
                                if(response.isSuccessful) {
                                    android.util.Log.i("Success", response.body().toString())
                                    try {
                                        val cargo: Cargo? = response.body()
                                        val jsonObjectCargo= JSONObject(Gson().toJson(cargo))
                                        val cargoObject = Gson().fromJson(jsonObjectCargo.toString(), Cargo::class.java)
                                        val minTemperatura = cargoObject.famproducto.familyProductTemperatureMin
                                        val maxTemperatura = cargoObject.famproducto.familyProductTemperatureMax
                                        val minHumedad = cargoObject.famproducto.familyProductHumidityMin
                                        val maxHumedad = cargoObject.famproducto.familyProductHumidityMax

                                        minTemp.text = "$minTemperatura ºC"
                                        maxTemp.text = "$maxTemperatura ºC"
                                        minHum.text = "$minHumedad %"
                                        maxHum.text = "$maxHumedad %"
                                        maxVel.text = "60.0 km/h"

                                        var temp = ""
                                        var hum = ""
                                        var vel = ""
                                        if(contador>=3) {
                                            temp = logData.logCargoTemperature
                                            hum = logData.logCargoHumidity
                                            vel = logData.logCargoVelocity
                                        }


                                        val cargoTemperature = data2.text.toString().fullTrim().toDouble()
                                        val cargoHumidity = data4.text.toString().fullTrim().toDouble()
                                        val cargoVelocidad = data6.text.toString().fullTrim().toDouble()
                                        var cargoAlert = "0"
                                        if(cargoTemperature < minTemperatura || cargoTemperature > maxTemperatura) {
                                            cargoAlert = "1"
                                            data2.setBackgroundResource(R.drawable.box_red)
                                        }
                                        if(cargoTemperature >= (maxTemperatura - 2) && cargoTemperature <= maxTemperatura) {
                                            data2.setBackgroundResource(R.drawable.box_yellow)
                                        }
                                        if(cargoTemperature >= minTemperatura && cargoTemperature < (maxTemperatura-2)){
                                            data4.setBackgroundResource(R.drawable.box_green)
                                        }
                                        if(cargoHumidity < minHumedad || cargoHumidity > maxHumedad) {
                                            cargoAlert = "2"
                                            data4.setBackgroundResource(R.drawable.box_red)
                                        }
                                        if(cargoHumidity >= (maxHumedad - 2) && cargoHumidity <= maxHumedad) {
                                            data4.setBackgroundResource(R.drawable.box_yellow)
                                        }
                                        if(cargoHumidity >= minHumedad && cargoHumidity < (maxHumedad-2)){
                                            data4.setBackgroundResource(R.drawable.box_green)
                                        }
                                        if(cargoVelocidad > 60.0) {
                                            cargoAlert = "3"
                                            data6.setBackgroundResource(R.drawable.box_red)
                                        }
                                        if(cargoVelocidad > 50.0 && cargoVelocidad <= 60.0) {
                                            data6.setBackgroundResource(R.drawable.box_yellow)
                                        }
                                        if(cargoVelocidad <= 50.0) {
                                            data6.setBackgroundResource(R.drawable.box_green)
                                        }



                                        logData = Logs(  codigo = null,
                                            logCargoDate = fechaActual.toString(),
                                            logCargoHour = horaActual.toString(),
                                            logCargoUbication = "$latitude, $longitude",
                                            logCargoTemperature = cargoTemperature.toString(),
                                            logCargoHumidity = cargoHumidity.toString(),
                                            logCargoVelocity = cargoVelocidad.toString(),
                                            logCargoAlertType = cargoAlert,
                                            cargo = cargoObject
                                        )

                                        //CUANDO EL ENTERO VARIA SE GUARDA EL LOG
                                        if(contador>=3) {
                                            if ((cargoTemperature.toString()!=temp && cargoAlert=="1") || (cargoHumidity.toString()!=hum && cargoAlert=="2") || (cargoVelocidad.toInt().toString()!=vel.toDouble().toInt().toString() && cargoAlert=="3")) {
                                                addLog(logData) {
                                                    Toast.makeText(applicationContext, "ALERTA GENERADA", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                        //POR CADA MINUTO SE GUARDA EL LOG
                                        if(contador%20 == 0){ //cada minuto
                                            addLog(logData) {
                                                println("minute")
                                                //Toast.makeText(applicationContext, "LOG GUARDADO", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        botonFinRuta.setOnClickListener(View.OnClickListener{
                                            segundos = contador*3
                                            minutos = segundos / 60
                                            horas = minutos / 60
                                            minutos %= 60

                                            val cargoData = Cargo(  codigo = cargoObject.codigo,
                                                cargoName = cargoObject.cargoName,
                                                cargoDate = cargoObject.cargoDate,
                                                cargoHour = cargoObject.cargoHour,
                                                cargoInitialUbication = cargoObject.cargoInitialUbication,
                                                cargoFinalUbication = cargoObject.cargoFinalUbication,
                                                cargoStatus = "Finalizada",
                                                cargoRouteDuration = "$horas:$minutos",
                                                cargoRouteStatus = cargoObject.cargoRouteStatus,
                                                camion = cargoObject.camion,
                                                famproducto = cargoObject.famproducto,
                                                personClientId = cargoObject.personClientId,
                                                personOperatorId = cargoObject.personOperatorId,
                                                personDriverId = cargoObject.personDriverId,
                                                cargoComments = cargoObject.cargoComments
                                            )

                                            updateCargo(cargoData, getCargoId) {
                                                if (it?.codigo != null) {
                                                    Toast.makeText(this@ConnectionActivity, "Se finalizó ruta de carga", Toast.LENGTH_SHORT).show()
                                                    val intent = Intent(this@ConnectionActivity, CarrierMainActivity::class.java)
                                                    //
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                                    startActivity(intent)

                                                } else {
                                                    Toast.makeText(this@ConnectionActivity, "Error al finalizar ruta de carga", Toast.LENGTH_SHORT).show()
                                                }
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

                    } else {
                        break
                    }

                    BluetoothConfiguration.mensajeReset()
                }
                Thread.sleep(100)
            }
        }
        doStuff()

        this.updateSpeed(null)

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

    @SuppressLint("MissingPermission")
    fun getLastLocation(){
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                mLocation = location
                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                }
            }
    }

    fun addLog(logData: Logs, onResult: (Logs?) -> Unit){
        val logService: LogsService = RetrofitClients.getUsersClient().create(LogsService::class.java)
        logService.addLog(logData).enqueue(object : Callback<Logs> {
            override fun onResponse(call: Call<Logs>, response: Response<Logs>) {
                val addedLog = response.body()
                onResult(addedLog)
            }
            override fun onFailure(call: Call<Logs>, t: Throwable) {
                onResult(null)
            }
        })
    }

    //esto cambiar por bluetoothJhr
    override fun onResume() {
        super.onResume()
        initHilo = BluetoothConfiguration.conectaBluetooth()
        //esto
        /*if(!BluetoothConfiguration.inicioConexion){
            BluetoothConfiguration.conectaBluetooth()
        }*/
        //Esto
        doStuff()

        this.updateSpeed(null)
    }

    override fun onPause() {
        super.onPause()
        hilo=true
        initHilo=false
        BluetoothConfiguration.exitConexion()
        finish()
    }

    /*override fun onDestroy() {
        super.onDestroy()
        //initHilo=true
        hilo=false
    }*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.carrier_main, menu)
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
        val intent = Intent(this, CarrierMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }
    fun goToLoginActivity() {
        val alerta: AlertDialog.Builder = AlertDialog.Builder(this)
        alerta.setMessage("¿Desea salir de la aplicación?")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, which ->
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

    override fun onLocationChanged(location: Location) {
        if (location != null) {
            val myLocation = CLocation(location, useMetricUnits())
            updateSpeed(myLocation)
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    @SuppressLint("MissingPermission")
    fun doStuff() {
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        Toast.makeText(this, "Esperando por conexion GPS", Toast.LENGTH_SHORT).show()
    }

    private fun updateSpeed(location: CLocation?) {
        var nCurrentSpeed = 0f
        if (location != null) {
            location.setUseMetricunits(useMetricUnits())
            nCurrentSpeed = location.speed
        }
        val fmt = Formatter(java.lang.StringBuilder())
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed)
        var strCurrentSpeed = fmt.toString()
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0")
        if (useMetricUnits()) {
            data6.setText("$strCurrentSpeed")
        }
    }

    private fun useMetricUnits(): Boolean {
        return true
    }

}
