package com.example.logiapplication.logisticOperator.ui.register

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.carrier.CarrierMainActivity
import com.example.logiapplication.databinding.CarrierActivityConnectionBinding
import com.example.logiapplication.databinding.LogisticNextRegisterActivityBinding
import com.example.logiapplication.logisticOperator.LogisticMainActivity

class NextRegisterActivity : AppCompatActivity() {
    private lateinit var binding: LogisticNextRegisterActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LogisticNextRegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

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
}