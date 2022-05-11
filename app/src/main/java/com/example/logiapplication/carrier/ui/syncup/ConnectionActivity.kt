package com.example.logiapplication.carrier.ui.syncup

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.carrier.CarrierMainActivity
import com.example.logiapplication.R
import com.example.logiapplication.RetrofitClients
import com.example.logiapplication.carrier.BluetoothConfiguration
import com.example.logiapplication.carrier.CLocation
import com.example.logiapplication.carrier.ui.time.TimeFragment.Companion.CARGO
import com.example.logiapplication.databinding.CarrierActivityConnectionBinding
import com.example.logiapplication.interfaces.CargoService
import com.example.logiapplication.interfaces.LogService
import com.example.logiapplication.models.Cargo
import com.example.logiapplication.models.Log
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
    lateinit var data1: TextView
    lateinit var data2: TextView
    lateinit var data3: TextView
    lateinit var data4: TextView
    lateinit var data6: TextView
    var getCargoId : Int = 0

    lateinit var latitude: String
    lateinit var longitude: String

    var contador : Int = 0

    var msj = ""
    var initHilo = false
    var hilo = false
    var boolean: Boolean = true

    var mLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TEMPERATURE AND HUMIDITY
        binding = CarrierActivityConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        data1 = findViewById(R.id.tv_temperature)
        data2 = findViewById(R.id.temperature)
        data3 = findViewById(R.id.tv_humidity)
        data4 = findViewById(R.id.humidity)
        data6 = findViewById(R.id.velocity)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        boolean = true
        getCargoId = intent.getIntExtra(CARGO, 0)

        //bluetoothJhr = BluetoothJhr(this, CarrierMainActivity::class.java)

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
                            data1.text = lines[0]
                            data2.text = lines[1]
                            data3.text = lines[2]
                            data4.text = lines[3]

                        })
                        //fun String.fullTrim() = trim().replace("\uFEFF", "")
                        //val number = "39.05166667".fullTrim().toDouble()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val fechaActual = dateFormat.format(Date())
                        val dateFormat2 = SimpleDateFormat("HH:mm")
                        val horaActual = dateFormat2.format(Date())

                        getLastLocation()
                        contador++

                        val cargoService: CargoService = RetrofitClients.getUsersClient().create(CargoService::class.java)
                        cargoService.getCargo(getCargoId).enqueue(object : Callback<Cargo> {
                            override fun onResponse(call: Call<Cargo>, response: Response<Cargo>) {
                                if(response.isSuccessful) {
                                    android.util.Log.i("Success", response.body().toString())
                                    try {
                                        val cargo: Cargo? = response.body()
                                        val jsonObjectCargo= JSONObject(Gson().toJson(cargo))
                                        val cargoObject = Gson().fromJson(jsonObjectCargo.toString(), Cargo::class.java)
                                        val logData = Log(  codigo = null,
                                            logCargoDate = fechaActual.toString(),
                                            logCargoHour = horaActual.toString(),
                                            logCargoUbication = "$latitude, $longitude",
                                            logCargoTemperature = data2.text.toString(),
                                            logCargoHumidity = data4.text.toString(),
                                            logCargoVelocity = data6.text.toString(),
                                            logCargoAlertType = 0,
                                            cargo = cargoObject
                                        )
                                        if(contador%20 == 0){
                                            addLog(logData) {
                                                Toast.makeText(applicationContext, "Se registró el log", Toast.LENGTH_SHORT).show()
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

    fun addLog(logData: Log, onResult: (Log?) -> Unit){
        val logService: LogService = RetrofitClients.getUsersClient().create(LogService::class.java)
        logService.addLog(logData).enqueue(object : Callback<Log> {
            override fun onResponse(call: Call<Log>, response: Response<Log>) {
                val addedLog = response.body()
                onResult(addedLog)
            }
            override fun onFailure(call: Call<Log>, t: Throwable) {
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
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
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
            data6.setText("$strCurrentSpeed km/h")
        }
    }

    private fun useMetricUnits(): Boolean {
        return true
    }

}
