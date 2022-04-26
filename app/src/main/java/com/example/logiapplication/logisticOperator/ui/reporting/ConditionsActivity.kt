package com.example.logiapplication.logisticOperator.ui.reporting

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.databinding.LogisticConditionsActivityBinding
import com.example.logiapplication.logisticOperator.LogisticMainActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.security.KeyStore

class ConditionsActivity:AppCompatActivity() {
    private lateinit var binding: LogisticConditionsActivityBinding

    lateinit var graphic : LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LogisticConditionsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        setLineGraphicData()

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

        //EJE X
        val xValue = ArrayList<String>()
        xValue.add("0")
        xValue.add("1")
        xValue.add("2")
        xValue.add("3")
        xValue.add("4")
        xValue.add("5")
        xValue.add("6")
        xValue.add("7")

        val xVal = graphic.xAxis
        xVal.position = XAxis.XAxisPosition.BOTTOM


        //EJE Y
        val lineEntry = ArrayList<Entry>()
        lineEntry.add(Entry(24f, 0))
        lineEntry.add(Entry(28f, 1))
        lineEntry.add(Entry(23f, 2))
        lineEntry.add(Entry(30f, 3))
        lineEntry.add(Entry(35f, 4))
        lineEntry.add(Entry(29f, 5))
        lineEntry.add(Entry(28f, 6))
        lineEntry.add(Entry(26f, 7))

        val yValRight = graphic.axisRight
        yValRight.isEnabled = false

        val lineDataSet = LineDataSet(lineEntry, "Temperatura de la carga por tiempo de ruta")
        lineDataSet.color=resources.getColor(R.color.green)

        lineDataSet.circleRadius = 0f
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor=resources.getColor(R.color.colorPrimaryDark)
        lineDataSet.fillAlpha = 30

        val data = LineData(xValue, lineDataSet)


        graphic.data=data
        graphic.setBackgroundColor(resources.getColor(R.color.white))

        //graphic.animateXY(3000, 3000)



    }
}