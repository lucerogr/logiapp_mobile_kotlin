package com.example.logiapplication.carrier.ui.syncup

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.carrier.CarrierMainActivity
import com.example.logiapplication.R
import com.example.logiapplication.databinding.CarrierActivityConnectionBinding
import com.google.android.gms.location.*
import ingenieria.jhr.bluetoothjhr.BluetoothJhr
import java.util.*
import kotlin.concurrent.thread

class ConnectionActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: CarrierActivityConnectionBinding
    lateinit var bluetoothJhr: BluetoothJhr
    lateinit var data1: TextView
    lateinit var data2: TextView
    lateinit var data3: TextView
    lateinit var data4: TextView
    lateinit var data6: TextView


    var msj = ""
    var initHilo = false
    var hilo = true
    var boolean: Boolean = true

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


        boolean = true

        bluetoothJhr = BluetoothJhr(this, CarrierMainActivity::class.java)

        thread(start = true) {
            while (!initHilo) {
                Thread.sleep(50)
            }
                while (hilo) {
                    bluetoothJhr.mTx("i")
                    Thread.sleep(100)
                    msj = bluetoothJhr.mRx()
                    if (msj != "") {
                        if (hilo) {
                            runOnUiThread(Runnable {
                                val input: String = msj
                                val lines: List<String> = input.split("\n")
                                data1.text = lines[0]
                                data2.text = lines[1]
                                data3.text = lines[2]
                                data4.text = lines[3]
                            })
                        } else {
                            break
                        }
                        bluetoothJhr.mensajeReset()
                    }
                    Thread.sleep(100)
                }
        }

        doStuff()

        this.updateSpeed(null)

    }

    override fun onResume() {
        super.onResume()
        initHilo = bluetoothJhr.conectaBluetooth()
        doStuff()

        this.updateSpeed(null)
    }

    override fun onPause() {
        super.onPause()
        hilo=false
        initHilo=false
        bluetoothJhr.exitConexion()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        initHilo=true
        hilo=false
    }
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
